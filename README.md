# StoryMapping

StoryMapping 是一个前后端分离的故事地图应用。用户可以注册、登录，创建故事地图，在地图上编排卡片，管理角色和协作者，并把地图导出为 Excel。

后端是 Spring Boot 2.1.1，前端是 Vue 2.5。本仓库 fork 自 [xiaoluomati/StoryMapping](https://github.com/xiaoluomati/StoryMapping)。默认使用内嵌 H2 内存数据库，不需要单独安装数据库即可在本地启动和跑测试。

## 技术栈

- 后端：Java 8、Spring Boot 2.1.1、Spring Data JPA、H2、Apache POI
- 前端：Vue 2.5、Vue Router 3、Element UI 2.4、Axios；样式使用 Sass（Dart Sass）和 Less

`pom.xml` 里还保留着 MySQL 驱动。当前生效的数据源写在 `src/main/resources/application.properties`，是 H2 内存库。

## 环境

- JDK 8
- Maven 3
- Node.js 与 npm

请使用 JDK 8 编译和运行。Spring Boot 2.1.1 面向 Java 8。

## 启动

先启动后端，再启动前端。

### 后端

在仓库根目录：

```bash
mvn spring-boot:run
```

服务端口是 **8888**。入口类是 `cn.nju.edu.StoryMapApplication`。

数据库是内存库 `jdbc:h2:mem:storymap`。`spring.jpa.hibernate.ddl-auto=create-drop`，进程退出后表和数据都会删除，下次启动是空库，需要重新注册用户。

H2 控制台路径是 `/h2-console`，即 `http://localhost:8888/h2-console`。登录所用账号与 `application.properties` 中的数据源配置相同。该配置打开了 `spring.h2.console.settings.web-allow-others`，只适合本机开发。

改用外部数据库时，用环境变量覆盖连接信息，并把 JPA 方言改成对应数据库。不要把地址、账号或口令提交进仓库：

```bash
export SPRING_DATASOURCE_URL='jdbc:mysql://<host>:<port>/<database>'
export SPRING_DATASOURCE_USERNAME='<username>'
export SPRING_DATASOURCE_PASSWORD='<password>'
```

环境变量的优先级高于 `application.properties`。

### 前端

```bash
cd frontend
npm install
npm run serve
```

开发服务器端口是 **3000**（`frontend/vue.config.js`），浏览器打开 `http://localhost:3000`。

前端请求以 `/api` 开头。开发代理把 `/api` 转到 `http://localhost:8888/`，并去掉这个前缀，所以页面上的 `/api/user/login` 对应后端的 `/user/login`。

常用脚本：

| 命令 | 作用 |
| --- | --- |
| `npm run serve` | 启动开发服务器，端口 3000 |
| `npm run build` | 生产构建，输出到 `frontend/dist/` |
| `npm run lint` | 检查前端代码 |
| `npm run mock` | 用 json-server 监视 `frontend/src/mock/db.json`，端口 8090 |

`package.json` 没有 `test` 脚本。`npm run mockdev` 会再调用仓库里不存在的 `npm run dev`，请改用上面的 `npm run serve` 和 `npm run mock`。

## 文档

- [CLAUDE.md](CLAUDE.md)：目录结构、实体、接口和开发约定
- [docs/testing.md](docs/testing.md)：现有后端测试的配置和运行方式
- [frontend/README.md](frontend/README.md)：Vue CLI 自带的英文说明。命令和端口以本文为准

## 构建

```bash
mvn clean package
```

后端包在 `target/storymap.jar`。前端需要在 `frontend/` 下单独执行 `npm run build`，产物在 `frontend/dist/`，自行托管或放到后端的静态资源目录。
