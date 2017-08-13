package paul.NLPTextDungeon.entities.obstacles;

import paul.NLPTextDungeon.entities.Hero;

/**
 * Created by Paul Dennis on 8/13/2017.
 */
public class RiddleObstacle extends Obstacle {

    private String riddle;

    public RiddleObstacle () {

    }

    public RiddleObstacle (String riddle, String solution) {
        super("What Am I Riddle", solution, false);
        this.riddle = riddle;
    }

    @Override
    public boolean attempt(String solution, Hero hero) {
        if (solution.equals(this.getSolution())) {
            setCleared(true);
            return true;
        } else {
            hero.takeDamage(5);
            return false;
        }
    }

    public String getRiddle() {
        return riddle;
    }

    public void setRiddle(String riddle) {
        this.riddle = riddle;
    }
}
