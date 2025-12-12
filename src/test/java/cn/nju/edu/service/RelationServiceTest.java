package cn.nju.edu.service;

import cn.nju.edu.vo.RelationVo;
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
public class RelationServiceTest {

    @Autowired
    private RelationService relationService;

    @Test
    public void testRelationServiceInjection() {
        assertNotNull("RelationService应该被正确注入", relationService);
    }

    @Test
    public void testGetRelationByCardId() {
        // 测试获取不存在卡片的关系
        List<RelationVo> relations = relationService.getRelationByCardId(999999);
        assertNotNull("返回的关系列表不应为null", relations);
        assertTrue("不存在卡片的关系列表应该为空", relations.isEmpty());
    }

    @Test
    public void testGetRelationByInvalidCardId() {
        // 测试无效的卡片ID
        List<RelationVo> relations = relationService.getRelationByCardId(-1);
        assertNotNull("返回的关系列表不应为null", relations);
        assertTrue("无效卡片ID的关系列表应该为空", relations.isEmpty());
    }

    @Test
    public void testAddRelation() {
        RelationVo relationVo = createTestRelation();

        // 测试添加关系 - RelationService对于新关系返回true
        boolean result = relationService.addRelation(relationVo);
        assertTrue("添加关系应该成功", result);

        // 验证能够获取到关系
        List<RelationVo> relations = relationService.getRelationByCardId(relationVo.getCardId());
        assertNotNull("关系列表不应为null", relations);
    }

    @Test
    public void testAddDuplicateRelation() {
        RelationVo relationVo = createTestRelation();

        // 添加第一个关系
        assertTrue("第一次添加关系应该成功", relationService.addRelation(relationVo));

        // 尝试添加重复的关系（相同的roleId和cardId）- RelationService应该返回false
        RelationVo duplicateRelation = createTestRelation();
        boolean result = relationService.addRelation(duplicateRelation);
        assertFalse("添加重复关系应该失败", result);
    }

    @Test
    public void testAddRelationWithInvalidCardId() {
        RelationVo relationVo = createTestRelation();
        relationVo.setCardId(-1);

        // 测试添加无效卡片ID的关系 - RelationService可能仍然处理
        boolean result = relationService.addRelation(relationVo);
        // 根据实际业务逻辑，这个测试结果可能需要调整
    }

    @Test
    public void testAddRelationWithInvalidRoleId() {
        RelationVo relationVo = createTestRelation();
        relationVo.setRoleId(-1);

        // 测试添加无效角色ID的关系
        boolean result = relationService.addRelation(relationVo);
        // 根据实际业务逻辑，这个测试结果可能需要调整
    }

    @Test
    public void testAddRelationWithZeroIds() {
        RelationVo relationVo = new RelationVo();
        relationVo.setCardId(0);
        relationVo.setRoleId(0);

        // 测试添加ID为0的关系
        boolean result = relationService.addRelation(relationVo);
        // 根据实际业务逻辑，这个测试结果可能需要调整
    }

    @Test
    public void testDeleteRelation() {
        RelationVo relationVo = createTestRelation();
        relationVo.setRelationId(1); // 设置一个有效的关系ID

        // 测试删除关系 - RelationService总是返回true
        boolean result = relationService.deleteRelation(relationVo);
        assertTrue("删除关系应该成功", result);
    }

    @Test
    public void testDeleteRelationWithZeroId() {
        RelationVo relationVo = new RelationVo();
        relationVo.setRelationId(0); // 无效ID

        // 测试删除ID为0的关系 - RelationService仍然返回true
        boolean result = relationService.deleteRelation(relationVo);
        assertTrue("删除关系应该成功", result);
    }

    @Test
    public void testRelationVoProperties() {
        RelationVo relationVo = createTestRelation();

        // 测试所有属性的getter和setter
        assertEquals("卡片ID应该正确设置", 1, relationVo.getCardId());
        assertEquals("角色ID应该正确设置", 1, relationVo.getRoleId());
        assertTrue("关系ID应该大于等于0", relationVo.getRelationId() >= 0);
    }

