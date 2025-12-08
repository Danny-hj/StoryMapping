# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

StoryMapping 是一个基于 Spring Boot 2.1.1 和 Vue.js 2.5 的故事地图应用。该项目采用前后端分离架构，支持用户创建和管理故事地图、卡片和角色协作。

## 技术栈

### 后端
- Java 8 (JDK 1.8)
- Spring Boot 2.1.1
- Spring Data JPA
- MySQL 5.x
- Apache POI (Excel 导出功能)

### 前端
- Vue.js 2.5.21
- Element UI 2.4.5
- Vue Router 3.0.1
- Vuex 3.0.1
- Axios (HTTP 客户端)

## 开发环境配置

### 后端开发
- 项目使用 Maven 管理依赖
- 默认运行端口: 9999
- 数据库连接配置在 `src/main/resources/application.properties`
- 支持热部署 (spring-boot-devtools)

### 前端开发
- 前端代码位于 `frontend/` 目录
- 使用 Vue CLI 3.x 构建
- 开发服务器默认端口: 8080

## 常用命令

### 后端命令
```bash
# 编译项目
mvn compile

# 运行项目
mvn spring-boot:run

# 运行测试
mvn test

# 打包项目
mvn package

# 清理项目
mvn clean
```

### 前端命令
```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run serve

# 构建生产版本
npm run build

# 代码检查和修复
npm run lint

# 启动 Mock 服务器
npm run mock
```

## 项目架构

### 后端架构
```
src/main/java/cn/nju/edu/
├── controller/          # REST API 控制器层
├── service/            # 业务逻辑接口层
├── serviceImpl/        # 业务逻辑实现层
├── repository/         # JPA 数据访问层
├── entity/             # JPA 实体类
├── vo/                 # 值对象 (Value Objects)
├── enumeration/        # 枚举类
└── util/               # 工具类
```

### 前端架构
```
frontend/src/
├── api/                # API 调用模块
├── components/         # Vue 组件
├── views/              # 页面视图
├── router.js           # 路由配置
├── main.js             # 应用入口
└── assets/             # 静态资源
```

## 核心功能模块

### 实体关系
- **User**: 用户管理
- **StoryMap**: 故事地图 (使用复合主键: storyName + userId)
- **Card**: 故事卡片 (使用复合主键: cardName + storyId + storyName)
- **StoryRole**: 故事角色
- **Relation**: 卡片关系
- **Collaborator**: 协作者

### API 端点
- `/getStoryMapList`: 获取故事地图列表
- `/addStoryMap`: 创建新故事地图
- `/updateStoryMap`: 更新故事地图
- `/addCollaborator`: 添加协作者
- `/exportStoryMap`: 导出故事地图为 Excel

## 数据库配置

数据库使用 MySQL，默认配置:
- 主机: 172.19.240.12:3306
- 数据库: storymap
- 用户名: root
- 密码: 123456

## 开发注意事项

### 后端开发
1. 使用 JPA 进行数据访问，实体类使用 `@Entity` 和 `@Table` 注解
2. 复合主键使用 `@IdClass` 注解配置
3. 所有服务层使用 `@Service` 注解，控制器使用 `@RestController`
4. 测试类使用 `@SpringBootTest` 和 `@Transactional` 注解

### 前端开发
1. 使用 Element UI 组件库构建界面
2. API 调用统一使用 Axios
3. 使用 Vue Router 进行路由管理
4. 组件间通信可以使用 props、events 或 Vuex

## 测试

### 后端测试
测试文件位于 `src/test/java/cn/nju/edu/` 目录下，使用 JUnit 4 进行单元测试。

### 前端测试
使用 `npm run test` 运行前端测试（需要配置测试框架）。

## 构建和部署

1. 后端构建: `mvn clean package`，生成的 JAR 文件位于 `target/` 目录
2. 前端构建: `cd frontend && npm run build`，生成的静态文件位于 `frontend/dist/` 目录
3. 部署时需要将前端构建产物放置到后端的静态资源目录或使用独立的服务器托管