# 页面-组件-接口映射

## 页面映射

| 页面 | 路由 | 主要组件 | 主要接口 |
|---|---|---|---|
| 首页 | `/` | `AppHeader` `ProductCard` | `GET /categories` `GET /products` |
| 搜索页 | `/search` | `AppHeader` `ProductCard` | `GET /categories` `GET /products` |
| 商品详情 | `/product/:id` | `AppHeader` | `GET /products/{id}` `POST /cart/items` `POST /favorites/{productId}` |
| 购物车 | `/cart` | `AppHeader` | `GET /cart` `PUT /cart/items/{id}` `DELETE /cart/items/{id}` `POST /orders` |
| 订单页 | `/orders` | `AppHeader` | `GET /orders` `POST /orders/{id}/pay` |
| 个人中心 | `/profile` | `AppHeader` | `GET /users/me` `GET/POST/PUT/DELETE /addresses` `GET/DELETE /favorites` |
| 登录页 | `/login` | - | `POST /auth/login` |
| 注册页 | `/register` | - | `POST /auth/register` |

## 组件复用矩阵

- `AppHeader`：全站顶部导航与登录态入口。
- `ProductCard`：首页与搜索页的商品卡片。
- 订单卡片：在 `OrdersView` 内部按订单展开渲染。
- 购物车行项目：在 `CartView` 内部复用同一布局。

## 状态与空态

- 加载态：通过页面请求时按钮 loading 和默认骨架占位（简版）。
- 空态：搜索无结果、购物车空、收藏空。
- 错误态：统一通过 `alert(error.message)` 反馈，可后续替换为全局消息组件。
