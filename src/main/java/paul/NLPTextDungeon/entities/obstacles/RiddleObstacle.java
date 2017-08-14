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
        System.out.println("You guessed " + solution);
        if (solution.equals(this.getSolution())) {
            System.out.println("Correct!");
            setCleared(true);
            return true;
        } else {
            System.out.println("Incorrect. You take 5 damage.");
            hero.takeDamage(5);
            System.out.println(riddle);
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
