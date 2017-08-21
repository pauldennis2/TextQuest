package paul.NLPTextDungeon.entities.obstacles;

import paul.NLPTextDungeon.entities.Hero;
import paul.NLPTextDungeon.interfaces.VoidAction;

/**
 * Created by pauldennis on 8/21/17.
 */

//Represents an obstacle that is "smashable" (like a wooden barrel, or weak stonework)
//By someone of sufficient strength
public class SmashableObstacle extends Obstacle {

    private int requiredStrength;
    private String onSmash;

    private transient VoidAction onSmashed;

    public SmashableObstacle () {

    }

    public SmashableObstacle (int requiredStrength) {
        this.requiredStrength = requiredStrength;
    }

    public void setOnSmashed(VoidAction onSmashed) {
        this.onSmashed = onSmashed;
    }

    public boolean attempt (String input, Hero hero) {
        //assert: input should = "smash"
        if (hero.getMight() >= requiredStrength) {
            this.setCleared(true);
            if (onSmashed != null) {
                onSmashed.doAction(hero.getLocation());
            }
            return true;
        } else {
            return false;
        }
    }
}
