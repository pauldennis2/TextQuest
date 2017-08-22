package paul.NLPTextDungeon.entities.obstacles;

import paul.NLPTextDungeon.entities.BackpackItem;
import paul.NLPTextDungeon.entities.DungeonRoom;
import paul.NLPTextDungeon.entities.Hero;
import paul.NLPTextDungeon.interfaces.VoidAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pauldennis on 8/21/17.
 */

//Represents an obstacle that is "smashable" (like a wooden barrel, or weak stonework)
//By someone of sufficient strength
public class SmashableObstacle extends Obstacle {

    private int requiredStrength;
    private String onSmash;

    private List<BackpackItem> contents;

    private transient VoidAction onSmashed;

    public SmashableObstacle () {
        contents = new ArrayList<>();
    }

    public SmashableObstacle (int requiredStrength) {
        this.requiredStrength = requiredStrength;
    }

    public void setOnSmashed(VoidAction onSmashed) {
        this.onSmashed = onSmashed;
    }

    public boolean attempt (String input, Hero hero) {
        DungeonRoom room = hero.getLocation();
        //assert: input should = "smash"
        if (hero.getMight() >= requiredStrength) {
            this.setCleared(true);
            if (onSmashed != null) {
                onSmashed.doAction(room);
            }
            if (contents != null && contents.size() > 0) {
                contents.forEach(room::addItem);
                contents = new ArrayList<>();
            }
            return true;
        } else {
            return false;
        }
    }

    public int getRequiredStrength() {
        return requiredStrength;
    }

    public void setRequiredStrength(int requiredStrength) {
        this.requiredStrength = requiredStrength;
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
