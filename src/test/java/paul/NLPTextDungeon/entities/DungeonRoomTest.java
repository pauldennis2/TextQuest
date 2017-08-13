package paul.NLPTextDungeon.entities;

import org.junit.Test;

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

}