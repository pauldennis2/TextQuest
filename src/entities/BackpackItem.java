package entities;

import java.util.Random;

/**
 * Created by Paul Dennis on 8/8/2017.
 */
public class BackpackItem {

    private String name;
    private boolean isQuestItem;
    private int value;

    public BackpackItem (String name) {
        this.name = name;
        isQuestItem = false;
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

    public boolean isQuestItem() {
        return isQuestItem;
    }

    public int getValue() {
        return value;
    }
}
