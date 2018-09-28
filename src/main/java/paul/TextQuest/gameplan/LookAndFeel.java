/**
 * @author Paul Dennis
 * Sep 18, 2018
 */
package paul.TextQuest.gameplan;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import paul.TextQuest.utils.StringUtils;

/**
 * A class to provide some customization for the 
 * "Look and Feel" of the game. Like, do we refer to
 * "magic" or "Force Powers" or "aether abilities".
 * 
 * This is currently just a template/placeholder.
 */
public class LookAndFeel {
	
	private String magicName;
	private boolean describeLighting;
	
	public LookAndFeel () {
		
	}
	
	private static LookAndFeel jsonRestore(String levelUpJson) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(levelUpJson, LookAndFeel.class);
    }
	
	public static LookAndFeel buildFromFile (String fileName) throws IOException {
		return jsonRestore(StringUtils.readFile(fileName));
	}

	public String getMagicName() {
		return magicName;
	}

	public void setMagicName(String magicName) {
		this.magicName = magicName;
	}

	public boolean isDescribeLighting() {
		return describeLighting;
	}

	public void setDescribeLighting(boolean describeLighting) {
		this.describeLighting = describeLighting;
	}
	
	
}
