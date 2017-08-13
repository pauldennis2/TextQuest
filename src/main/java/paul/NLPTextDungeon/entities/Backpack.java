package paul.NLPTextDungeon.entities;

import paul.NLPTextDungeon.interfaces.listeners.OnPickup;
import paul.NLPTextDungeon.utils.VictoryException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Paul Dennis on 8/10/2017.
 */
public class Backpack extends Location {

    private static Map<String, OnPickup> listenerMap;
    private static boolean initialized;

    private List<BackpackItem> items;

    public Backpack () {
        items = new ArrayList<>();
        if (!initialized) {
            initListenerMap();
            initialized = true;
        }
    }

    public Stream<BackpackItem> stream () {
        return items.stream();
    }

    public void add (BackpackItem item) {
        items.add(item);
        if (item.hasPickupAction()) {
            listenerMap.get(item.getPickupAction()).doAction();
        }
    }

    private static void initListenerMap () {
        listenerMap = new HashMap<>();
        listenerMap.put("victory", () -> {
            throw new VictoryException("You win!");
        });
    }

    public void remove (BackpackItem item) {
        items.remove(item);
    }

    @Override
    public String toString () {
        return items.toString();
    }

    public boolean contains (String itemName) {
        return items.stream()
                .map(e -> e.getName().toLowerCase())
                .collect(Collectors.toList())
                .contains(itemName);
    }
}
