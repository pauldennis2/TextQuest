package paul.TextQuest.entities;

/**
 * Created by Paul Dennis on 9/6/2017.
 */
//TODO - what is this class for?
public class Feature extends Container {

    private String name;
    private boolean darklight;
    private String description;
    
    private boolean isContainer;

    private transient boolean bumped;
    
    private String status;

    public Feature () {

    }
    
    public Feature (String name) {
    	this.name = name;
    }

    public boolean isDarklight() {
        return darklight;
    }

    public void setDarklight(boolean darklight) {
        this.darklight = darklight;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static final double DEFAULT_VISIBILITY_THRESHHOLD = 0.6;
    public boolean isVisible (double lighting) {
        if (darklight) {
            return lighting == 0.0;
        }
        return lighting >= DEFAULT_VISIBILITY_THRESHHOLD;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isBumped() {
        return bumped;
    }

    public void setBumped(boolean bumped) {
        this.bumped = bumped;
    }

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isContainer() {
		return isContainer;
	}

	public void setIsContainer(boolean isContainer) {
		this.isContainer = isContainer;
	}
    
    
}