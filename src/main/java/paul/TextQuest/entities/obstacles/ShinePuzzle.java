package paul.TextQuest.entities.obstacles;

import paul.TextQuest.entities.Hero;

/**
 * Created by Paul Dennis on 9/5/2017.
 */
public class ShinePuzzle extends Obstacle {

    @Override
    public boolean attempt(String solution, Hero hero) {
        return false;
    }
}
