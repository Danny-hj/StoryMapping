package cn.nju.edu.repository;

import cn.nju.edu.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * UserRepository单元测试
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindById() {
        // 准备测试数据
        User user = new User();
        user.setName("testuser");
        user.setPassword("password123");
        user.setNickname("测试用户");
        user.setEmail("test@example.com");
        User savedUser = entityManager.persistAndFlush(user);

        // 执行测试
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // 验证结果
        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getId(), foundUser.get().getId());
        assertEquals(savedUser.getName(), foundUser.get().getName());
        assertEquals(savedUser.getEmail(), foundUser.get().getEmail());
    }

    @Test
    public void testFindByIdNotFound() {
        // 查找不存在的用户
        Optional<User> foundUser = userRepository.findById(99999);
        assertFalse(foundUser.isPresent());
    }

    @Test
    public void testFindByName() {
        // 准备测试数据
        User user = new User();
        user.setName("testuser");
        user.setPassword("password123");
        user.setNickname("测试用户");
        user.setEmail("test@example.com");
        entityManager.persistAndFlush(user);

        // 执行测试
        User foundUser = userRepository.findByName(user.getName());

        // 验证结果
        assertNotNull(foundUser);
        assertEquals(user.getName(), foundUser.getName());
        assertEquals(user.getEmail(), foundUser.getEmail());
        assertEquals(user.getNickname(), foundUser.getNickname());
    }

    @Test
    public void testFindByNameNotFound() {
        // 查找不存在的用户名
        User foundUser = userRepository.findByName("nonexistentuser");
        assertNull(foundUser);
    }

    @Test
    public void testSaveUser() {
        // 准备测试数据
        User user = new User();
        user.setName("testuser");
        user.setPassword("password123");
        user.setNickname("测试用户");
        user.setEmail("test@example.com");

        // 执行测试
        User savedUser = userRepository.save(user);

        // 验证结果
        assertNotNull(savedUser);
        assertTrue(savedUser.getId() > 0);
        assertEquals(user.getName(), savedUser.getName());
        assertEquals(user.getEmail(), savedUser.getEmail());
        assertEquals(user.getNickname(), savedUser.getNickname());

        // 验证数据已持久化
        User foundUser = entityManager.find(User.class, savedUser.getId());
        assertNotNull(foundUser);
        assertEquals(savedUser.getName(), foundUser.getName());
    }

    @Test
    public void testUpdateUser() {
        // 准备测试数据
        User user = new User();
        user.setName("testuser");
        user.setPassword("password123");
        user.setNickname("测试用户");
        user.setEmail("test@example.com");
        User savedUser = entityManager.persistAndFlush(user);

        // 修改用户信息
        savedUser.setNickname("更新后的昵称");

        // 执行更新
        User updatedUser = userRepository.save(savedUser);

        // 验证结果
        assertEquals("更新后的昵称", updatedUser.getNickname());

        // 验证数据库已更新
        User foundUser = entityManager.find(User.class, updatedUser.getId());
        assertEquals("更新后的昵称", foundUser.getNickname());
    }

    @Test
    public void testDeleteUser() {
        // 准备测试数据
        User user = new User();
        user.setName("testuser");
        user.setPassword("password123");
        user.setNickname("测试用户");
        user.setEmail("test@example.com");
        User savedUser = entityManager.persistAndFlush(user);

        // 执行删除
        userRepository.delete(savedUser);
        entityManager.flush();

        // 验证数据已删除
        User foundUser = entityManager.find(User.class, savedUser.getId());
        assertNull(foundUser);
    }

    @Test
    public void testFindAll() {
        // 准备测试数据
        User user1 = new User();
        user1.setName("testuser1");
        user1.setPassword("password123");
        user1.setNickname("测试用户1");
        user1.setEmail("test1@example.com");

        User user2 = new User();
        user2.setName("testuser2");
        user2.setPassword("password456");
        user2.setNickname("测试用户2");
        user2.setEmail("test2@example.com");

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        // 执行测试
        Iterable<User> users = userRepository.findAll();

        // 验证结果
        assertNotNull(users);
        List<User> userList = (List<User>) users;
        assertTrue(userList.size() >= 2);

        // 验证包含我们创建的用户
        boolean containsUser1 = userList.stream()
            .anyMatch(u -> u.getName().equals(user1.getName()));
        boolean containsUser2 = userList.stream()
            .anyMatch(u -> u.getName().equals(user2.getName()));

        assertTrue(containsUser1);
        assertTrue(containsUser2);
    }

    @Test
    public void testCount() {
        // 获取初始数量
        long initialCount = userRepository.count();

        // 添加新用户
        User user = new User();
        user.setName("testuser");
        user.setPassword("password123");
        user.setNickname("测试用户");
        user.setEmail("test@example.com");
        entityManager.persistAndFlush(user);

        // 验证数量增加
        long newCount = userRepository.count();
        assertEquals(initialCount + 1, newCount);
    }

    @Test
    public void testUserWithSpecialCharacters() {
        // 测试包含特殊字符的用户
        User user = new User();
        user.setName("user@#$%^&*()");
        user.setPassword("password123");
        user.setNickname("特殊字符用户");
        user.setEmail("special@example.com");

        User savedUser = entityManager.persistAndFlush(user);

        // 验证特殊字符被正确处理
        User foundUser = userRepository.findById(savedUser.getId()).orElse(null);
        assertNotNull(foundUser);
        assertEquals("user@#$%^&*()", foundUser.getName());
    }

    @Test
    public void testUserWithEmptyFields() {
        // 测试空字段
        User user = new User();
        user.setName("testuser");
        user.setPassword("password123");
        // 其他字段保持默认值（null）

        User savedUser = entityManager.persistAndFlush(user);

        // 验证空字段被正确处理
        User foundUser = userRepository.findById(savedUser.getId()).orElse(null);
        assertNotNull(foundUser);
        assertEquals("testuser", foundUser.getName());
        assertNull(foundUser.getNickname());
        assertNull(foundUser.getEmail());
    }
}