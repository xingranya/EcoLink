# 数据库 ER 说明（MVP）

## 核心实体

- `users`：用户主表。
- `categories`：商品分类。
- `products`：商品主表，关联 `categories`。
- `product_images`：商品图片表，关联 `products`。
- `favorites`：用户收藏关系，唯一索引 `(user_id, product_id)`。
- `addresses`：用户地址。
- `cart_items`：购物车条目，唯一索引 `(user_id, product_id)`。
- `orders`：订单主表。
- `order_items`：订单明细，保留下单时快照。
- `order_status_logs`：订单状态流转日志。

## 关键关系

- `products.category_id -> categories.id`
- `favorites.user_id -> users.id`
- `favorites.product_id -> products.id`
- `addresses.user_id -> users.id`
- `cart_items.user_id -> users.id`
- `cart_items.product_id -> products.id`
- `orders.user_id -> users.id`
- `order_items.order_id -> orders.id`
- `order_items.product_id -> products.id`
- `order_status_logs.order_id -> orders.id`

## 索引

- `idx_products_category_status_price(category_id, status, price)`
- `idx_orders_user_created_at(user_id, created_at)`
- `idx_cart_items_user(user_id)`
