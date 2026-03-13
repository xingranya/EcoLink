# EcoLink 全栈项目（Vue + Spring Boot + MySQL）

本项目为前后端分离架构：
- 前端：Vue 3 + TypeScript + Vite + Pinia + Vue Router + Tailwind CSS
- 后端：Spring Boot 3.3 + Java 17 + Spring Security(JWT) + JPA + Flyway
- 后台管理：自建管理后台（`/admin`）
- 数据库：MySQL 8.x

## 目录结构

```text
.
├─src/                     # Vue 前端（含 C 端 + 后台管理）
├─server/                  # Spring Boot 后端
│  └─src/main/resources/db/migration
├─deploy/                  # 部署配置（nginx/env）
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
- 后台管理：仪表盘、商品管理、分类管理、订单管理
- 体验优化：统一消息提示、订单进度可视化、管理员后台入口、登录页常用账号快捷填充
- 测试支撑：认证、商品、购物车、订单服务层单元测试

## 路由规划

- C 端 API：`/api/v1/** -> ecolink-server`
- 后台 API：`/api/v1/admin/** -> ecolink-server`（需 ADMIN 角色）
- 后台入口：`/admin/`
- C 端前台：`/`

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

Flyway 会自动执行迁移脚本初始化表结构与种子数据。

## 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 普通用户 | demo | 123456 |
| 管理员 | admin | admin123 |

## 质量校验

```bash
# 前端静态检查
npm run lint

# 后端编译
cd server
mvn -q -DskipTests compile

# 后端单元测试
cd server
mvn -q test
```

## 文档阅读入口

如果你希望快速了解项目结构、功能范围和实现方式，建议按下面顺序阅读：

1. 总览与架构：`docs/wiki/Home.md`、`docs/wiki/01-系统架构总览.md`
2. 前后端实现：`docs/wiki/02-前端实现设计.md`、`docs/wiki/03-后端实现设计.md`
3. 接口与后台：`docs/wiki/05-接口设计与安全机制.md`、`docs/wiki/06-后台管理模块设计.md`
4. 需求与质量：`docs/wiki/10-需求分析与可行性评估.md`、`docs/wiki/11-非功能需求与质量目标.md`、`docs/wiki/12-测试设计与验收方案.md`
5. 补充资料：[项目讲解提纲](docs/wiki/14-%E9%A1%B9%E7%9B%AE%E8%AE%B2%E8%A7%A3%E6%8F%90%E7%BA%B2.md)、[页面截图清单](docs/wiki/15-%E9%A1%B5%E9%9D%A2%E6%88%AA%E5%9B%BE%E6%B8%85%E5%8D%95.md)、[常见问题说明](docs/wiki/16-%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98%E8%AF%B4%E6%98%8E.md)、[初始化数据说明](docs/wiki/17-%E5%88%9D%E5%A7%8B%E5%8C%96%E6%95%B0%E6%8D%AE%E8%AF%B4%E6%98%8E.md)

## 快速体验路径

普通用户链路：

1. `demo / 123456` 登录
2. 首页或搜索页选品
3. 商品详情页加入购物车
4. 购物车创建订单
5. 支付页模拟支付
6. 订单页查看状态进度和时间字段

管理员链路：

1. `admin / admin123` 登录
2. 进入后台仪表盘
3. 查看商品管理与分类管理
4. 打开订单详情抽屉
5. 执行发货或完成操作

## 接口文档

后端启动后访问：

- Swagger UI：`http://localhost:8080/swagger-ui/index.html`
- OpenAPI：`http://localhost:8080/v3/api-docs`

## 技术文档与 Wiki

- Wiki 首页：`docs/wiki/Home.md`
- 架构总览：`docs/wiki/01-系统架构总览.md`
- 前端实现：`docs/wiki/02-前端实现设计.md`
- 后端实现：`docs/wiki/03-后端实现设计.md`
- 数据库设计：`docs/wiki/04-数据库设计与数据流.md`
- 接口与安全：`docs/wiki/05-接口设计与安全机制.md`
- 后台管理：`docs/wiki/06-后台管理模块设计.md`
- 业务流程：`docs/wiki/07-核心业务流程与时序.md`
- 部署配置：`docs/wiki/08-部署配置与工程说明.md`
- 接口字段字典：`docs/wiki/09-接口字段与返回模型字典.md`
- 需求与可行性：`docs/wiki/10-需求分析与可行性评估.md`
- 非功能需求：`docs/wiki/11-非功能需求与质量目标.md`
- 测试设计：`docs/wiki/12-测试设计与验收方案.md`
- 性能与扩展性：`docs/wiki/13-性能与扩展性分析.md`
- 项目讲解提纲：[查看文档](docs/wiki/14-%E9%A1%B9%E7%9B%AE%E8%AE%B2%E8%A7%A3%E6%8F%90%E7%BA%B2.md)
- 页面截图清单：[查看文档](docs/wiki/15-%E9%A1%B5%E9%9D%A2%E6%88%AA%E5%9B%BE%E6%B8%85%E5%8D%95.md)
- 常见问题说明：[查看文档](docs/wiki/16-%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98%E8%AF%B4%E6%98%8E.md)
- 初始化数据说明：[查看文档](docs/wiki/17-%E5%88%9D%E5%A7%8B%E5%8C%96%E6%95%B0%E6%8D%AE%E8%AF%B4%E6%98%8E.md)
