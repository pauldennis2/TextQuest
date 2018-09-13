package paul.TextQuest.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Paul Dennis on 8/10/2017.
 */
public abstract class Container extends TickTock {
	
    List<BackpackItem> contents;
    Map<String, String> onInsert;
    
    public Container () {
    	contents = new ArrayList<>();
    	onInsert = new HashMap<>();
    }
    
    public void add (BackpackItem item) {
    	contents.add(item);
    }
    
    public boolean contains (BackpackItem item) {
    	return contents.contains(item);
    }
    
    public List<BackpackItem> getContents () {
    	return contents;
    }
    
    public void setContents (List<BackpackItem> contents) {
    	this.contents = contents;
    }

	public Map<String, String> getOnInsert() {
		return onInsert;
	}

	public void setOnInsert(Map<String, String> onInsert) {
		this.onInsert = onInsert;
	}
    
}
