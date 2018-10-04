/**
 * @author Paul Dennis
 * Sep 5, 2018
 */
package paul.TextQuest.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A class to group together static utility functions
 * mostly for string manipulation and File IO.
 */
public class StringUtils {

	public static String capitalize (String input) {
		input = input.toLowerCase();
		return input.substring(0, 1).toUpperCase() + input.substring(1);
	}
	
	public static String capitalizeWithoutLowerCasing (String input) {
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
	
	public static String appendModifierWithSign (int modifier) {
		if (modifier >= 0) {
			return "+" + modifier;
		} else {
			return "" + modifier;
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
	
	public static String addAOrAn (String input) {
		if (startsWithVowel(input)) {
			return "an " + input;
		} else {
			return "a " + input;
		}
	}
	
	public static boolean startsWithVowel (String input) {
		input = input.toLowerCase(); //Don't try to economize this line, dummy
		return input.startsWith("a") || input.startsWith("e") || input.startsWith("i")
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
			System.err.println("!Could not find file: " + fileName + ". Returning null.");
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <E> E buildObjectFromFile (String fileName, Class<?> type) throws IOException {
		String json = readFile(fileName);
		if (json != null) {
			return (E) new ObjectMapper().readValue(json, type);
		}
		return null;
    	
    }
	
	public static String serializeIgnoringTransient (Object obj) {
    	ObjectMapper objectMapper = new ObjectMapper();
    	
    	objectMapper.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true);    	
    	
    	try {
    		return objectMapper.writeValueAsString(obj);
    	} catch (JsonProcessingException ex) {
    		ex.printStackTrace();
    		throw new AssertionError("Error");
    	}
    }
}
