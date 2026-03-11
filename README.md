# EcoLink 全栈项目（Vue + Spring Boot + MySQL + 芋道后台）

本项目已从 React 原型重构为前后端分离架构：
- 前端：Vue 3 + TypeScript + Vite + Pinia + Vue Router + Tailwind CSS
- 后端：Spring Boot 3.3 + Java 17 + Spring Security(JWT) + JPA + Flyway
- 后台管理：yudao-boot-mini（`system + infra`）+ yudao-ui-admin-vue3
- 数据库：MySQL 8.x

## 目录结构

```text
.
├─src/                     # Vue 前端
├─server/                  # Spring Boot 后端
│  └─src/main/resources/db/migration
├─yudao-admin-server/      # 芋道后台后端（git submodule）
├─yudao-ui-admin/          # 芋道后台前端（git submodule）
├─deploy/                  # 集成配置（nginx/env/yudao 覆盖配置）
├─scripts/                 # 集成辅助脚本
└─docs/                    # 文档
```

## 已实现能力

- 认证：注册、登录、获取当前用户
- 商品：分类、搜索/筛选/排序、商品详情
- 收藏：收藏列表、添加、取消
- 地址：增删改查
- 购物车：查询、添加、改数量、删除
- 订单：创建订单、订单列表/详情、模拟支付
- 订单自动流转：`PAID -> SHIPPED -> COMPLETED`
- Phase 1 后台：管理员登录、RBAC、菜单、后台用户管理（芋道）
- Phase 2 后台：EcoLink 分类/商品/订单管理（`/admin-api/ecolink/**`）

## Phase 1 集成边界

- C 端 API：`/api/v1/** -> ecolink-server`
- 后台 API：`/admin-api/** -> yudao-admin-server`
- 后台入口：`/admin/ -> yudao-ui-admin`
- C 端前台：`/ -> EcoLink Vue`

详细落地步骤见：

- `docs/yudao-phase1-integration.md`
- `deploy/nginx/ecolink.local.conf`
- `deploy/sql/yudao/ecolink-admin-menu.sql`

## 前端启动

1. 安装依赖

```bash
npm install
```

2. 配置环境变量

```bash
cp .env.example .env.local
```

3. 启动前端

```bash
npm run dev
```

## 后端启动

1. 创建数据库

```sql
CREATE DATABASE ecolink DEFAULT CHARACTER SET utf8mb4;
```

2. 配置环境变量

```bash
cd server
cp .env.example .env
# 编辑 .env：把 DB_PASSWORD 改成你本机 MySQL root 的密码
# 若 8080 端口被占用，可设置 SERVER_PORT=8081
```

3. 启动后端（**必须在 server 目录下执行**，否则读不到 .env）

```bash
cd server
mvn spring-boot:run
```

Flyway 会自动执行 `V1/V2` 脚本初始化表结构与种子数据。若出现 `Access denied for user 'root'@'localhost'`，请确认：① 是在 `server` 目录下执行的上述命令；② `.env` 中的 `DB_PASSWORD` 与本地 MySQL root 密码一致（可先用 `mysql -u root -p` 验证）。

## 默认演示账号

- 用户名：`demo`
- 密码：`123456`

## 接口文档

后端启动后访问：

- Swagger UI：`http://localhost:8080/swagger-ui/index.html`
- OpenAPI：`http://localhost:8080/v3/api-docs`

## 芋道后台默认账号

- 用户名：`admin`
- 密码：`admin123`
