package paul.TextQuest.entities.obstacles;

import paul.TextQuest.entities.BackpackItem;
import paul.TextQuest.entities.DungeonRoom;
import paul.TextQuest.entities.Hero;
import paul.TextQuest.entities.Monster;
import paul.TextQuest.interfaces.VoidAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pauldennis on 8/21/17.
 */

//Represents an obstacle that is "smashable" (like a wooden barrel, or weak stonework)
//By someone of sufficient strength
public class SmashableObstacle extends Obstacle {

    private int requiredMight;
    private String onSmash;

    private List<BackpackItem> contents;

    public SmashableObstacle () {
        contents = new ArrayList<>();
    }

    public boolean attempt (String input, Hero hero) {
        DungeonRoom room = hero.getLocation();
        //assert: input should = "smash"
        if (hero.getMight() >= requiredMight) {
            this.setCleared(true);
            if (onSmash != null) {
                room.doAction(onSmash);
            }
            if (contents != null && contents.size() > 0) {
                contents.forEach(room::addItem);
                contents = new ArrayList<>();
            }
            //No xp just for smashing stuff
            return true;
        } else {
            return false;
        }
    }

    private static Map<String, VoidAction> smashActionMap;

    public int getRequiredMight() {
        return requiredMight;
    }

    public void setRequiredMight(int requiredMight) {
        this.requiredMight = requiredMight;
    }

    public String getOnSmash() {
        return onSmash;
    }

    public void setOnSmash(String onSmash) {
        this.onSmash = onSmash;
    }

    public List<BackpackItem> getContents() {
        return contents;
    }

    public void setContents(List<BackpackItem> contents) {
        this.contents = contents;
    }
}
