package entities;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Paul Dennis on 8/8/2017.
 */
public class BackpackItem {

    private String name;
    private boolean isQuestItem;
    private int value;

    private static Map<String, RoomAction> itemActionMap = new HashMap<>();
    private static boolean actionMapInit = false;

    public BackpackItem (String name) {
        this.name = name;
        isQuestItem = false;

        if (actionMapInit == false) {

        }
    }

    public BackpackItem (boolean quest) {
        isQuestItem = quest;
        name = getQuestItemName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString () {
        return name + " (" + value + ")";
    }

    public static void initActionMap () {
        itemActionMap.put("Torch", e -> e.setLighting(e.getLighting() + 0.5));

        itemActionMap.put("Key", e -> e.getChest().unlock(e.getChest().getKey()));
        itemActionMap.put("Fight", e -> e.getMonsters());
    }

    public static String[] DESCRIPTORS = {"Fabled", "Awesome", "Sought-After", "Fabulous", "Enchanted", "Teeny Tiny"};
    public static String[] ITEMS = {"Scepter", "Sword", "Gem", "McGuffin"};
    public static String[] PLACES = {"Aer", "Dal", "Tifen", "Coldon", "Manos"};

    public static String getQuestItemName () {
        Random random = new Random();
        String response = "";
        response += DESCRIPTORS[random.nextInt(DESCRIPTORS.length)] + " ";
        response += ITEMS[random.nextInt(ITEMS.length)] + " of ";
        response += PLACES[random.nextInt(PLACES.length)] + "'" + PLACES[random.nextInt(PLACES.length)];
        return response;
    }
}

interface RoomAction {
    void doAction (DungeonRoom room);
}
