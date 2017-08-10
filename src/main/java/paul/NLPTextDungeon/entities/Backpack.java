package paul.NLPTextDungeon.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Paul Dennis on 8/10/2017.
 */
public class Backpack extends Location {
    private List<BackpackItem> items;

    public Backpack () {
        items = new ArrayList<>();
    }

    public Stream<BackpackItem> stream () {
        return items.stream();
    }

    public void add (BackpackItem item) {
        items.add(item);
    }

    public void remove (BackpackItem item) {
        items.remove(item);
    }

    @Override
    public String toString () {
        return items.toString();
    }
}
