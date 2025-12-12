package cn.nju.edu.repository;

import cn.nju.edu.entity.Card;
import cn.nju.edu.entity.CardKey;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
public class CardRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CardRepository cardRepository;

    @Test
    public void testFindById() {
        // 准备测试数据
        Card card = new Card();
        card.setPositionX(1);
        card.setPositionY(2);
        card.setStoryId(1);
        card.setTitle("Test Card");
        card.setContent("Test Content");
        card.setState(1);
        card.setCost(5);

        CardKey id = new CardKey();
        id.setPositionX(1);
        id.setPositionY(2);
        id.setStoryId(1);

        Card savedCard = entityManager.persistAndFlush(card);

        // 执行测试
        Optional<Card> foundCard = cardRepository.findById(id);

        // 验证结果
        assertTrue(foundCard.isPresent());
        assertEquals("Test Card", foundCard.get().getTitle());
        assertEquals("Test Content", foundCard.get().getContent());
        assertSame(Integer.valueOf(1), foundCard.get().getState());
        assertSame(Integer.valueOf(5), foundCard.get().getCost());
    }

    @Test
    public void testFindByIdNotFound() {
        // 查找不存在的Card
        CardKey id = new CardKey();
        id.setPositionX(999);
        id.setPositionY(999);
        id.setStoryId(999);

        Optional<Card> foundCard = cardRepository.findById(id);
        assertFalse(foundCard.isPresent());
    }

    @Test
    public void testFindByStoryId() {
        // 准备测试数据
        Card card = new Card();
        card.setPositionX(1);
        card.setPositionY(2);
        card.setStoryId(123);
        card.setTitle("Test Card");
        card.setContent("Test Content");
        card.setState(1);
        card.setCost(5);

        entityManager.persistAndFlush(card);

        // 执行测试
        java.util.List<Card> cards = cardRepository.findByStoryId(123);

        // 验证结果
        assertNotNull(cards);
        assertFalse(cards.isEmpty());
        assertEquals("Test Card", cards.get(0).getTitle());
    }

    @Test
    public void testFindByStoryIdNotFound() {
        // 查找不存在的storyId
        java.util.List<Card> cards = cardRepository.findByStoryId(99999);
        assertNotNull(cards);
        assertTrue(cards.isEmpty());
    }

    @Test
    public void testSave() {
        // 准备测试数据
        Card card = new Card();
        card.setPositionX(3);
        card.setPositionY(4);
        card.setStoryId(1);
        card.setTitle("New Card");
        card.setContent("New Content");
        card.setState(2);
        card.setCost(10);

        // 执行保存
        Card savedCard = cardRepository.save(card);

        // 验证结果
        assertNotNull(savedCard);
        assertEquals("New Card", savedCard.getTitle());
        assertEquals("New Content", savedCard.getContent());
        assertSame(Integer.valueOf(2), savedCard.getState());
        assertSame(Integer.valueOf(10), savedCard.getCost());
    }

    @Test
    public void testDelete() {
        // 准备测试数据
        Card card = new Card();
        card.setPositionX(5);
        card.setPositionY(6);
        card.setStoryId(1);
        card.setTitle("To Delete");
        card.setContent("To be deleted");
        card.setState(1);
        card.setCost(3);

        entityManager.persistAndFlush(card);

        // 执行删除
        CardKey id = new CardKey();
        id.setPositionX(5);
        id.setPositionY(6);
        id.setStoryId(1);

        cardRepository.deleteById(id);

        // 验证删除
        Optional<Card> foundCard = cardRepository.findById(id);
        assertFalse(foundCard.isPresent());
    }

    @Test
    public void testUpdate() {
        // 准备测试数据
        Card card = new Card();
        card.setPositionX(7);
        card.setPositionY(8);
        card.setStoryId(1);
        card.setTitle("Original Title");
        card.setContent("Original Content");
        card.setState(1);
        card.setCost(5);

        Card savedCard = entityManager.persistAndFlush(card);

        // 修改数据
        savedCard.setTitle("Updated Title");
        savedCard.setContent("Updated Content");
        savedCard.setState(2);
        savedCard.setCost(8);

        // 执行更新
        Card updatedCard = cardRepository.save(savedCard);

        // 验证更新
        assertEquals("Updated Title", updatedCard.getTitle());
        assertEquals("Updated Content", updatedCard.getContent());
        assertSame(Integer.valueOf(2), updatedCard.getState());
        assertSame(Integer.valueOf(8), updatedCard.getCost());
    }
}