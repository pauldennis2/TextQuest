package paul.NLPTextDungeon.entities.obstacles;

import paul.NLPTextDungeon.entities.BackpackItem;
import paul.NLPTextDungeon.entities.DungeonRoom;
import paul.NLPTextDungeon.entities.Hero;
import paul.NLPTextDungeon.entities.Monster;
import paul.NLPTextDungeon.interfaces.VoidAction;

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
                doAction(onSmash, hero);
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

    private void doAction (String action, Hero hero) {
        if (smashActionMap == null) {
            initSmashActionMap();
        }
        VoidAction smashAction = smashActionMap.get(action);
        if (smashAction != null) {
            smashAction.doAction(hero.getLocation());
        } else {
            hero.getTextOut().debug("Action *" + action + "* was not in the map.");
        }
    }

    private static void initSmashActionMap () {
        smashActionMap = new HashMap<>();
        smashActionMap.put("createSkelly", room -> room.addMonster(new Monster(2, 1, "Skeleton")));
        smashActionMap.put("explode", room -> {
            room.getHero().getTextOut().println("BOOM!! Barrel exploded.");
            room.getHero().takeNonMitigatedDamage(5);
            //This            ^^ is a good reason for this functionality to be somewhere else
        });
    }

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
