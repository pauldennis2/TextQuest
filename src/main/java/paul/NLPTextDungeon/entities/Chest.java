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
        contents = new ArrayList<>();
    }

    public Chest (String name) {
        locked = true;
        this.name = name;
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
        if (key.getName().equals(this.key)) {
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




    public boolean isLocked() {
        return locked;
    }

    public String getName() {
        return name;
    }

    public List<BackpackItem> getContents() {
        return contents;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContents(List<BackpackItem> contents) {
        this.contents = contents;
    }
}
