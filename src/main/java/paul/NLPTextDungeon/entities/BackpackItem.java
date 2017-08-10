package paul.NLPTextDungeon.entities;

import paul.NLPTextDungeon.interfaces.listeners.OnPickup;

import java.util.Random;

/**
 * Created by Paul Dennis on 8/8/2017.
 */
public class BackpackItem {

    private String name;
    private boolean isQuestItem;
    private int value;

    private Location location;

    private OnPickup pickupListener;

    public BackpackItem (String name, Location location) {
        this.name = name;
        this.location = location;
        isQuestItem = false;
    }

    public BackpackItem (boolean quest, Location location) {
        isQuestItem = quest;
        name = getQuestItemName();
    }

    public OnPickup getPickupListener() {
        return pickupListener;
    }

    public void setPickupListener(OnPickup pickupListener) {
        this.pickupListener = pickupListener;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
        if (pickupListener != null) {
            pickupListener.doAction();
        }
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
