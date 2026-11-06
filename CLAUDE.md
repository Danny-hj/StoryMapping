# CLAUDE.md

本文件说明 StoryMapping 的架构、本地运行方式和开发约定，供后续开发时与当前代码对照。启动步骤的简版见根目录 [README.md](README.md)，测试见 [docs/testing.md](docs/testing.md)。

## 项目概述

StoryMapping 是基于 Spring Boot 3.5 和 Vue 3 的故事地图应用，前后端分离。用户可以注册登录、创建故事地图、编排卡片、管理角色和协作者，并把地图导出为 Excel。

本仓库 fork 自 [xiaoluomati/StoryMapping](https://github.com/xiaoluomati/StoryMapping)。本地默认使用文件型 H2。测试 profile 使用另一套内存 H2。

## 技术栈

### 后端

- Java 21（`pom.xml` 中 `java.version` 为 21）
- Spring Boot 3.5
- Spring Data JPA（Jakarta Persistence）
- H2（当前数据源）
- Apache POI 5（Excel 导出）

`pom.xml` 仍声明了 MySQL 驱动，给 `prod` 或环境变量覆盖使用。默认数据源在 `src/main/resources/application.properties`，`application-prod.properties` 覆盖部署配置。

### 前端

- Vue 3
- Element Plus
- Vue Router 4
- Vite
- Axios

页面状态写在组件里，没有 Vuex / Pinia。

## 本地运行

使用 JDK 21 与 Maven 3。

### 后端

- 入口类：`cn.nju.edu.StoryMapApplication`
- 默认端口：`8888`（`server.port`）
- 热部署：`spring-boot-devtools` 已引入

```bash
mvn compile
mvn spring-boot:run
mvn test
mvn package
mvn clean
```

### 前端

代码在 `frontend/`，构建工具是 Vite。开发服务器端口是 `3000`，写在 `frontend/vite.config.js`。

```bash
cd frontend
npm install
npm run serve
npm run build
npm run lint
npm run mock
```

`npm run mock` 用 json-server 监视 `frontend/src/mock/db.json`，端口 `8090`。`npm run mockdev` 会同时启动 mock 和 `npm run serve`。`package.json` 没有 `test` 脚本。

开发代理把浏览器的 `/api` 转到 `http://localhost:8888/`，并去掉 `/api` 前缀。`frontend/src/api/api.js` 的基地址是 `api/`，因此页面请求 `/api/user/login` 到达后端时路径是 `/user/login`。

## 数据库

默认配置（`application.properties`）：

- JDBC URL：`jdbc:h2:file:./data/storymap`（从仓库根目录启动）
- 方言：`org.hibernate.dialect.H2Dialect`
- `spring.jpa.hibernate.ddl-auto=update`：重启后保留数据
- H2 控制台路径：`/h2-console`，`web-allow-others=false`，仅本机可连
- `./data/` 被 git 忽略

`prod` 关闭 H2 控制台，数据源由环境变量提供，`ddl-auto` 默认为 `validate`。

不要把外部数据库的地址、账号或口令写入仓库或文档。环境变量优先级高于配置文件，见根目录 [README.md](README.md)。

测试使用 `src/test/resources/application-test.properties`，由 `@ActiveProfiles("test")` 加载，覆盖为内存库。详见 [docs/testing.md](docs/testing.md)。

用户密码以 BCrypt 存储。`UserService` 返回的 `UserVo` 不带密码。`User.name` 有唯一约束，重复注册返回 `0`。表名是 `app_user`。故事地图和卡片的业务 id 在保存时分配，因为它们不是单独的主键，Hibernate 不会为它们生成值。

## 项目架构

### 后端

```
src/main/java/cn/nju/edu/
├── controller/     # REST 控制器
├── service/        # 业务接口
├── serviceImpl/    # 业务实现
├── repository/     # Spring Data JPA
├── entity/         # 实体
├── vo/             # 入参与返回对象
├── enumeration/    # 卡片状态、类型
└── util/           # ExcelHelper
```

### 前端

```
frontend/src/
├── api/            # Axios 封装
├── components/     # 顶栏、侧栏
├── views/          # 页面
├── mock/           # json-server 数据
├── router.js
├── main.js
└── assets/
```

主要页面路由：`/login`、`/register`、`/storymap-manager`、`/create`、`/search`、`/storymap/:storymapid`。`/` 会重定向到 `/storymap-manager`。

## 实体

- **User**：表 `app_user`，字段 `id`、`name`、`password`、`nickname`、`email`
- **StoryMap**：表 `story_map`，复合主键 `storyName` + `userId`（`@IdClass(StoryMapKey.class)`）
- **Card**：表 `card`。实体上的 `@Id` 字段是 `positionX`、`positionY`、`storyId`、`cardId`，`@IdClass` 为 `CardKey`。`CardKey` 目前只声明了 `positionX`、`positionY`、`storyId`
- **StoryRole**：`roleId`、`roleName`、`roleDetail`、`storyId`
- **Relation**：`relationId`、`roleId`、`cardId`
- **Collaborator**：`id`、`userId`、`storyId`

## API

控制器上的路径如下。开发环境里前端会在前面加 `/api`，由 devServer 代理去掉。

### UserController（`/user`）

| 方法 | 路径 |
| --- | --- |
| POST | `/user/register` |
| POST | `/user/login` |
| GET | `/user/{id}` |
| PUT | `/user/{id}` |
| PUT | `/user/password/{id}` |
| GET | `/user/list` |

`UserService.getUserByNameAndPassword` 与 `addUser` 失败时返回 `0`，成功时返回用户 id。`getUserById` 在 id 不存在时用空的 `User` 组装返回对象。

### StoryMapController

| 方法 | 路径 |
| --- | --- |
| GET | `/getStoryMapList` |
| POST | `/addStoryMap` |
| POST | `/addCollaborator` |
| POST | `/updateStoryMap` |
| POST | `/deleteStoryMap` |
| GET | `/exportExcel` |

### CardController

| 方法 | 路径 |
| --- | --- |
| GET | `/getCardList` |
| POST | `/addCard/{source}` |
| POST | `/udateCardList` |
| POST | `/updateCard` |
| POST | `/deleteCard` |

批量更新路径在源码里是 `/udateCardList`。

### StoryRoleController（`/storyRoles`）

| 方法 | 路径 |
| --- | --- |
| GET | `/storyRoles/getStoryRoleList` |
| POST | `/storyRoles/addStoryRole` |
| DELETE | `/storyRoles/deleteStoryRole` |
| PUT | `/storyRoles/updateStoryRole` |
| GET | `/storyRoles/getRelationList` |
| POST | `/storyRoles/addRelation` |
| DELETE | `/storyRoles/deleteRelation` |

## 开发约定

1. 持久化使用 JPA。实体使用 `@Entity`、`@Table`，复合主键使用 `@IdClass`。
2. 服务实现使用 `@Service`，控制器使用 `@RestController`。
3. 前端界面使用 Element UI，HTTP 走 `frontend/src/api/` 里的 Axios 封装，路由在 `frontend/src/router.js`。

## 测试

后端测试在 `src/test/java/cn/nju/edu/`，JUnit 4，分 Repository（`@DataJpaTest`）和 Service（`@SpringBootTest`）两类，都加了 `@ActiveProfiles("test")`。没有 Controller、Entity、Util 测试，也没有 Mockito / MockMvc。

说明、用例数量和运行方式见 [docs/testing.md](docs/testing.md)。

## 构建

1. `mvn clean package` 生成 `target/storymap.jar`（`finalName` 为 `storymap`）。
2. `cd frontend && npm run build` 生成 `frontend/dist/`。
3. 前端产物需要单独托管，或放到后端静态资源目录。
