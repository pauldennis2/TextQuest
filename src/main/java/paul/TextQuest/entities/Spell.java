/**
 * @author Paul Dennis (pd236m)
 * Sep 17, 2018
 */
package paul.TextQuest.entities;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import paul.TextQuest.utils.StringUtils;

public class Spell {
	
	private String name;
	private String key;
	
	private List<String> actions;
	private List<String> reagents;
	private List<String> prereqs;
	private List<String> requiredItems;
	
	private TargetType targetType;
	
	public Spell () {
		
	}
	
	private static Spell jsonRestore(String spellJson) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(spellJson, Spell.class);
    }
	
	public static void main(String[] args) throws IOException {
		System.out.println(Spellbook.buildFromFile("content_files/game/spellbook.json"));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public List<String> getActions() {
		return actions;
	}

	public void setActions(List<String> actions) {
		this.actions = actions;
	}

	public List<String> getReagents() {
		return reagents;
	}

	public void setReagents(List<String> reagents) {
		this.reagents = reagents;
	}

	public List<String> getPrereqs() {
		return prereqs;
	}

	public void setPrereqs(List<String> prereqs) {
		this.prereqs = prereqs;
	}

	public TargetType getTargetType() {
		return targetType;
	}

	public void setTargetType(TargetType targetType) {
		this.targetType = targetType;
	}
	

	public List<String> getRequiredItems() {
		return requiredItems;
	}

	public void setRequiredItems(List<String> requiredItems) {
		this.requiredItems = requiredItems;
	}

	@Override
	public String toString() {
		return "Spell [name=" + name + ", key=" + key + ", actions=" + actions + ", reagents=" + reagents + ", prereqs="
				+ prereqs + ", targetType=" + targetType + "]";
	}
	
}

class Spellbook {
	Map<String, Spell> spellbook;
	
	public Spellbook () {
		
	}

	public Map<String, Spell> getSpellbook() {
		return spellbook;
	}

	public void setSpellbook(Map<String, Spell> spellbook) {
		this.spellbook = spellbook;
	}
	
	private static Spellbook jsonRestore(String spellbookJson) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(spellbookJson, Spellbook.class);
    }
	
	public static Spellbook buildFromFile (String fileName) throws IOException {
		return jsonRestore(StringUtils.readFile(fileName));
	}
	
	public String toString () {
		return spellbook.toString();
	}
	
}

enum TargetType {
	SELF, ALL_ENEMIES, RANDOM_ENEMY, NONE;
}
