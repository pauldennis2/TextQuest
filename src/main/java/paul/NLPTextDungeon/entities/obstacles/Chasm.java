package paul.NLPTextDungeon.entities.obstacles;

import paul.NLPTextDungeon.entities.Hero;

import java.util.Arrays;

/**
 * Created by Paul Dennis on 8/13/2017.
 */
public class Chasm extends Obstacle {

    public Chasm () {
        super("Chasm", "jump", false);
    }

    @Override
    public boolean attempt (String solution, Hero hero) {
        if (solution.equals(this.getSolution())) {
            setCleared(true);
            hero.addExp(DEFAULT_XP_AMT);
            return true;
        }
        return false;
    }

}
