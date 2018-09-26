/**
 * @author Paul Dennis (pd236m)
 * Sep 17, 2018
 */
package paul.TextQuest.entities;

import java.util.ArrayList;
import java.util.List;

import paul.TextQuest.TextInterface;
import paul.TextQuest.enums.SpellTargetType;

public class Spell {
	
	private String name;
	private String key;
	
	private String statusString;
	
	private List<String> actions;
	private List<String> reagents;
	private List<String> prereqs;
	private List<String> requiredItems;
	
	private String description;
	
	private SpellTargetType targetType;
	
	public Spell () {
		actions = new ArrayList<>();
		reagents = new ArrayList<>();
		prereqs = new ArrayList<>();
		requiredItems = new ArrayList<>();
	}
	
	
	//Sandbox area for writing this code. Will probably end up in Hero
	//TODO move to Hero (or other appropriate location)
	public static void castSpell (Spell spell, DungeonRoom location, Hero hero) {
		TextInterface textOut = hero.getTextOut();
		//Check prereqs
		spell.getPrereqs().forEach(prereq -> {
			List<String> knownSpells = hero.getSpellbook();
			if (prereq.contains(" ")) {
				String[] splits = prereq.split(" ");
				int requiredLevel = Integer.parseInt(splits[1]);
				String type = splits[0];
				if (knownSpells.contains(type)) {
					if (requiredLevel > 1) {
						textOut.println("Your knowledge of " + prereq + " magic is not strong enough.");
					}
				} else {
					textOut.println("You don't know the neccessary type of magic (" + prereq + ")");
				}
			} else {
				if (!knownSpells.contains(prereq)) {
					textOut.println("You don't know the neccessary type of magic (" + prereq + ")");
					return;
				}
			}
		});
		
		//Check required items
		spell.getRequiredItems().forEach(itemName -> {
			if (!hero.getBackpack().contains(itemName)) {
				textOut.println("You are missing a required item.");
				return;
			}
		});
		
		//Check and remove reagents
		spell.getReagents().forEach(reagent -> {
			if (!hero.getBackpack().contains(reagent)) {
				textOut.println("You are missing a required reagent: " + reagent + ".");
				return;
			} else {
				hero.getBackpack().remove(reagent);
			}
		});
		
		//Check status string
		if (spell.statusString != null) {
			if (hero.hasStatus(spell.statusString)) {
				textOut.println("You are already affected by that spell.");
				return;
			} else {
				hero.addStatus(spell.statusString);
			}
		}
		
		//Do spell actions
		spell.getActions().forEach(location::doAction);
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

	public SpellTargetType getTargetType() {
		return targetType;
	}

	public void setTargetType(SpellTargetType targetType) {
		this.targetType = targetType;
	}
	

	public List<String> getRequiredItems() {
		return requiredItems;
	}

	public void setRequiredItems(List<String> requiredItems) {
		this.requiredItems = requiredItems;
	}
	
	public void setStatusString (String statusString) {
		this.statusString = statusString;
	}

	public String getStatusString() {
		return statusString;
	}

	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	@Override
	public String toString() {
		if (description == null) {
			return name;
		}
		return name + " - " + description;
	}
}