package paul.TextQuest.entities.obstacles;

import paul.TextQuest.entities.Hero;

/**
 * Created by Paul Dennis on 8/13/2017.
 */
public class Chasm extends Obstacle {

    public Chasm () {
        super("Chasm", "jump", false);
    }

    @Override
    public boolean attempt (String solution, Hero hero) {
        if (!isCleared()) {
            if (solution.equals(this.getSolution())) {
                setCleared(true);
                hero.addExp(DEFAULT_XP_AMT);
                return true;
            }
            return false;
        } else {
            return true;
        }
    }

}
