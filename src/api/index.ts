import http from './http';
import type {
  Address,
  AuthResult,
  CartData,
  Category,
  FavoriteItem,
  OrderData,
  PageResult,
  ProductDetail,
  ProductItem,
  UserMe,
} from '@/types/api';

export const authApi = {
  register(payload: { username: string; password: string; nickname: string; phone?: string }) {
    return http.post<AuthResult>('/auth/register', payload);
  },
  login(payload: { username: string; password: string }) {
    return http.post<AuthResult>('/auth/login', payload);
  },
  me() {
    return http.get<UserMe>('/users/me');
  },
};

export const productApi = {
  categories() {
    return http.get<Category[]>('/categories');
  },
  list(params: {
    keyword?: string;
    categoryId?: number;
    minPrice?: number;
    maxPrice?: number;
    sort?: string;
    page?: number;
    size?: number;
  }) {
    return http.get<PageResult<ProductItem>>('/products', params);
  },
  detail(id: number) {
    return http.get<ProductDetail>(`/products/${id}`);
  },
};

export const cartApi = {
  list() {
    return http.get<CartData>('/cart');
  },
  add(payload: { productId: number; quantity: number }) {
    return http.post('/cart/items', payload);
  },
  update(itemId: number, payload: { quantity: number }) {
    return http.put(`/cart/items/${itemId}`, payload);
  },
  remove(itemId: number) {
    return http.delete(`/cart/items/${itemId}`);
  },
};

export const orderApi = {
  create(payload: { addressId: number; cartItemIds: number[] }) {
    return http.post<OrderData>('/orders', payload);
  },
  list() {
    return http.get<OrderData[]>('/orders');
  },
  detail(id: number) {
    return http.get<OrderData>(`/orders/${id}`);
  },
  pay(id: number) {
    return http.post<OrderData>(`/orders/${id}/pay`);
  },
};

export const addressApi = {
  list() {
    return http.get<Address[]>('/addresses');
  },
  create(payload: Omit<Address, 'id'>) {
    return http.post<Address>('/addresses', payload);
  },
  update(id: number, payload: Omit<Address, 'id'>) {
    return http.put<Address>(`/addresses/${id}`, payload);
  },
  remove(id: number) {
    return http.delete(`/addresses/${id}`);
  },
};

export const favoriteApi = {
  list() {
    return http.get<FavoriteItem[]>('/favorites');
  },
  add(productId: number) {
    return http.post(`/favorites/${productId}`);
  },
  remove(productId: number) {
    return http.delete(`/favorites/${productId}`);
  },
};
