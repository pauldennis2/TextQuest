/**
 * @author Paul Dennis
 * Sep 5, 2018
 */
package paul.TextQuest.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import paul.TextQuest.entities.BackpackItem;

public class StringUtils {

	public static String capitalize (String input) {
		return input.toLowerCase().substring(0, 1).toUpperCase() + input.substring(1);
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
	
	/**
	 * Squishes the given tokens to one string, skipping the first.
	 * @param tokens
	 * @return
	 */
	public static String squishTokens (String[] tokens) {
		String response = "";
		for (int i = 1; i < tokens.length; i++) {
    		response += tokens[i];
    		if (i + 1 < tokens.length) {
    			response += " ";
    		}
    	}
		return response;
	}
	
	public static String appendModifierWithSignInParens (int modifier) {
		if (modifier != 0) {
			return "(" + (modifier > 0 ? "+":"") + modifier + ")";
		} else {
			return "";
		}
	}
	
	public static String prettyPrintListNoPeriod (List<?> list) {
		if (list.size() == 2) {
			return list.get(0) + " and " + list.get(1);
		}
		String response = "";
		for (int i = 0; i < list.size(); i++) {
			response += list.get(i);
			if (i + 2 == list.size()) {
				response += ", and ";
			} else if (i + 1 == list.size()) {
			} else {
				response += ", ";
			}
		}
		return response;
	}
	
	public static String prettyPrintCount (List<BackpackItem> list) {
		List<String> itemNames = list.stream().map(BackpackItem::getName).collect(Collectors.toList());
		CountMap<String> itemNameCountMap = new CountMap<>();
		itemNames.forEach(itemNameCountMap::add);
		return "null";
	}
	
	public static String addAOrAn (String input) {
		if (startsWithVowel(input)) {
			return "an " + input;
		} else {
			return "a " + input;
		}
	}
	
	public static boolean startsWithVowel (String input) {
		input = input.toLowerCase(); //Don't try to economize this line, dummy
		return input.toLowerCase().startsWith("a") || input.startsWith("e") || input.startsWith("i")
				|| input.startsWith("o") || input.startsWith("u") || input.startsWith("h"); //"an historic event"
	}
	
	public static String readFile (String fileName) {
		try (Scanner fileScanner = new Scanner(new File(fileName))) {
			StringBuilder stringBuilder = new StringBuilder();
			while(fileScanner.hasNext()) {
				stringBuilder.append(fileScanner.nextLine());
			}
			return stringBuilder.toString();
		} catch (FileNotFoundException ex) {
			throw new AssertionError(ex);
		}
	}
}
