# StoryMapping

StoryMapping 是一个前后端分离的故事地图应用。用户可以注册、登录，创建故事地图，在地图上编排卡片，管理角色和协作者，并把地图导出为 Excel。

后端是 Spring Boot 3.5，前端是 Vue 3。本仓库 fork 自 [xiaoluomati/StoryMapping](https://github.com/xiaoluomati/StoryMapping)。本地默认使用文件型 H2，不需要单独安装数据库即可启动。测试使用另一套内存库。

## 技术栈

- 后端：Java 21、Spring Boot 3.5、Spring Data JPA、H2、Apache POI 5
- 前端：Vue 3、Vue Router 4、Element Plus、Vite、Axios

`pom.xml` 里还保留着 MySQL 驱动，供 `prod` 配置或环境变量覆盖时使用。本地数据源写在 `src/main/resources/application.properties`。

## 环境

- JDK 21
- Maven 3
- Node.js 20 或更新版本，以及 npm

请使用 JDK 21 编译和运行。Maven 会拒绝其它主版本。

## 启动

先启动后端，再启动前端。

### 后端

在仓库根目录：

```bash
mvn spring-boot:run
```

服务端口是 **8888**。入口类是 `cn.nju.edu.StoryMapApplication`。

默认数据源是文件库 `jdbc:h2:file:./data/storymap`（相对仓库根目录），`ddl-auto=update`，重启后用户和地图还在。`./data/` 已加入 `.gitignore`。H2 控制台路径是 `http://localhost:8888/h2-console`，并且 `web-allow-others=false`，不接受远程连接。控制台账号与 `application.properties` 里的数据源相同，口令默认为空，只适合本机。

密码入库前用 BCrypt 散列，接口响应不返回密码。同名账号不能重复注册。用户表名是 `app_user`（H2 2 把 `user` 当作保留字）。

换外部数据库时不要把地址、账号或口令写进仓库。环境变量会覆盖配置文件。不要在配置里写 `${SPRING_DATASOURCE_URL:...}`，这个名字和属性本身是同一个键，占位符会变成空值，Spring Boot 就会退回内存库。切换到 `prod` 会关闭 H2 控制台，并把 `ddl-auto` 默认为 `validate`。`prod` 在没有 `SPRING_DATASOURCE_URL` 时会读取 `DB_URL`，两者都没有则启动失败：

```bash
export SPRING_PROFILES_ACTIVE=prod
export SPRING_DATASOURCE_URL='jdbc:mysql://<host>:<port>/<database>'
export SPRING_DATASOURCE_USERNAME='<username>'
export SPRING_DATASOURCE_PASSWORD='<password>'
export SPRING_DATASOURCE_DRIVER_CLASS_NAME='com.mysql.cj.jdbc.Driver'
export SPRING_JPA_DATABASE_PLATFORM='org.hibernate.dialect.MySQLDialect'
```

### 前端

```bash
cd frontend
npm install
npm run serve
```

开发服务器端口是 **3000**（`frontend/vite.config.js`），浏览器打开 `http://localhost:3000`。路由使用 hash 模式。

前端请求以 `/api` 开头。开发代理把 `/api` 转到 `http://localhost:8888/`，并去掉这个前缀，所以页面上的 `/api/user/login` 对应后端的 `/user/login`。

常用脚本：

| 命令 | 作用 |
| --- | --- |
| `npm run serve` | 启动开发服务器，端口 3000 |
| `npm run build` | 生产构建，输出到 `frontend/dist/` |
| `npm run lint` | 检查前端代码 |
| `npm run mock` | 用 json-server 监视 `frontend/src/mock/db.json`，端口 8090 |

`package.json` 没有 `test` 脚本。`npm run mockdev` 会同时启动 mock 服务和 `npm run serve`。

## 文档

- [CLAUDE.md](CLAUDE.md)：目录结构、实体、接口和开发约定
- [docs/testing.md](docs/testing.md)：现有后端测试的配置和运行方式
- [frontend/README.md](frontend/README.md)：前端补充说明。命令和端口以本文为准

## 构建

```bash
mvn clean package
```

后端包在 `target/storymap.jar`。前端需要在 `frontend/` 下单独执行 `npm run build`，产物在 `frontend/dist/`，自行托管或放到后端的静态资源目录。

持续集成见 `.github/workflows/ci.yml`：JDK 21 下执行 `mvn test`，Node.js 22 下执行前端 `npm run lint` 和 `npm run build`。`frontend/src/plugins/html2canvas.js` 是未使用的第三方打包文件，已从 ESLint 中排除。
