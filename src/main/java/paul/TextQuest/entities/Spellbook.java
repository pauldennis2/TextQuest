/**
 * @author Paul Dennis (pd236m)
 * Sep 17, 2018
 */
package paul.TextQuest.entities;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import paul.TextQuest.utils.StringUtils;

public class Spellbook {
	
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
	
	//Get all spells that require the given type of magic
	//i.e. get all Fire spells
	public List<Spell> getSpellsOfType (String type) {
		 return spellbook.keySet()
			.stream()
			.map(spellbook::get)
			.filter(spell -> {
				List<String> prereqs = spell.getPrereqs();
				if (prereqs.contains(type)) {
					return true;
				}
				//Have to do this 2nd level search because a requirement might be "fire 2"
				for (String prereq : prereqs) {
					if (prereq.contains(type)) {
						return true;
					}
				}
				return false;
			})
			.collect(Collectors.toList());
	}	
}