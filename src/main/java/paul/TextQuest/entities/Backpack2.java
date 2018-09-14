/**
 * @author Paul Dennis
 * Sep 14, 2018
 */
package paul.TextQuest.entities;

import java.util.ArrayList;
import java.util.List;

import paul.TextQuest.utils.CountMap;

public class Backpack2 {
	
	private CountMap<BackpackItem> items;
	
	public Backpack2 () {
		items = new CountMap<>();
	}
	
	public void add (BackpackItem item) {
		items.add(item);
	}
	
	public void remove (BackpackItem item) {
		items.remove(item);
	}
	
	public void remove (String itemName) {
		BackpackItem toBeRemoved = null;
		for (BackpackItem item : items.keySet()) {
			if (item.getName().equals(itemName)) {
				toBeRemoved = item;
				break;
			}
		}
		remove(toBeRemoved);
	}
	
	public List<BackpackItem> toList () {
		List<BackpackItem> itemList = new ArrayList<>();
		for (BackpackItem item : items.keySet()) {
			int count = items.get(item);
			for (int i = 0; i < count; i++) {
				itemList.add(item);
			}
		}
		return itemList;
	}
}
