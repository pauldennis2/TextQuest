package paul.TextQuest.entities.obstacles;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import paul.TextQuest.entities.DungeonRoom;
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
    
    private String description;
    
    private String onClear;
    protected String onAttempt;

    private List<Direction> blockedDirections;
    
    private transient DungeonRoom location;

    public Obstacle () {
        blockedDirections = new ArrayList<>();
    }

    public Obstacle(String name, String solution, boolean isCleared) {
        this.name = name;
        this.solution = solution;
        this.isCleared = isCleared;
        blockedDirections = new ArrayList<>();
    }

    public abstract boolean attempt (String solution, Hero hero);

    public boolean isCleared() {
        return isCleared;
    }

    public void setCleared(boolean cleared) {
    	if (cleared && onClear != null) {
    		location.doAction(onClear);
    	}
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

	public String toLongString() {
		return "Obstacle [name=" + name + ", solution=" + solution + ", isCleared=" + isCleared + ", blocksLooting="
				+ blocksLooting + ", blockedDirections=" + blockedDirections + "]";
	}
	
	public void setLocation (DungeonRoom location) {
		this.location = location;
	}

	public String getOnClear() {
		return onClear;
	}

	public void setOnClear(String onClear) {
		this.onClear = onClear;
	}

	public String getOnAttempt() {
		return onAttempt;
	}

	public void setOnAttempt(String onAttempt) {
		this.onAttempt = onAttempt;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
