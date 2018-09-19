/**
 * @author Paul Dennis (pd236m)
 * Sep 17, 2018
 */
package paul.TextQuest.entities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import paul.TextQuest.enums.SpellTargetType;
import paul.TextQuest.parsing.TextInterface;

public class Spell {
	
	private String name;
	private String key;
	
	private String statusString;
	
	private List<String> actions;
	private List<String> reagents;
	private List<String> prereqs;
	private List<String> requiredItems;
	
	private SpellTargetType targetType;
	
	public Spell () {
		actions = new ArrayList<>();
		reagents = new ArrayList<>();
		prereqs = new ArrayList<>();
		requiredItems = new ArrayList<>();
	}
	
	public static void main(String[] args) throws IOException {
		Spellbook spellbook = Spellbook.buildFromFile("content_files/game/spellbook.json");
		System.out.println(Spellbook.buildFromFile("content_files/game/spellbook.json"));
		Scanner inputScanner = new Scanner(System.in);
		
		while (true) {
			String input = inputScanner.nextLine();
			if (input.equals("")) {
				break;
			}
			System.out.println("All spells with type " + input + ":");
			List<Spell> spellsOfType = spellbook.getSpellsOfType(input);
			spellsOfType.forEach(spell -> System.out.println("\t" + spell.getName()));
		}
		
		inputScanner.close();
	}
	
	
	//Sandbox area for writing this code. Will probably end up in Hero
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

	@Override
	public String toString() {
		return "Spell [name=" + name + ", key=" + key + ", actions=" + actions + ", reagents=" + reagents + ", prereqs="
				+ prereqs + ", targetType=" + targetType + "]";
	}	
}