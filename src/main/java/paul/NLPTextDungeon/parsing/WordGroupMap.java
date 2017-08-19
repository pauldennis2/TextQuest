package paul.NLPTextDungeon.parsing;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by pauldennis on 8/19/17.
 */
public class WordGroupMap {

    private Map<String, WordGroup> wordMap;

    public WordGroupMap () {
        wordMap = new HashMap<>();
    }

    public WordGroup get (String key) {
        return wordMap.get(key);
    }

    public void put (String key, WordGroup value) {
        wordMap.put(key, value);
    }

    public Set<String> keySet () {
        return wordMap.keySet();
    }
}
