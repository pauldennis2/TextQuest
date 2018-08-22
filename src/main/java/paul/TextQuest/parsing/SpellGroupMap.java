package paul.NLPTextDungeon.parsing;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pauldennis on 8/19/17.
 */
public class SpellGroupMap {

    private Map<String, WordGroup> spellGroupMap;

    public SpellGroupMap () {
        spellGroupMap = new HashMap<>();
    }

    public WordGroup get (String key) {
        //this is junk
        if (key.length() == 3 && spellGroupMap.get(key) == null) {
            return null;
        } else if (spellGroupMap.get(key) == null) {
            return get(key.substring(0, key.length() - 1));
        } else {
            return spellGroupMap.get(key);
        }
    }
}
