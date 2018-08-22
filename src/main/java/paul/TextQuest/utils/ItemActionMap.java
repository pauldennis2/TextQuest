package paul.TextQuest.utils;

import java.util.HashMap;
import java.util.Map;

import paul.TextQuest.interfaces.VoidAction;

/**
 * Created by Paul Dennis on 8/10/2017.
 */
public class ItemActionMap {

    Map<String, VoidAction> itemActions;

    public ItemActionMap() {
        itemActions = new HashMap<>();
    }

    public void put (String key, VoidAction value) {
        itemActions.put(key, value);
    }

    public VoidAction get (String key) {
        VoidAction mapped = itemActions.get(key);
        if (mapped == null) {
            return room -> System.out.println(key + " is not mapped in itemactions");
        }
        return mapped;
    }
}
