package paul.NLPTextDungeon.entities.obstacles;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import paul.NLPTextDungeon.entities.DungeonRoomEntity;
import paul.NLPTextDungeon.entities.Hero;

/**
 * Created by Paul Dennis on 8/13/2017.
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include= JsonTypeInfo.As.PROPERTY, property="@class")
public abstract class Obstacle extends DungeonRoomEntity {


    private String name;
    private String solution;

    //Cleared variable represents whether the room can be freely traversed
    //Up to the implementing class to decide how it works (whether cleared is permanent, etc)
    private boolean isCleared;

    //TODO Implement
    //Represents whether this is a "major obstacle" puzzle or just a smashed barrel or something
    private boolean displayIfCleared;

    public Obstacle () {

    }

    public Obstacle(String name, String solution, boolean isCleared) {
        this.name = name;
        this.solution = solution;
        this.isCleared = isCleared;
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
        String response = name + " , Cleared: ";
        if (isCleared) {
            response += "yes";
        } else {
            response += "no";
        }
        return response;
    }
}
