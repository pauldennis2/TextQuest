/**
 * @author Paul Dennis
 * Sep 18, 2018
 */
package paul.TextQuest.utils;

import java.util.List;
import java.util.Random;


public class CollectionUtils {
	
	public static <E> E getRandom (List<E> items) {
		
		if (items != null && items.size() > 1) {
			return items.get(new Random().nextInt(items.size() - 1));
		} else if (items != null && items.size() == 1) {
			return items.get(0);
		} else {
			return null;
		}
	}
}