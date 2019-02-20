package paul.TextQuest.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Paul Dennis on 8/10/2017.
 * 
 * The Backpack represent's the Hero's inventory.
 */
public class Backpack {

    private List<BackpackItem> items;

    public Backpack () {
        items = new ArrayList<>();
    }

    public Stream<BackpackItem> stream () {
        return items.stream();
    }

    public void add (BackpackItem item) {
        items.add(item);
    }

    public void remove (BackpackItem item) {
        items.remove(item);
    }
    
    public void remove (String itemName) {
    	BackpackItem toBeRemoved = null;
    	for (BackpackItem item : items) {
    		if (item.getName().equals(itemName)) {
    			toBeRemoved = item;
    			break;
    		}
    	}
    	if (toBeRemoved != null) {
    		items.remove(toBeRemoved);
    	}
    }

    @Override
    public String toString () {
        return items.toString();
    }

    public boolean contains (String itemName) {
        return items.stream()
                .map(e -> e.getName().toLowerCase())
                .collect(Collectors.toList())
                .contains(itemName.toLowerCase());
    }
    
    /**
     * Looks for the given item WITHOUT removing it.
     * @param itemName
     * @return the item in question, or null if not present.
     */
    public BackpackItem getItem (String itemName) {
    	for (BackpackItem item : items) {
    		if (item.getName().equalsIgnoreCase(itemName)) {
    			return item;
    		}
    	}
    	return null;
    }
    
    public List<BackpackItem> getItems () {
    	return items;
    }
    
    public void setItems (List<BackpackItem> items) {
    	this.items = items;
    }
}
