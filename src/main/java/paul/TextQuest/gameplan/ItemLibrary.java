/**
 * @author Paul Dennis
 * Sep 21, 2018
 */
package paul.TextQuest.gameplan;

import java.util.HashMap;
import java.util.Map;

import paul.TextQuest.entities.BackpackItem;

public class ItemLibrary {
	
	private Map<String, BackpackItem> itemMap;
	
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
