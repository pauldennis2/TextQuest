package paul.TextQuest.entities;

/**
 * Created by pauldennis on 8/21/17.
 */
public abstract class DungeonEntity extends TickTock {
	
	protected String name;
	protected String description;
	protected boolean darklight;
	
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
	
    public boolean isVisible (double lighting) {
        if (darklight) {
            return lighting == 0.0;
        }
        return lighting >= DEFAULT_VISIBILITY_THRESHHOLD;
    }
    
    public boolean hasDescription () {
    	return description != null && !description.trim().equals("");
    }
}
