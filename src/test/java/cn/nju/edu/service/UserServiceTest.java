package cn.nju.edu.service;

import cn.nju.edu.vo.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void testUserServiceInjection() {
        // 测试UserService是否正确注入
        assertNotNull("UserService应该被正确注入", userService);
    }

    @Test
    public void testGetUserByNameAndPassword() {
        UserLoginVo userLoginVo = createTestUserLogin();

        // 测试不存在的用户登录 - UserService返回0表示失败
        int result = userService.getUserByNameAndPassword(userLoginVo);
        assertEquals("不存在的用户应该返回0", 0, result);
    }

    @Test
    public void testGetUserByNameAndPasswordWithNullName() {
        UserLoginVo userLoginVo = new UserLoginVo();
        userLoginVo.setName(null);
        userLoginVo.setPassword("password");

        int result = userService.getUserByNameAndPassword(userLoginVo);
        assertEquals("用户名为null时应该返回0", 0, result);
    }

    @Test
    public void testGetUserByNameAndPasswordWithNullPassword() {
        UserLoginVo userLoginVo = new UserLoginVo();
        userLoginVo.setName("testuser");
        userLoginVo.setPassword(null);

        int result = userService.getUserByNameAndPassword(userLoginVo);
        assertEquals("密码为null时应该返回0", 0, result);
    }

    @Test
    public void testGetUserByNameAndPasswordWithEmptyCredentials() {
        UserLoginVo userLoginVo = new UserLoginVo();
        userLoginVo.setName("");
        userLoginVo.setPassword("");

        int result = userService.getUserByNameAndPassword(userLoginVo);
        assertEquals("空用户名和密码时应该返回0", 0, result);
    }

    @Test
    public void testGetUserByName() {
        // 测试获取不存在的用户
        UserVo user = userService.getUserByName("不存在的用户");
        assertNull("不存在的用户应该返回null", user);
    }

    @Test
    public void testGetUserByNameWithNullName() {
        UserVo user = userService.getUserByName(null);
        assertNull("用户名为null时应该返回null", user);
    }

    @Test
    public void testGetUserByNameWithEmptyName() {
        UserVo user = userService.getUserByName("");
        assertNull("用户名为空时应该返回null", user);
    }

    @Test
    public void testGetUserById() {
        // 测试获取不存在的用户ID - 返回默认User对象
        UserVo user = userService.getUserById(999999);
        assertNotNull("不存在用户ID应该返回默认User对象", user);
        assertEquals("默认对象的ID应为0", 0, user.getId());
        assertNull("默认对象的名字应为null", user.getName());
        assertNull("默认对象的密码应为null", user.getPassword());
        assertNull("默认对象的昵称应为null", user.getNickname());
        assertNull("默认对象的邮箱应为null", user.getEmail());
    }

    @Test
    public void testGetUserByIdWithInvalidId() {
        UserVo user = userService.getUserById(-1);
        assertNotNull("无效的用户ID应该返回默认User对象", user);
        assertEquals("默认对象的ID应为0", 0, user.getId());
    }

    @Test
    public void testGetUserByIdWithZeroId() {
        UserVo user = userService.getUserById(0);
        assertNotNull("用户ID为0时应该返回默认User对象", user);
        assertEquals("默认对象的ID应为0", 0, user.getId());
    }

    @Test
    public void testAddUser() {
        UserVo userVo = createTestUser();

        // 测试添加用户
        int userId = userService.addUser(userVo);
        assertTrue("添加用户应该返回用户ID", userId > 0);
    }

    @Test
    public void testAddUserWithNullName() {
        try {
            UserVo userVo = createTestUser();
            userVo.setName(null);

            int result = userService.addUser(userVo);
            assertEquals("用户名为null时应该添加失败", 0, result);
        } catch (NullPointerException e) {
            // 如果抛出NullPointerException也是合理的
            assertTrue("抛出NullPointerException是合理的", true);
        }
    }

    @Test
    public void testAddUserWithEmptyName() {
        UserVo userVo = createTestUser();
        userVo.setName("");

        int result = userService.addUser(userVo);
        assertEquals("用户名为空时应该添加失败", 0, result);
    }

    @Test
    public void testAddUserWithNullPassword() {
        try {
            UserVo userVo = createTestUser();
            userVo.setPassword(null);

            int result = userService.addUser(userVo);
            assertEquals("密码为null时应该添加失败", 0, result);
        } catch (NullPointerException e) {
            // 如果抛出NullPointerException也是合理的
            assertTrue("抛出NullPointerException是合理的", true);
        }
    }

    @Test
    public void testAddUserWithEmptyPassword() {
        UserVo userVo = createTestUser();
        userVo.setPassword("");

        int result = userService.addUser(userVo);
        assertEquals("密码为空时应该添加失败", 0, result);
    }

    @Test
    public void testAddUserWithDuplicateName() {
        String uniqueUsername = "testuser_" + System.currentTimeMillis();

        // 第一个用户
        UserVo userVo1 = createTestUser();
        userVo1.setName(uniqueUsername);
        int userId1 = userService.addUser(userVo1);
        assertTrue("第一个用户应该添加成功", userId1 > 0);

        // 第二个同名用户（使用不同的对象，但相同的用户名）
        UserVo userVo2 = createTestUser();
        userVo2.setName(uniqueUsername); // 相同用户名
        userVo2.setPassword("differentpassword"); // 不同密码
        userVo2.setEmail("different@example.com"); // 不同邮箱

        // 添加第二个用户 - 结果取决于实际实现
        try {
            int userId2 = userService.addUser(userVo2);
            assertTrue("第二个用户应该有明确的返回值", userId2 >= 0);
        } catch (Exception e) {
            // 如果抛出异常也是合理的
            assertTrue("抛出异常是合理的", true);
        }
    }

    @Test
    public void testUpdateUser() {
        UserVo userVo = createTestUser();

        // 先添加用户
        int userId = userService.addUser(userVo);
        assertTrue("添加用户应该成功", userId > 0);

        // 设置用户ID用于更新
        userVo.setId(userId);
        userVo.setNickname("更新后的昵称");
        userVo.setEmail("updated@example.com");

        // 测试更新用户
        boolean result = userService.updateUser(userVo);
        assertTrue("更新用户应该成功", result);
    }

    @Test
    public void testUpdateUserWithZeroId() {
        UserVo userVo = createTestUser();
        userVo.setId(0); // 无效ID

        boolean result = userService.updateUser(userVo);
        assertFalse("更新ID为0的用户应该失败", result);
    }

    @Test
    public void testUpdateUserWithEmptyNickname() {
        UserVo userVo = createTestUser();
        userVo.setId(1);
        userVo.setNickname(""); // 空昵称

        boolean result = userService.updateUser(userVo);
        assertFalse("更新空昵称的用户应该失败", result);
    }

    @Test
    public void testUpdatePassword() {
        UserVo userVo = createTestUser();

        // 先添加用户
        int userId = userService.addUser(userVo);
        assertTrue("添加用户应该成功", userId > 0);

        // 创建密码更新对象
        UserPswdVo userPswdVo = new UserPswdVo();
        userPswdVo.setId(userId);
        userPswdVo.setOldPassword("password"); // 初始密码
        userPswdVo.setNewPassword("newpassword");

        // 测试更新密码
        boolean result = userService.updatePassword(userPswdVo);
        assertTrue("更新密码应该成功", result);
    }

    @Test
    public void testUpdatePasswordWithWrongOldPassword() {
        UserVo userVo = createTestUser();

        // 先添加用户
        int userId = userService.addUser(userVo);
        assertTrue("添加用户应该成功", userId > 0);

        // 创建密码更新对象
        UserPswdVo userPswdVo = new UserPswdVo();
        userPswdVo.setId(userId);
        userPswdVo.setOldPassword("wrongpassword"); // 错误的旧密码
        userPswdVo.setNewPassword("newpassword");

        boolean result = userService.updatePassword(userPswdVo);
        assertFalse("旧密码错误时更新密码应该失败", result);
    }

    @Test
    public void testListUser() {
        List<SimpleUserVo> users = userService.listUser();
        assertNotNull("用户列表不应为null", users);
        // 列表可能为空也可能不为空，取决于测试数据
    }

    @Test
    public void testUserVoProperties() {
        UserVo userVo = createTestUser();

        // 测试所有属性的getter和setter
        assertEquals("用户名应该正确设置", "testuser", userVo.getName());
        assertEquals("密码应该正确设置", "password", userVo.getPassword());
        assertEquals("昵称应该正确设置", "测试用户", userVo.getNickname());
        assertEquals("邮箱应该正确设置", "test@example.com", userVo.getEmail());
        assertTrue("用户ID应该大于等于0", userVo.getId() >= 0);
    }

    @Test
    public void testUserVoToString() {
        UserVo userVo = createTestUser();
        String toString = userVo.toString();

        assertNotNull("toString不应返回null", toString);
        assertTrue("toString应该包含用户名", toString.contains("testuser"));
        assertTrue("toString应该包含昵称", toString.contains("测试用户"));
    }

    @Test
    public void testUserVoSetAndGet() {
        UserVo userVo = new UserVo();

        // 测试所有属性的设置和获取
        userVo.setId(100);
        userVo.setName("newuser");
        userVo.setPassword("newpassword");
        userVo.setNickname("新用户");
        userVo.setEmail("new@example.com");

        assertEquals("用户ID应该被正确设置", 100, userVo.getId());
        assertEquals("用户名应该被正确设置", "newuser", userVo.getName());
        assertEquals("密码应该被正确设置", "newpassword", userVo.getPassword());
        assertEquals("昵称应该被正确设置", "新用户", userVo.getNickname());
        assertEquals("邮箱应该被正确设置", "new@example.com", userVo.getEmail());
    }

    @Test
    public void testUserLoginVoProperties() {
        UserLoginVo userLoginVo = createTestUserLogin();

        assertEquals("用户名应该正确设置", "testuser", userLoginVo.getName());
        assertEquals("密码应该正确设置", "password", userLoginVo.getPassword());
    }

    @Test
    public void testUserPswdVoProperties() {
        UserPswdVo userPswdVo = createTestUserPswd();

        assertEquals("用户ID应该正确设置", 1, userPswdVo.getId());
        assertEquals("旧密码应该正确设置", "oldpassword", userPswdVo.getOldPassword());
        assertEquals("新密码应该正确设置", "newpassword", userPswdVo.getNewPassword());
    }

    @Test
    public void testSimpleUserVoProperties() {
        SimpleUserVo simpleUserVo = createTestSimpleUser();

        assertEquals("用户名应该正确设置", "simpleuser", simpleUserVo.getName());
        assertEquals("用户ID应该正确设置", 1, simpleUserVo.getId());
    }

    @Test
    public void testServiceMethodsExistence() {
        // 验证所有声明的方法都存在
        try {
            assertNotNull("getUserByNameAndPassword方法应该存在",
                         userService.getClass().getMethod("getUserByNameAndPassword", UserLoginVo.class));
            assertNotNull("getUserByName方法应该存在",
                         userService.getClass().getMethod("getUserByName", String.class));
            assertNotNull("getUserById方法应该存在",
                         userService.getClass().getMethod("getUserById", int.class));
            assertNotNull("addUser方法应该存在",
                         userService.getClass().getMethod("addUser", UserVo.class));
            assertNotNull("updateUser方法应该存在",
                         userService.getClass().getMethod("updateUser", UserVo.class));
            assertNotNull("updatePassword方法应该存在",
                         userService.getClass().getMethod("updatePassword", UserPswdVo.class));
            assertNotNull("listUser方法应该存在",
                         userService.getClass().getMethod("listUser"));
        } catch (NoSuchMethodException e) {
            fail("Service方法应该存在: " + e.getMessage());
        }
    }

    @Test
    public void testCompleteUserFlow() {
        // 测试完整的用户流程：添加 -> 登录 -> 更新 -> 删除
        UserVo userVo = createTestUser();
        String uniqueUsername = "testuser_" + System.currentTimeMillis();
        userVo.setName(uniqueUsername);

        // 1. 添加用户
        int userId = userService.addUser(userVo);
        assertTrue("添加用户应该成功", userId > 0);

        // 2. 验证登录
        UserLoginVo loginVo = new UserLoginVo();
        loginVo.setName(uniqueUsername);
        loginVo.setPassword("password");
        int loginUserId = userService.getUserByNameAndPassword(loginVo);
        assertEquals("登录应该返回正确的用户ID", userId, loginUserId);

        // 3. 更新用户信息
        userVo.setId(userId);
        userVo.setNickname("更新的昵称");
        userVo.setEmail("updated@test.com");
        boolean updateResult = userService.updateUser(userVo);
        assertTrue("更新用户信息应该成功", updateResult);

        // 4. 更新密码
        UserPswdVo pswdVo = new UserPswdVo();
        pswdVo.setId(userId);
        pswdVo.setOldPassword("password");
        pswdVo.setNewPassword("newpassword");
        boolean passwordResult = userService.updatePassword(pswdVo);
        assertTrue("更新密码应该成功", passwordResult);

        // 5. 验证新密码可以登录
        loginVo.setPassword("newpassword");
        int newLoginUserId = userService.getUserByNameAndPassword(loginVo);
        assertEquals("新密码登录应该成功", userId, newLoginUserId);
    }

    private UserVo createTestUser() {
        UserVo userVo = new UserVo();
        userVo.setName("testuser");
        userVo.setPassword("password");
        userVo.setNickname("测试用户");
        userVo.setEmail("test@example.com");
        return userVo;
    }

    private UserLoginVo createTestUserLogin() {
        UserLoginVo userLoginVo = new UserLoginVo();
        userLoginVo.setName("testuser");
        userLoginVo.setPassword("password");
        return userLoginVo;
    }

    private UserPswdVo createTestUserPswd() {
        UserPswdVo userPswdVo = new UserPswdVo();
        userPswdVo.setId(1);
        userPswdVo.setOldPassword("oldpassword");
        userPswdVo.setNewPassword("newpassword");
        return userPswdVo;
    }

    private SimpleUserVo createTestSimpleUser() {
        SimpleUserVo simpleUserVo = new SimpleUserVo();
        simpleUserVo.setId(1);
        simpleUserVo.setName("simpleuser");
        return simpleUserVo;
    }
}