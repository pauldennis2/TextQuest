/**
 * @author Paul Dennis (pd236m)
 * Sep 5, 2018
 */
package paul.TextQuest.utils;

import java.util.List;

public class StringUtils {

	public static String capitalize (String input) {
		return input.substring(0, 1).toUpperCase() + input.substring(1);
	}
	
	public static String prettyPrintList (List<?> list) {
		if (list.size() == 2) {
			return list.get(0) + " and " + list.get(1) + ".";
		}
		String response = "";
		for (int i = 0; i < list.size(); i++) {
			response += list.get(i);
			if (i + 2 == list.size()) {
				response += ", and ";
			} else if (i + 1 == list.size()) {
				response += ".";
			} else {
				response += ", ";
			}
		}
		return response;
	}
}
