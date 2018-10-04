package paul.TextQuest.entities;

import paul.TextQuest.utils.StringUtils;

/**
 * Created by pauldennis on 8/21/17.
 */
public abstract class DungeonEntity extends TickTock {
	
	protected String name;
	protected String description;
	protected boolean darklight;
	
	/*This flag indicates whether the entity is "named" or not.
	 * This affects how things are displayed to the user. For example:
	 * "Iron Chest" with the named flag true would just print as
	 * Iron Chest. If named is set to false it will add a/an.
	 * 
	 *  This distinguishes between named/important entities, like
	 *  "Ganondorf" (we don't want to say there's "a Ganondorf")
	 *  and more "anonymous" entities that don't have a true "name".
	 */ 
	protected boolean named;
	
	public static final double DEFAULT_VISIBILITY_THRESHHOLD = 0.6;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public boolean isDarklight() {
		return darklight;
	}
	
	public void setDarklight(boolean darklight) {
		this.darklight = darklight;
	}
	
	public void setNamed (boolean named) {
		this.named = named;
	}
	
	public boolean isNamed () {
		return named;
	}
	
    public boolean isVisible (double lighting) {
        if (darklight) {
            return lighting == 0.0;
        }
        return lighting >= DEFAULT_VISIBILITY_THRESHHOLD;
    }
    
    @Override
    public String toString () {
    	if (named) {
    		return name;
    	}
        return StringUtils.addAOrAn(name);
    }
    
    public boolean hasDescription () {
    	return description != null && !description.trim().equals("");
    }
}
