package paul.TextQuest.entities.obstacles;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import paul.TextQuest.entities.DungeonRoomEntity;
import paul.TextQuest.entities.Hero;
import paul.TextQuest.enums.Direction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul Dennis on 8/13/2017.
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include= JsonTypeInfo.As.PROPERTY, property="@class")
public abstract class Obstacle extends DungeonRoomEntity {


    private String name;
    private String solution;
    private int expAmount;
    public static final int DEFAULT_XP_AMT = 25;

    //Cleared variable represents whether the room can be freely traversed
    //Up to the implementing class to decide how it works (whether cleared is permanent, etc)
    private boolean isCleared;

    //Represents whether this is a "major obstacle" puzzle or just a smashed barrel or something
    private boolean displayIfCleared;
    private boolean blocksLooting;

    private List<Direction> blockedDirections;

    public Obstacle () {
        blockedDirections = new ArrayList<>();
        blockedDirections.add(Direction.NONE);
    }

    public Obstacle(String name, String solution, boolean isCleared) {
        this.name = name;
        this.solution = solution;
        this.isCleared = isCleared;
        blockedDirections = new ArrayList<>();
        blockedDirections.add(Direction.NONE);
    }

    public abstract boolean attempt (String solution, Hero hero);

    public boolean isCleared() {
        return isCleared;
    }

    protected void setCleared(boolean cleared) {
        isCleared = cleared;
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public String getSolution() {
        return solution;
    }

    protected void setSolution(String solution) {
        this.solution = solution;
    }

    @Override
    public String toString () {
        String response = name;

        if (isCleared) {
            response += " (cleared)";
        }
        return response;
    }

    public int getExpAmount() {
        return expAmount;
    }

    public void setExpAmount(int expAmount) {
        this.expAmount = expAmount;
    }

    public boolean isDisplayIfCleared() {
        return displayIfCleared;
    }

    public void setDisplayIfCleared(boolean displayIfCleared) {
        this.displayIfCleared = displayIfCleared;
    }

    public List<Direction> getBlockedDirections() {
        return blockedDirections;
    }

    public void setBlockedDirections(List<Direction> blockedDirections) {
        this.blockedDirections = blockedDirections;
    }

    public void addBlockedDirection(Direction direction) {
        if (blockedDirections.get(0) == Direction.NONE) {
            blockedDirections.remove(0);
        }
        blockedDirections.add(direction);
    }
    
    public void setBlocksLooting (boolean blocksLooting) {
    	this.blocksLooting = blocksLooting;
    }
    
    public boolean blocksLooting () {
    	return blocksLooting;
    }
}
