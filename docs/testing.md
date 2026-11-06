# 测试

本文描述仓库里**已经存在**的后端测试：位置、配置和运行方式。用例数量按当前源码中的 `@Test` 方法统计，不是某次构建的通过或失败记录。

## 范围

测试在 `src/test/java/cn/nju/edu/`，使用 JUnit 4 和 Spring Boot Test。测试会拉起 Spring 容器并访问 H2，没有 Mockito，也没有 MockMvc。

目前没有 Controller、Entity、Util 层测试类。前端 `package.json` 没有 `test` 脚本。

共 8 个测试类、141 个测试方法：Repository 25 个，Service 116 个。

| 测试类 | 层 | 方法数 | 覆盖内容 |
| --- | --- | --- | --- |
| `repository/UserRepositoryTest` | Repository | 11 | 用户的保存、查询、更新、删除 |
| `repository/CardRepositoryTest` | Repository | 7 | 按主键和 `storyId` 查询、保存、更新、删除 |
| `repository/StoryMapRepositoryTest` | Repository | 7 | 按主键和 `storyId` 查询、保存、更新、删除 |
| `service/UserServiceTest` | Service | 32 | 注册、登录、资料与密码更新、用户列表、密码散列 |
| `service/CardServiceTest` | Service | 17 | 卡片查询、新增、更新、删除 |
| `service/StoryMapServiceTest` | Service | 26 | 故事地图、协作者、Excel 导出 |
| `service/StoryRoleServiceTest` | Service | 24 | 角色的查询与增删改 |
| `service/RelationServiceTest` | Service | 17 | 卡片与角色的关联 |

`CollaboratorRepository`、`StoryRoleRepository`、`RelationRepository` 没有单独的 Repository 测试，相关行为出现在 Service 测试里。

## 配置

两类测试都使用 `@ActiveProfiles("test")`，加载 `src/test/resources/application-test.properties`。这个库与 `dev` profile 的 `jdbc:h2:file:./data/storymap` 相互独立，测试不会打开 H2 控制台。

测试库：

- JDBC URL：`jdbc:h2:mem:testdb`，并带有 `MODE=MySQL`
- 驱动：`org.h2.Driver`
- 方言：`org.hibernate.dialect.H2Dialect`
- `spring.jpa.hibernate.ddl-auto=create-drop`

Repository 测试类：

- `@RunWith(SpringRunner.class)`
- `@DataJpaTest`（默认随测试回滚）
- `@ActiveProfiles("test")`

Service 测试类：

- `@RunWith(SpringJUnit4ClassRunner.class)`
- `@SpringBootTest`
- `@ActiveProfiles("test")`
- `@Transactional`（测试方法结束后回滚）

`src/test/resources/schema-h2.sql` 只写了 `app_user` 表的建表语句，没有任何配置引用它。表结构以实体和 `ddl-auto=create-drop` 为准。测试仍是 JUnit 4，由 `junit-vintage-engine` 在 Spring Boot 3 上运行。

## 运行

在仓库根目录、JDK 21 下执行：

```bash
# 全部测试
mvn test

# 单个测试类
mvn test -Dtest=UserRepositoryTest

# 单个测试方法
mvn test -Dtest=UserRepositoryTest#testFindById
```

Service 测试会启动完整的 Spring 上下文，第一次运行比 Repository 测试慢。

## 写新测试时

- 新的测试类继续加上 `@ActiveProfiles("test")`，避免连到主配置的内存库。
- Repository 测试沿用 `@DataJpaTest` 和 `TestEntityManager`。Service 测试沿用 `@SpringBootTest` 与类上的 `@Transactional`。
- 用户名等会碰到唯一约束或 `findByName` 的数据，每个用例使用不同的值。
- 登录和注册失败时，`UserService` 返回 `0`；成功时返回用户 id。重复的用户名也返回 `0`。`getUserById` 在记录不存在时返回由空 `User` 组装的对象，各字段为默认值。
- 密码在入库前做 BCrypt 散列。`getUserById` / `getUserByName` 不回传密码，断言不要把查出来的密码和明文比较。登录断言走 `getUserByNameAndPassword`。
- 现有个别 Service 测试在 `catch` 里把异常当成通过。新增断言请直接检查返回值或抛出的异常，不要在捕获之后写恒为真的断言。
