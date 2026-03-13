package com.ecolink.server.service;

import com.ecolink.server.domain.Address;
import com.ecolink.server.domain.CartItem;
import com.ecolink.server.domain.Order;
import com.ecolink.server.domain.OrderItem;
import com.ecolink.server.domain.User;
import com.ecolink.server.domain.enums.OrderStatus;
import com.ecolink.server.dto.order.CreateOrderRequest;
import com.ecolink.server.exception.BizException;
import com.ecolink.server.repository.OrderItemRepository;
import com.ecolink.server.repository.OrderRepository;
import com.ecolink.server.repository.OrderStatusLogRepository;
import com.ecolink.server.support.TestSecurityContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private OrderStatusLogRepository orderStatusLogRepository;
    @Mock
    private AuthService authService;
    @Mock
    private AddressService addressService;
    @Mock
    private CartService cartService;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        TestSecurityContext.mockUser(1L);
    }

    @AfterEach
    void tearDown() {
        TestSecurityContext.clear();
    }

    @Test
    void createOrderShouldReduceStockIncreaseSalesAndRemoveCartItems() {
        User user = buildUser(1L);
        Address address = buildAddress(user);
        CartItem cartItem = buildCartItem(user, 10L, "有机苹果", 2, 12, 8, BigDecimal.valueOf(29.9));
        OrderItem savedItem = buildOrderItem(cartItem);

        when(addressService.findByIdForCurrentUser(101L)).thenReturn(address);
        when(cartService.findItemsForCurrentUser(List.of(501L))).thenReturn(List.of(cartItem));
        when(authService.getCurrentUserEntity()).thenReturn(user);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(200L);
            return order;
        });
        when(orderItemRepository.findByOrderIdOrderByIdAsc(200L)).thenReturn(List.of(savedItem));

        var response = orderService.createOrder(new CreateOrderRequest(101L, List.of(501L)));

        assertThat(response.status()).isEqualTo(OrderStatus.UNPAID);
        assertThat(response.totalAmount()).isEqualByComparingTo("59.8");
        assertThat(cartItem.getProduct().getStock()).isEqualTo(10);
        assertThat(cartItem.getProduct().getSales()).isEqualTo(10);
        verify(cartService).removeItems(List.of(cartItem));
        verify(orderStatusLogRepository).save(any());
    }

    @Test
    void createOrderShouldRejectWhenStockNotEnough() {
        User user = buildUser(1L);
        Address address = buildAddress(user);
        CartItem cartItem = buildCartItem(user, 10L, "有机苹果", 5, 1, 8, BigDecimal.valueOf(29.9));

        when(addressService.findByIdForCurrentUser(101L)).thenReturn(address);
        when(cartService.findItemsForCurrentUser(List.of(501L))).thenReturn(List.of(cartItem));

        BizException exception = assertThrows(BizException.class, () -> orderService.createOrder(new CreateOrderRequest(101L, List.of(501L))));

        assertThat(exception.getCode()).isEqualTo(4005);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void payShouldUpdateStatusAndWritePaidTime() {
        Order order = buildOrder(300L, OrderStatus.UNPAID);
        OrderItem item = buildOrderItem(buildCartItem(buildUser(1L), 10L, "有机苹果", 2, 10, 8, BigDecimal.valueOf(29.9)));

        when(orderRepository.findByIdAndUserId(300L, 1L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findByOrderIdOrderByIdAsc(300L)).thenReturn(List.of(item));

        var response = orderService.pay(300L);

        assertThat(response.status()).isEqualTo(OrderStatus.PAID);
        assertThat(order.getPaidAt()).isNotNull();
        verify(orderStatusLogRepository).save(any());
    }

    @Test
    void autoFlowShouldMovePaidAndShippedOrdersForward() {
        Order paidOrder = buildOrder(401L, OrderStatus.PAID);
        paidOrder.setPaidAt(LocalDateTime.now().minusSeconds(10));
        Order shippedOrder = buildOrder(402L, OrderStatus.SHIPPED);
        shippedOrder.setShippedAt(LocalDateTime.now().minusSeconds(10));

        when(orderRepository.findByStatusAndPaidAtBefore(any(OrderStatus.class), any(LocalDateTime.class))).thenReturn(List.of(paidOrder));
        when(orderRepository.findByStatusAndShippedAtBefore(any(OrderStatus.class), any(LocalDateTime.class))).thenReturn(List.of(shippedOrder));

        orderService.autoFlow();

        assertThat(paidOrder.getStatus()).isEqualTo(OrderStatus.SHIPPED);
        assertThat(paidOrder.getShippedAt()).isNotNull();
        assertThat(shippedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(shippedOrder.getCompletedAt()).isNotNull();
        verify(orderStatusLogRepository, times(2)).save(any());
    }

    private User buildUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setUsername("demo");
        user.setNickname("演示用户");
        return user;
    }

    private Address buildAddress(User user) {
        Address address = new Address();
        address.setId(101L);
        address.setUser(user);
        address.setReceiverName("张三");
        address.setReceiverPhone("13800000000");
        address.setDetail("上海市浦东新区演示路 1 号");
        return address;
    }

    private CartItem buildCartItem(User user, Long productId, String name, int quantity, int stock, int sales, BigDecimal price) {
        CartItem cartItem = new CartItem();
        cartItem.setId(501L);
        cartItem.setUser(user);
        cartItem.setQuantity(quantity);

        var product = new com.ecolink.server.domain.Product();
        product.setId(productId);
        product.setName(name);
        product.setMainImage("https://example.com/product.jpg");
        product.setPrice(price);
        product.setStock(stock);
        product.setSales(sales);
        cartItem.setProduct(product);
        return cartItem;
    }

    private OrderItem buildOrderItem(CartItem cartItem) {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(601L);
        orderItem.setOrder(buildOrder(200L, OrderStatus.UNPAID));
        orderItem.setProduct(cartItem.getProduct());
        orderItem.setProductName(cartItem.getProduct().getName());
        orderItem.setProductImage(cartItem.getProduct().getMainImage());
        orderItem.setSalePrice(cartItem.getProduct().getPrice());
        orderItem.setQuantity(cartItem.getQuantity());
        return orderItem;
    }

    private Order buildOrder(Long id, OrderStatus status) {
        Order order = new Order();
        order.setId(id);
        order.setOrderNo("ECO202603130001");
        order.setStatus(status);
        order.setTotalAmount(BigDecimal.valueOf(59.8));
        order.setReceiverName("张三");
        order.setReceiverPhone("13800000000");
        order.setReceiverAddress("上海市浦东新区演示路 1 号");
        order.setCreatedAt(LocalDateTime.now().minusMinutes(5));
        return order;
    }
}
