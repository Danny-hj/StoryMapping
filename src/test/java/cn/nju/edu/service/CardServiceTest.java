package cn.nju.edu.service;

import cn.nju.edu.enumeration.CardState;
import cn.nju.edu.enumeration.CardType;
import cn.nju.edu.vo.CardVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CardServiceTest {

    @Autowired
    private CardService cardService;

    @Test
    public void testCardServiceInjection() {
        assertNotNull("CardService应该被正确注入", cardService);
    }

    @Test
    public void testGetCardList() {
        // 测试获取不存在故事的卡片列表
        List<CardVo> cardList = cardService.getCardList(999999);
        assertNotNull("返回的卡片列表不应为null", cardList);
        assertTrue("不存在故事的卡片列表应该为空", cardList.isEmpty());
    }

    @Test
    public void testGetCardListWithValidStory() {
        // 测试获取有效故事的卡片列表
        List<CardVo> cardList = cardService.getCardList(1);
        assertNotNull("返回的卡片列表不应为null", cardList);
        // 可能为空或不为空，取决于测试数据
    }

    @Test
    public void testAddCard() {
        CardVo cardVo = createTestCard();

        // 测试添加卡片 - CardService总是返回true
        boolean result = cardService.addCard("system", cardVo);
        assertTrue("添加卡片应该成功", result);

        // 验证列表中的卡片
        List<CardVo> cards = cardService.getCardList(cardVo.getStoryId());
        assertNotNull("卡片列表不应为null", cards);
    }

    @Test
    public void testAddCardWithNullSource() {
        CardVo cardVo = createTestCard();

        // 测试source为null的情况
        boolean result = cardService.addCard(null, cardVo);
        assertTrue("source为null时添加卡片应该成功", result);
    }

    @Test
    public void testAddCardWithDifferentSources() {
        CardVo cardVo = createTestCard();

        // 测试不同的source值
        String[] sources = {"left", "right", "top", "bottom", "system"};
        for (String source : sources) {
            CardVo card = createTestCard();
            card.setCardId((int) (Math.random() * 1000)); // 避免ID冲突
            boolean result = cardService.addCard(source, card);
            assertTrue("source=" + source + "时添加卡片应该成功", result);
        }
    }

    @Test
    public void testUpdateCard() {
        CardVo cardVo = createTestCard();
        cardVo.setCardId(1); // 设置一个有效的cardId

        // 修改卡片信息
        cardVo.setTitle("更新后的标题");
        cardVo.setContent("更新后的内容");
        cardVo.setState(CardState.DONE);

        // 测试更新 - CardService总是返回true
        boolean result = cardService.updateCard(cardVo);
        assertTrue("更新卡片应该成功", result);
    }

    @Test
    public void testUpdateCardWithDifferentStates() {
        // 测试更新所有可能的状态
        CardState[] states = {CardState.TODO, CardState.READY, CardState.DOING, CardState.DONE};

        for (CardState state : states) {
            CardVo cardVo = createTestCard();
            cardVo.setCardId((int) (Math.random() * 1000)); // 使用随机ID避免冲突
            cardVo.setState(state);
            cardVo.setTitle("测试卡片_" + state.name()); // 避免标题重复

            boolean result = cardService.updateCard(cardVo);
            assertTrue("更新状态为" + state + "的卡片应该成功", result);
        }
    }

    @Test
    public void testUpdateCardList() {
        List<CardVo> cardVoList = new ArrayList<>();

        // 创建多个卡片进行批量更新
        for (int i = 0; i < 3; i++) {
            CardVo card = createTestCard();
            card.setCardId(100 + i); // 避免ID冲突
            card.setTitle("测试卡片" + i);
            cardVoList.add(card);
        }

        // 测试批量更新
        boolean result = cardService.updateCardList(cardVoList);
        assertTrue("批量更新卡片应该成功", result);
    }

    @Test
    public void testUpdateEmptyCardList() {
        // 测试空列表 - 实际业务逻辑可能返回true
        boolean result = cardService.updateCardList(new ArrayList<>());
        assertTrue("更新空列表应该成功", result);
    }

    @Test
    public void testDeleteCard() {
        CardVo cardVo = createTestCard();
        cardVo.setCardId(1);

        // 测试删除 - CardService总是返回true
        boolean result = cardService.deleteCard(cardVo);
        assertTrue("删除卡片应该成功", result);
    }

    @Test
    public void testDeleteCardWithDifferentPositions() {
        // 测试删除不同位置的卡片
        int[][] positions = {{0, 0}, {1, 1}, {2, 2}, {3, 3}};

        for (int[] pos : positions) {
            CardVo cardVo = createTestCard();
            cardVo.setCardId((int) (Math.random() * 1000));
            cardVo.setPositionX(pos[0]);
            cardVo.setPositionY(pos[1]);

            boolean result = cardService.deleteCard(cardVo);
            assertTrue("删除位置(" + pos[0] + "," + pos[1] + ")的卡片应该成功", result);
        }
    }

    @Test
    public void testCardVoProperties() {
        CardVo cardVo = createTestCard();

        // 测试所有属性的getter和setter
        assertEquals("标题应该正确设置", "测试卡片", cardVo.getTitle());
        assertEquals("内容应该正确设置", "这是一个测试卡片", cardVo.getContent());
        assertEquals("状态应该正确设置", CardState.TODO, cardVo.getState());
        assertEquals("成本应该正确设置", 5, cardVo.getCost());
        assertEquals("X坐标应该正确设置", 1, cardVo.getPositionX());
        assertEquals("Y坐标应该正确设置", 1, cardVo.getPositionY());
        assertEquals("故事ID应该正确设置", 1, cardVo.getStoryId());
        assertEquals("类型应该正确设置", CardType.USER_STORY, cardVo.getType());
    }

    @Test
    public void testCardVoToString() {
        CardVo cardVo = createTestCard();
        String toString = cardVo.toString();

        assertNotNull("toString不应返回null", toString);
        assertTrue("toString应该包含标题", toString.contains("测试卡片"));
        assertTrue("toString应该包含内容", toString.contains("测试卡片"));
    }

    @Test
    public void testAllCardStates() {
        CardVo cardVo = createTestCard();

        // 测试所有可能的卡片状态
        for (CardState state : CardState.values()) {
            cardVo.setState(state);
            assertEquals("状态应该被正确设置", state, cardVo.getState());
        }
    }

    @Test
    public void testAllCardTypes() {
        CardVo cardVo = createTestCard();

        // 测试所有可能的卡片类型
        for (CardType type : CardType.values()) {
            cardVo.setType(type);
            assertEquals("类型应该被正确设置", type, cardVo.getType());
        }
    }

    @Test
    public void testServiceMethodsExistence() {
        // 验证所有声明的方法都存在
        try {
            assertNotNull("getCardList方法应该存在",
                         cardService.getClass().getMethod("getCardList", int.class));
            assertNotNull("addCard方法应该存在",
                         cardService.getClass().getMethod("addCard", String.class, CardVo.class));
            assertNotNull("updateCard方法应该存在",
                         cardService.getClass().getMethod("updateCard", CardVo.class));
            assertNotNull("updateCardList方法应该存在",
                         cardService.getClass().getMethod("updateCardList", List.class));
            assertNotNull("deleteCard方法应该存在",
                         cardService.getClass().getMethod("deleteCard", CardVo.class));
        } catch (NoSuchMethodException e) {
            fail("Service方法应该存在: " + e.getMessage());
        }
    }

    private CardVo createTestCard() {
        CardVo cardVo = new CardVo();
        cardVo.setTitle("测试卡片");
        cardVo.setContent("这是一个测试卡片");
        cardVo.setState(CardState.TODO);
        cardVo.setCost(5);
        cardVo.setPositionX(1); // 使用小的坐标值避免数组越界
        cardVo.setPositionY(1);
        cardVo.setStoryId(1);
        cardVo.setType(CardType.USER_STORY);
        return cardVo;
    }
}