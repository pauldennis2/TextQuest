package paul.NLPTextDungeon.entities;

import org.junit.Test;

import paul.TextQuest.entities.BackpackItem;
import paul.TextQuest.entities.DungeonRoom;
import paul.TextQuest.entities.Hero;
import paul.TextQuest.entities.Monster;
import paul.TextQuest.entities.obstacles.Chasm;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Paul Dennis on 8/12/2017.
 */
public class DungeonRoomTest {

    @Test
    public void testHiddenItems () {
        DungeonRoom room = new DungeonRoom("Room", "Description");
        room.addHiddenItem("fountain", new BackpackItem("Magic Sword"));
        List<BackpackItem> hiddenItems = room.searchForHiddenItems("fountain");

        //Check that we can get the item
        assertNotNull(hiddenItems);
        assertEquals(1, hiddenItems.size());
        assertEquals("Magic Sword", hiddenItems.get(0).getName());

        //Check that it's been removed (no items are at the fountain now)
        assertEquals(room.searchForHiddenItems("fountain").size(), 0);
    }

    @Test
    public void testIsCleared () {
        DungeonRoom room = new DungeonRoom("Room", "Description");

        assertTrue(room.isCleared());
        Monster bob = new Monster (5, 2, "bob");
        room.addMonster(bob);

        assertFalse(room.isCleared());

        bob.takeDamage(10);
        room.updateMonsters();

        assertTrue(room.isCleared());

        Chasm chasm = new Chasm();
        room.addObstacle(chasm);

        assertFalse(room.isCleared());

        chasm.attempt("jump", new Hero());
        assertTrue(chasm.isCleared());
        assertTrue(room.isCleared());
    }

}