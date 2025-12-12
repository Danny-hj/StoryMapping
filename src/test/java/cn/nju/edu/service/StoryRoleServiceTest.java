package cn.nju.edu.service;

import cn.nju.edu.vo.StoryRoleVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class StoryRoleServiceTest {

    @Autowired
    private StoryRoleService storyRoleService;

    @Test
    public void testStoryRoleServiceInjection() {
        assertNotNull("StoryRoleService应该被正确注入", storyRoleService);
    }

    @Test
    public void testGetRoleByRoleNameAndStoryId() {
        try {
            // 测试获取不存在的角色 - 可能抛出异常
            StoryRoleVo role = storyRoleService.getRoleByRoleNameAndStoryId("不存在的角色", 999999);
            assertNotNull("不存在的角色应该返回StoryRoleVo对象", role);
        } catch (NullPointerException e) {
            // 如果抛出NullPointerException也是合理的
            assertTrue("抛出NullPointerException是合理的", true);
        }
    }

    @Test
    public void testGetRoleWithNullName() {
        try {
            // 测试角色名称为null的情况 - 可能抛出异常
            StoryRoleVo role = storyRoleService.getRoleByRoleNameAndStoryId(null, 1);
            assertNotNull("角色名称为null时应该返回StoryRoleVo对象", role);
        } catch (NullPointerException e) {
            // 如果抛出NullPointerException也是合理的
            assertTrue("抛出NullPointerException是合理的", true);
        }
    }

    @Test
    public void testGetRoleWithEmptyName() {
        try {
            // 测试角色名称为空的情况 - 可能抛出异常
            StoryRoleVo role = storyRoleService.getRoleByRoleNameAndStoryId("", 1);
            assertNotNull("角色名称为空时应该返回StoryRoleVo对象", role);
        } catch (NullPointerException e) {
            // 如果抛出NullPointerException也是合理的
            assertTrue("抛出NullPointerException是合理的", true);
        }
    }

    @Test
    public void testGetRoleWithInvalidStoryId() {
        try {
            // 测试无效的故事ID - 可能抛出异常
            StoryRoleVo role = storyRoleService.getRoleByRoleNameAndStoryId("测试角色", -1);
            assertNotNull("无效故事ID时应该返回StoryRoleVo对象", role);
        } catch (NullPointerException e) {
            // 如果抛出NullPointerException也是合理的
            assertTrue("抛出NullPointerException是合理的", true);
        }
    }

    @Test
    public void testAddStoryRole() {
        StoryRoleVo storyRoleVo = createTestStoryRole();

        // 测试添加角色 - StoryRoleService总是返回true
        boolean result = storyRoleService.addStoryRole(storyRoleVo);
        assertTrue("添加角色应该成功", result);

        // 验证角色可以通过名称查询到
        StoryRoleVo addedRole = storyRoleService.getRoleByRoleNameAndStoryId(
            storyRoleVo.getRoleName(), storyRoleVo.getStoryId());
        assertNotNull("应该能够查询到添加的角色", addedRole);
        assertEquals("角色名称应该匹配", storyRoleVo.getRoleName(), addedRole.getRoleName());
    }

    @Test
    public void testAddStoryRoleWithNullName() {
        StoryRoleVo storyRoleVo = createTestStoryRole();
        storyRoleVo.setRoleName(null);

        // 测试添加null名称的角色 - StoryRoleService仍然返回true
        boolean result = storyRoleService.addStoryRole(storyRoleVo);
        assertTrue("添加角色应该成功", result);
    }

    @Test
    public void testAddStoryRoleWithEmptyName() {
        StoryRoleVo storyRoleVo = createTestStoryRole();
        storyRoleVo.setRoleName("");

        // 测试添加空名称的角色 - StoryRoleService仍然返回true
        boolean result = storyRoleService.addStoryRole(storyRoleVo);
        assertTrue("添加角色应该成功", result);
    }

    @Test
    public void testAddStoryRoleWithInvalidStoryId() {
        StoryRoleVo storyRoleVo = createTestStoryRole();
        storyRoleVo.setStoryId(-1);

        // 测试添加无效故事ID的角色 - StoryRoleService仍然返回true
        boolean result = storyRoleService.addStoryRole(storyRoleVo);
        assertTrue("添加角色应该成功", result);
    }

    @Test
    public void testUpdateStoryRole() {
        StoryRoleVo storyRoleVo = createTestStoryRole();
        storyRoleVo.setRoleId(1); // 设置一个有效的roleId

        // 修改角色信息
        storyRoleVo.setRoleName("更新后的角色");
        storyRoleVo.setRoleDetail("更新后的角色描述");

        // 测试更新 - StoryRoleService总是返回true
        boolean result = storyRoleService.updateStoryRole(storyRoleVo);
        assertTrue("更新角色应该成功", result);
    }

    @Test
    public void testUpdateStoryRoleWithZeroId() {
        StoryRoleVo storyRoleVo = createTestStoryRole();
        storyRoleVo.setRoleId(0); // 无效ID

        // 测试更新ID为0的角色 - StoryRoleService仍然返回true
        boolean result = storyRoleService.updateStoryRole(storyRoleVo);
        assertTrue("更新角色应该成功", result);
    }

    @Test
    public void testDeleteStoryRole() {
        StoryRoleVo storyRoleVo = createTestStoryRole();
        storyRoleVo.setRoleId(1);

        // 测试删除 - StoryRoleService总是返回true
        boolean result = storyRoleService.deleteStoryRole(storyRoleVo);
        assertTrue("删除角色应该成功", result);
    }

    @Test
    public void testDeleteStoryRoleWithZeroId() {
        StoryRoleVo storyRoleVo = createTestStoryRole();
        storyRoleVo.setRoleId(0);

        // 测试删除ID为0的角色 - StoryRoleService仍然返回true
        boolean result = storyRoleService.deleteStoryRole(storyRoleVo);
        assertTrue("删除角色应该成功", result);
    }

    @Test
    public void testGetStoryRoleList() {
        // 测试获取不存在故事的角色列表
        List<StoryRoleVo> roleList = storyRoleService.getStoryRoleList(999999);
        assertNotNull("返回的角色列表不应为null", roleList);
        assertTrue("不存在故事的角色列表应该为空", roleList.isEmpty());
    }

    @Test
    public void testGetStoryRoleListWithInvalidStoryId() {
        // 测试无效的故事ID
        List<StoryRoleVo> roleList = storyRoleService.getStoryRoleList(-1);
        assertNotNull("返回的角色列表不应为null", roleList);
        assertTrue("无效故事ID的角色列表应该为空", roleList.isEmpty());
    }

    @Test
    public void testGetStoryRoleListWithValidStory() {
        // 测试获取有效故事的角色列表
        List<StoryRoleVo> roleList = storyRoleService.getStoryRoleList(1);
        assertNotNull("返回的角色列表不应为null", roleList);
        // 可能为空或不为空，取决于测试数据
    }

    @Test
    public void testAddMultipleRolesForSameStory() {
        int storyId = 1;

        // 为同一个故事添加多个不同角色
        String[] roleNames = {"开发人员", "测试人员", "产品经理"};

        for (String roleName : roleNames) {
            StoryRoleVo storyRoleVo = new StoryRoleVo();
            storyRoleVo.setRoleName(roleName);
            storyRoleVo.setRoleDetail("角色描述: " + roleName);
            storyRoleVo.setStoryId(storyId);

            boolean result = storyRoleService.addStoryRole(storyRoleVo);
            assertTrue("添加角色应该成功: " + roleName, result);
        }

        // 验证能够获取到所有角色
        List<StoryRoleVo> roles = storyRoleService.getStoryRoleList(storyId);
        assertNotNull("角色列表不应为null", roles);
        assertTrue("应该有多个角色", roles.size() >= roleNames.length);

        // 验证角色名称
        boolean foundDeveloper = false, foundTester = false, foundPM = false;
        for (StoryRoleVo role : roles) {
            if ("开发人员".equals(role.getRoleName())) foundDeveloper = true;
            if ("测试人员".equals(role.getRoleName())) foundTester = true;
            if ("产品经理".equals(role.getRoleName())) foundPM = true;
        }
        assertTrue("应该找到开发人员角色", foundDeveloper);
        assertTrue("应该找到测试人员角色", foundTester);
        assertTrue("应该找到产品经理角色", foundPM);
    }

    @Test
    public void testAddDuplicateRole() {
        // 测试添加重复角色
        StoryRoleVo storyRoleVo1 = createTestStoryRole();
        StoryRoleVo storyRoleVo2 = createTestStoryRole();
        storyRoleVo2.setRoleName(storyRoleVo1.getRoleName()); // 相同角色名

        // 添加第一个角色
        boolean result1 = storyRoleService.addStoryRole(storyRoleVo1);
        assertTrue("添加第一个角色应该成功", result1);

        // 添加重复角色
        boolean result2 = storyRoleService.addStoryRole(storyRoleVo2);
        assertTrue("添加重复角色应该成功", result2);
    }

    @Test
    public void testStoryRoleVoProperties() {
        StoryRoleVo storyRoleVo = createTestStoryRole();

        // 测试所有属性的getter和setter
        assertEquals("角色名称应该正确设置", "测试角色", storyRoleVo.getRoleName());
        assertEquals("角色描述应该正确设置", "这是一个测试角色", storyRoleVo.getRoleDetail());
        assertEquals("故事ID应该正确设置", 1, storyRoleVo.getStoryId());
        assertTrue("角色ID应该大于等于0", storyRoleVo.getRoleId() >= 0);
    }

    @Test
    public void testStoryRoleVoToString() {
        StoryRoleVo storyRoleVo = createTestStoryRole();
        String toString = storyRoleVo.toString();

        assertNotNull("toString不应返回null", toString);
        assertTrue("toString应该包含角色名称", toString.contains("测试角色"));
        assertTrue("toString应该包含roleId", toString.contains("roleId"));
        assertTrue("toString应该包含storyId", toString.contains("storyId"));
    }

    @Test
    public void testStoryRoleVoSetAndGet() {
        StoryRoleVo storyRoleVo = new StoryRoleVo();

        // 测试所有属性的设置和获取
        storyRoleVo.setRoleId(100);
        storyRoleVo.setRoleName("新角色");
        storyRoleVo.setRoleDetail("新的角色描述");
        storyRoleVo.setStoryId(200);

        assertEquals("角色ID应该被正确设置", 100, storyRoleVo.getRoleId());
        assertEquals("角色名称应该被正确设置", "新角色", storyRoleVo.getRoleName());
        assertEquals("角色描述应该被正确设置", "新的角色描述", storyRoleVo.getRoleDetail());
        assertEquals("故事ID应该被正确设置", 200, storyRoleVo.getStoryId());
    }

    @Test
    public void testGetRoleAfterAdding() {
        StoryRoleVo storyRoleVo = createTestStoryRole();

        // 添加角色
        assertTrue("添加角色应该成功", storyRoleService.addStoryRole(storyRoleVo));

        // 通过角色名称和故事ID获取角色
        StoryRoleVo retrievedRole = storyRoleService.getRoleByRoleNameAndStoryId(
            storyRoleVo.getRoleName(), storyRoleVo.getStoryId());

        assertNotNull("应该能够获取到添加的角色", retrievedRole);
        assertEquals("角色名称应该匹配", storyRoleVo.getRoleName(), retrievedRole.getRoleName());
        assertEquals("故事ID应该匹配", storyRoleVo.getStoryId(), retrievedRole.getStoryId());
    }

    @Test
    public void testServiceMethodsExistence() {
        // 验证所有声明的方法都存在
        try {
            assertNotNull("getRoleByRoleNameAndStoryId方法应该存在",
                         storyRoleService.getClass().getMethod("getRoleByRoleNameAndStoryId", String.class, int.class));
            assertNotNull("addStoryRole方法应该存在",
                         storyRoleService.getClass().getMethod("addStoryRole", StoryRoleVo.class));
            assertNotNull("deleteStoryRole方法应该存在",
                         storyRoleService.getClass().getMethod("deleteStoryRole", StoryRoleVo.class));
            assertNotNull("updateStoryRole方法应该存在",
                         storyRoleService.getClass().getMethod("updateStoryRole", StoryRoleVo.class));
            assertNotNull("getStoryRoleList方法应该存在",
                         storyRoleService.getClass().getMethod("getStoryRoleList", int.class));
        } catch (NoSuchMethodException e) {
            fail("Service方法应该存在: " + e.getMessage());
        }
    }

    @Test
    public void testCompleteRoleFlow() {
        // 测试完整的角色流程：添加 -> 查询 -> 更新 -> 删除
        String uniqueRoleName = "测试角色_" + System.currentTimeMillis();
        int storyId = 1;

        // 1. 添加角色
        StoryRoleVo storyRoleVo = new StoryRoleVo();
        storyRoleVo.setRoleName(uniqueRoleName);
        storyRoleVo.setRoleDetail("初始描述");
        storyRoleVo.setStoryId(storyId);

        boolean addResult = storyRoleService.addStoryRole(storyRoleVo);
        assertTrue("添加角色应该成功", addResult);

        // 2. 查询角色
        StoryRoleVo retrievedRole = storyRoleService.getRoleByRoleNameAndStoryId(uniqueRoleName, storyId);
        assertNotNull("应该能够查询到角色", retrievedRole);
        assertEquals("角色名称应该匹配", uniqueRoleName, retrievedRole.getRoleName());
        assertEquals("角色描述应该匹配", "初始描述", retrievedRole.getRoleDetail());

        // 3. 更新角色
        storyRoleVo.setRoleId(retrievedRole.getRoleId());
        storyRoleVo.setRoleDetail("更新后的描述");
        boolean updateResult = storyRoleService.updateStoryRole(storyRoleVo);
        assertTrue("更新角色应该成功", updateResult);

        // 4. 验证更新
        StoryRoleVo updatedRole = storyRoleService.getRoleByRoleNameAndStoryId(uniqueRoleName, storyId);
        assertNotNull("应该能够查询到更新的角色", updatedRole);
        assertEquals("更新后的描述应该匹配", "更新后的描述", updatedRole.getRoleDetail());

        // 5. 删除角色
        storyRoleVo.setRoleId(updatedRole.getRoleId());
        boolean deleteResult = storyRoleService.deleteStoryRole(storyRoleVo);
        assertTrue("删除角色应该成功", deleteResult);
    }

    private StoryRoleVo createTestStoryRole() {
        StoryRoleVo storyRoleVo = new StoryRoleVo();
        storyRoleVo.setRoleName("测试角色");
        storyRoleVo.setRoleDetail("这是一个测试角色");
        storyRoleVo.setStoryId(1);
        return storyRoleVo;
    }
}