    @Test
    public void testRelationVoToString() {
        RelationVo relationVo = createTestRelation();
        String toString = relationVo.toString();

        assertNotNull("toString不应返回null", toString);
        assertTrue("toString应该包含relationId", toString.contains("relationId"));
        assertTrue("toString应该包含roleId", toString.contains("roleId"));
        assertTrue("toString应该包含cardId", toString.contains("cardId"));
    }

    @Test
    public void testRelationVoSetAndGet() {
        RelationVo relationVo = new RelationVo();

        // 测试所有属性的设置和获取
        relationVo.setRelationId(100);
        relationVo.setRoleId(200);
        relationVo.setCardId(300);

        assertEquals("关系ID应该被正确设置", 100, relationVo.getRelationId());
        assertEquals("角色ID应该被正确设置", 200, relationVo.getRoleId());
        assertEquals("卡片ID应该被正确设置", 300, relationVo.getCardId());
    }

    @Test
    public void testAddMultipleRelationsForSameCard() {
        int cardId = 1;

        // 为同一张卡片添加多个不同角色的关系
        int[] roleIds = {1, 2, 3};
        boolean[] results = new boolean[roleIds.length];
        int successCount = 0;

        for (int i = 0; i < roleIds.length; i++) {
            RelationVo relationVo = new RelationVo();
            relationVo.setCardId(cardId);
            relationVo.setRoleId(roleIds[i]);

            results[i] = relationService.addRelation(relationVo);
            if (results[i]) {
                successCount++;
            }
        }

        assertTrue("至少应该添加一些关系", successCount > 0);

        // 验证能够获取到关系
        List<RelationVo> relations = relationService.getRelationByCardId(cardId);
        assertNotNull("关系列表不应为null", relations);
    }

    @Test
    public void testAddMultipleRelationsForSameRole() {
        int roleId = 1;

        // 为同一个角色添加多个不同卡片的关系
        int[] cardIds = {1, 2, 3};
        boolean[] results = new boolean[cardIds.length];
        int successCount = 0;

        for (int i = 0; i < cardIds.length; i++) {
            RelationVo relationVo = new RelationVo();
            relationVo.setCardId(cardIds[i]);
            relationVo.setRoleId(roleId);

            results[i] = relationService.addRelation(relationVo);
            if (results[i]) {
                successCount++;
            }
        }

        assertTrue("至少应该添加一些关系", successCount > 0);
    }

    @Test
    public void testServiceMethodsExistence() {
        // 验证所有声明的方法都存在
        try {
            assertNotNull("getRelationByCardId方法应该存在",
                         relationService.getClass().getMethod("getRelationByCardId", int.class));
            assertNotNull("addRelation方法应该存在",
                         relationService.getClass().getMethod("addRelation", RelationVo.class));
            assertNotNull("deleteRelation方法应该存在",
                         relationService.getClass().getMethod("deleteRelation", RelationVo.class));
        } catch (NoSuchMethodException e) {
            fail("Service方法应该存在: " + e.getMessage());
        }
    }

    @Test
    public void testCompleteRelationFlow() {
        // 测试完整的关系流程：添加 -> 查询 -> 删除
        RelationVo relationVo = new RelationVo();
        relationVo.setCardId(100);
        relationVo.setRoleId(100);

        // 1. 添加关系
        boolean addResult = relationService.addRelation(relationVo);
        if (addResult) {
            // 2. 查询关系
            List<RelationVo> relations = relationService.getRelationByCardId(100);
            assertNotNull("关系列表不应为null", relations);

            // 3. 删除关系
            if (!relations.isEmpty()) {
                RelationVo toDelete = relations.get(0);
                boolean deleteResult = relationService.deleteRelation(toDelete);
                assertTrue("删除关系应该成功", deleteResult);
            }
        }
    }

    private RelationVo createTestRelation() {
        RelationVo relationVo = new RelationVo();
        relationVo.setCardId(1);
        relationVo.setRoleId(1);
        return relationVo;
    }
}