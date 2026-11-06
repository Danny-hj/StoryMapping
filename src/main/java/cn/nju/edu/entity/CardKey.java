package cn.nju.edu.entity;

import java.io.Serializable;
import java.util.Objects;

public class CardKey implements Serializable{
    private int positionX;
    private int positionY;
    private int storyId;

    public int getPositionX() {
        return positionX;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    public int getStoryId() {
        return storyId;
    }

    public void setStoryId(int storyId) {
        this.storyId = storyId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CardKey cardKey = (CardKey) o;
        return positionX == cardKey.positionX
                && positionY == cardKey.positionY
                && storyId == cardKey.storyId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(positionX, positionY, storyId);
    }

    @Override
    public String toString() {
        return "CardKey [position_x=" + positionX + ", position_y=" + positionY
                + ", story_id=" + storyId + "]";
    }
}
