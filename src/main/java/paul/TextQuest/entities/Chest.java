package paul.TextQuest.entities;

import java.util.*;

/**
 * Created by Paul Dennis on 8/8/2017.
 */
public class Chest extends Container {

    private boolean locked;
    private String key;

    private transient boolean isOpen;

    public Chest() {
        super();
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

    public void unlock (BackpackItem key) {
        if (key.getName().equals(this.key)) {
            locked = false;
        }
    }
    
    public void open () {
    	if (!locked) {
    		isOpen = true;
    	}
    }
    
    public void open (BackpackItem key) {
    	if (locked) {
    		unlock(key);
    		if (!locked) {
    			isOpen = true;
    		}
    	} else {
    		isOpen = true;
    	}
    }

    public List<BackpackItem> removeContents () {
        if (locked) {
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
    
    public boolean isOpen () {
    	return isOpen;
    }
}
