package paul.NLPTextDungeon.entities;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by pauldennis on 8/21/17.
 */
public class CombatTest {

    @Test
    public void testCalcAccuracy () {
        //Base chance 0.5
        //Every 2 pts of defense = -0.05
        //Every 4 pts of might = +0.05
        assertEquals(0.5, NormalCombat.calcAccuracy(0, 0), 0.00001);
        assertEquals(0.5, NormalCombat.calcAccuracy(4, 2), 0.00001);
        assertEquals(0.5, NormalCombat.calcAccuracy(4, 3), 0.00001);

        assertEquals(0.45, NormalCombat.calcAccuracy(0, 2), 0.00001);
        assertEquals(0.55, NormalCombat.calcAccuracy(4, 1), 0.00001);
        assertEquals(0.6, NormalCombat.calcAccuracy(8, 0), 0.00001);
    }

}