package paul.NLPTextDungeon.entities;

import java.util.*;

/**
 * Created by Paul Dennis on 8/8/2017.
 */
public class Chest {

    private boolean locked;
    private String key;
    private String name;

    private List<BackpackItem> contents;

    public Chest() {
        locked = true;
        name = getContainerName();
        key = name + "'s Key";
        contents = new ArrayList<>();
    }

    @Override
    public String toString () {
        return name;
    }

    //This is meant to be called as part of initialization not gameplay
    public void addItem (BackpackItem item) {
        contents.add(item);
    }

    public void unlock (BackpackItem key) {
        if (key.getName().equals(name)) {
            locked = false;
        }
    }

    public List<BackpackItem> removeContents () {
        if (locked) {
            System.out.println(name + " is locked.");
            return null;
        }
        List<BackpackItem> returnContent = contents;
        contents = new ArrayList<>();
        return returnContent;
    }

    public static final String[] SIZES = {"Small", "Medium", "Large", "Massive"};
    public static final String[] BINDING = {"Iron-Bound", "Copper-Bound", "Enchanted"};
    public static String getContainerName () {
        Random random = new Random();
        String response = SIZES[random.nextInt(SIZES.length)] + " ";
        response += BINDING[random.nextInt(BINDING.length)] + " Chest";
        return response;
    }


    public boolean isLocked() {
        return locked;
    }

    public String getName() {
        return name;
    }

    public List<BackpackItem> getContents() {
        return contents;
    }
}
