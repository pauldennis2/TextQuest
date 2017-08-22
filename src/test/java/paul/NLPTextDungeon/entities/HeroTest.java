package paul.NLPTextDungeon.entities;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by pauldennis on 8/21/17.
 */
public class HeroTest {

    @Test
    public void testCalcAccuracy () {
        //Base chance 0.5
        //Every 2 pts of defense = -0.05
        //Every 4 pts of might = +0.05
        assertEquals(0.5, Hero.calcAccuracy(0, 0), 0.00001);
        assertEquals(0.5, Hero.calcAccuracy(4, 2), 0.00001);
        assertEquals(0.5, Hero.calcAccuracy(4, 3), 0.00001);

        assertEquals(0.45, Hero.calcAccuracy(0, 2), 0.00001);
        assertEquals(0.55, Hero.calcAccuracy(4, 1), 0.00001);
        assertEquals(0.6, Hero.calcAccuracy(8, 0), 0.00001);
    }

}