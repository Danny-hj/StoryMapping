package cn.nju.edu.repository;

import cn.nju.edu.entity.StoryMapKey;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import cn.nju.edu.entity.StoryMap;

import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
public class StoryMapRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StoryMapRepository storyMapRepository;

    @Test
    public void testFindById() {
        // 准备测试数据
        StoryMap storyMap = new StoryMap();
        storyMap.setUserId(1);
        storyMap.setStoryName("TestStory");
        storyMap.setStoryDescription("Test Description");
        storyMap.setRelease(1);

        StoryMap savedStoryMap = entityManager.persistAndFlush(storyMap);

        // 执行测试
        StoryMapKey id = new StoryMapKey();
        id.setStoryName("TestStory");
        id.setUserId(1);

        Optional<StoryMap> foundStoryMap = storyMapRepository.findById(id);

        // 验证结果
        assertTrue(foundStoryMap.isPresent());
        assertEquals(savedStoryMap.getStoryDescription(), foundStoryMap.get().getStoryDescription());
        assertEquals(savedStoryMap.getRelease(), foundStoryMap.get().getRelease());
    }

    @Test
    public void testFindByIdNotFound() {
        // 查找不存在的StoryMap
        StoryMapKey id = new StoryMapKey();
        id.setStoryName("NonExistent");
        id.setUserId(999);

        Optional<StoryMap> foundStoryMap = storyMapRepository.findById(id);
        assertFalse(foundStoryMap.isPresent());
    }

    @Test
    public void testFindByStoryId() {
        // 准备测试数据
        StoryMap storyMap = new StoryMap();
        storyMap.setUserId(1);
        storyMap.setStoryName("TestStory");
        storyMap.setStoryDescription("Test Description");
        storyMap.setRelease(1);

        entityManager.persistAndFlush(storyMap);

        // 执行测试
        StoryMap foundStoryMap = storyMapRepository.findByStoryId(storyMap.getStoryId());

        // 验证结果
        assertNotNull(foundStoryMap);
        assertEquals("TestStory", foundStoryMap.getStoryName());
        assertEquals("Test Description", foundStoryMap.getStoryDescription());
    }

    @Test
    public void testFindByStoryIdNotFound() {
        // 查找不存在的storyId
        StoryMap foundStoryMap = storyMapRepository.findByStoryId(99999);
        assertNull(foundStoryMap);
    }

    @Test
    public void testSave() {
        // 准备测试数据
        StoryMap storyMap = new StoryMap();
        storyMap.setUserId(1);
        storyMap.setStoryName("NewStory");
        storyMap.setStoryDescription("New Description");
        storyMap.setRelease(2);

        // 执行保存
        StoryMap savedStoryMap = storyMapRepository.save(storyMap);

        // 验证结果
        assertNotNull(savedStoryMap);
        assertNotNull(savedStoryMap.getStoryId());
        assertEquals("NewStory", savedStoryMap.getStoryName());
        assertEquals("New Description", savedStoryMap.getStoryDescription());
        assertSame(Integer.valueOf(2), savedStoryMap.getRelease());
    }

    @Test
    public void testDelete() {
        // 准备测试数据
        StoryMap storyMap = new StoryMap();
        storyMap.setUserId(1);
        storyMap.setStoryName("ToDelete");
        storyMap.setStoryDescription("To be deleted");
        storyMap.setRelease(1);

        StoryMap savedStoryMap = entityManager.persistAndFlush(storyMap);

        // 执行删除
        StoryMapKey id = new StoryMapKey();
        id.setStoryName("ToDelete");
        id.setUserId(1);

        storyMapRepository.deleteById(id);

        // 验证删除
        Optional<StoryMap> foundStoryMap = storyMapRepository.findById(id);
        assertFalse(foundStoryMap.isPresent());
    }

    @Test
    public void testUpdate() {
        // 准备测试数据
        StoryMap storyMap = new StoryMap();
        storyMap.setUserId(1);
        storyMap.setStoryName("ToUpdate");
        storyMap.setStoryDescription("Original Description");
        storyMap.setRelease(1);

        StoryMap savedStoryMap = entityManager.persistAndFlush(storyMap);

        // 修改数据
        savedStoryMap.setStoryDescription("Updated Description");
        savedStoryMap.setRelease(2);

        // 执行更新
        StoryMap updatedStoryMap = storyMapRepository.save(savedStoryMap);

        // 验证更新
        assertEquals("Updated Description", updatedStoryMap.getStoryDescription());
        assertSame(Integer.valueOf(2), updatedStoryMap.getRelease());
    }
}