#EcoLink 全栈项目（Vue + Spring Boot + MySQL）

本项目为前后端分离架构：
-前端：Vue 3 + TypeScript + Vite + Pinia + Vue Router + Tailwind CSS
-后端：Spring Boot 3.3 + Java 17 + Spring Security(JWT) + JPA + Flyway
- 后台管理：自建管理后台（`/admin`）
-数据库：MySQL 8.x

## 目录结构

```文本
.
├─src/                     # Vue 前端（含 C 端 + 后台管理）
├─server/ # Spring Boot 后端
│ └─src/main/resources/db/migration
├─deploy/ # 部署配置（nginx/env）
└─docs/                    # 文档
```

## 目前大概能跑的能力

- 认证：注册、登录、获取当前用户
- 商品：分类、搜索/筛选/排序、商品详情
- 收藏：收藏列表、添加、取消
- 地址：增删改查
- 购物车：查询、添加、改数量、删除
- 订单：创建订单、订单列表/详情、模拟支付
- 订单自动流转：`PAID -> SHIPPED -> COMPLETED`
- 后台管理：仪表盘、商品管理、分类管理、订单管理

## 路由规划

-C 端 API：`/api/v1/** -> ecolink-server`
-后台 API：`/api/v1/admin/** -> ecolink-server`（需 ADMIN 角色）
- 后台入口：`/admin/`
- C 端前台：`/`

## 前端启动

1. 安装依赖

```猛敲
npm 安装
```

2. 配置环境变量

```猛敲
复制 .env.example 文件为 .env.local 文件
```

3. 启动前端

```猛敲
运行开发环境：npm run dev
```

## 后端启动

1. 创建数据库

```结构化查询语言
CREATE DATABASE ecolink DEFAULT CHARACTER SET utf8mb4;
```

2. 配置环境变量

```猛敲
cd server
复制 .env.example 文件为 .env 文件
# 编辑 .env：把 DB_PASSWORD 改成你本机 MySQL root 的密码
# 若 8080 端口被占用，可设置 SERVER_PORT=8081
```

3. 启动后端（**必须在 server 目录下执行**，否则读不到 .env）

```猛敲
cd server
运行 Maven 命令：mvn spring-boot:run
```

Flyway 会自动执行迁移脚本初始化表结构与种子数据。

## 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 普通用户 |演示| 123456 |
| 管理员 |管理| admin123 |

## 接口文档

后端启动后访问：

-Swagger UI：`http://localhost:8080/swagger-ui/index.html`
- OpenAPI：`http://localhost:8080/v3/api-docs`
