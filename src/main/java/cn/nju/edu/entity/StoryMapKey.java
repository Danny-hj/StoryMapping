package cn.nju.edu.entity;

import java.io.Serializable;
import java.util.Objects;

public class StoryMapKey implements Serializable {

    private String storyName;

    private int userId;

    public String getStoryName() {
        return storyName;
    }

    public void setStoryName(String storyName) {
        this.storyName = storyName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StoryMapKey that = (StoryMapKey) o;
        return userId == that.userId && Objects.equals(storyName, that.storyName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storyName, userId);
    }

    @Override
    public String toString() {
        return "StoryMapKey [story_name=" + storyName + ", user_id=" + userId + "]";
    }
}