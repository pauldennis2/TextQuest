/**
 * @author Paul Dennis (pd236m)
 * Sep 21, 2018
 */
package paul.TextQuest.entities;

import java.util.HashMap;
import java.util.Map;

public class ItemLibrary {
	
	Map<String, BackpackItem> itemMap;
	
	public ItemLibrary () {
		itemMap = new HashMap<>();
	}

	public Map<String, BackpackItem> getItemMap() {
		return itemMap;
	}

	public void setItemMap(Map<String, BackpackItem> itemMap) {
		this.itemMap = itemMap;
	}
}
