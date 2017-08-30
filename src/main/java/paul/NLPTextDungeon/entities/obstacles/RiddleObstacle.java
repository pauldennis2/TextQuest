package paul.NLPTextDungeon.entities.obstacles;

import paul.NLPTextDungeon.entities.Hero;

/**
 * Created by Paul Dennis on 8/13/2017.
 */
public class RiddleObstacle extends Obstacle {

    private String riddle;
    private transient int numAttempts = 0;

    public final static int RIDDLE_MULT = 4;

    public RiddleObstacle () {

    }

    public RiddleObstacle (String riddle, String solution) {
        super("What Am I Riddle", solution, false);
        this.riddle = riddle;
    }

    @Override
    public boolean attempt(String solution, Hero hero) {
        if (!isCleared()) {
            numAttempts++;
            if (solution.equals(this.getSolution())) {
                this.setCleared(true);
                hero.addExp(DEFAULT_XP_AMT * RIDDLE_MULT);
                return true;
            }
            return false;
        } else {
            return true;
        }
    }

    public String getRiddle() {
        return riddle;
    }

    public void setRiddle(String riddle) {
        this.riddle = riddle;
    }

    public int getNumAttempts() {
        return numAttempts;
    }
}
