/**
 * @author Paul Dennis
 * Sep 17, 2018
 */
package paul.TextQuest.entities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import paul.TextQuest.utils.StringUtils;

public class Spellbook {
	
	private List<String> spellTypes;
	private Map<String, Spell> spellbook;
	
	public Spellbook () {
		spellTypes = new ArrayList<>();
		spellbook = new HashMap<>();
	}
	
	private static Spellbook jsonRestore(String spellbookJson) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(spellbookJson, Spellbook.class);
    }
	
	public static Spellbook buildFromFile (String fileName) throws IOException {
		return jsonRestore(StringUtils.readFile(fileName));
	}
	
	//Convenience method to access spells quickly
	public Spell getSpell (String key) {
		return spellbook.get(key);
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
	
	//Adds all entries from the other spellbook, throwing errors on conflicts
	public void addSpellbook (Spellbook other) {
		other.spellTypes.forEach(spellType -> {
			if (!this.spellTypes.contains(spellType)) {
				this.spellTypes.add(spellType);
			}
		});
		
		other.spellbook.keySet().forEach(key -> {
			Spell spell = other.spellbook.get(key);
			if (this.spellbook.containsKey(key)) {
				throw new AssertionError("Already have a mapping for " + key);
			}
			this.spellbook.put(key, spell);
		});
	}

	public List<String> getSpellTypes() {
		return spellTypes;
	}

	public void setSpellTypes(List<String> spellTypes) {
		this.spellTypes = spellTypes;
	}

	public Map<String, Spell> getSpellbook() {
		return spellbook;
	}

	public void setSpellbook(Map<String, Spell> spellbook) {
		this.spellbook = spellbook;
	}
}