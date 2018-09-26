/**
 * @author Paul Dennis (pd236m)
 * Sep 17, 2018
 */
package paul.TextQuest.entities;

import java.util.ArrayList;
import java.util.List;

import paul.TextQuest.enums.SpellTargetType;
import paul.TextQuest.interfaces.Detailable;
import paul.TextQuest.utils.StringUtils;

public class Spell implements Detailable {
	
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
	
	public String toDetailedString () {
		String response = name + " - ";
		if (description != null) {
			response += description + " ";
		}
		if (prereqs.size() > 0) {
			response += ", Magic Requirements: ";
			for (String prereq : prereqs) {
				response += StringUtils.capitalize(prereq);
			}
		}
		
		if (reagents.size() > 0) {
			response += ", Reagents: ";
			for (String reagent : reagents) {
				response += reagent;
			}
		}
		
		if (requiredItems.size() > 0) {
			response += ", Required Items: ";
			for (String requiredItem : requiredItems) {
				response += requiredItem;
			}
		}
		
		return response;
	}
}