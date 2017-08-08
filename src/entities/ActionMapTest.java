package entities;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Paul Dennis on 8/8/2017.
 */
public class ActionMapTest {

    Map<String, OneVoid> actionMap;

    public ActionMapTest () {
        actionMap = new HashMap<>();
        actionMap.put("Announce", () -> System.out.println("Hello!"));
    }

    public static void main(String[] args) {
        ActionMapTest map = new ActionMapTest();
        map.actionMap.get("Announce").doSomething();
    }
}

interface OneVoid {
    void doSomething ();
}