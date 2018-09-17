/**
 * @author Paul Dennis (pd236m)
 * Sep 5, 2018
 */
package paul.TextQuest.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

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
	
	public static String prettyPrintListNoPeriod (List<?> list) {
		if (list.size() == 2) {
			return list.get(0) + " and " + list.get(1) + ".";
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
	
	public static String addAOrAn (String input) {
		if (startsWithVowel(input)) {
			return "an " + input;
		} else {
			return "a " + input;
		}
	}
	
	public static boolean startsWithVowel (String input) {
		input = input.toLowerCase();
		return input.startsWith("a") || input.startsWith("e") || input.startsWith("i")
				|| input.startsWith("o") || input.startsWith("u") || input.startsWith("h");
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
