package paul.TextQuest.entities;

/**
 * Features represent Things in a dungeon room - could be a fountain,
 * a big machine, a stream, whatever. They're an opportunity for
 * dungeon designers to better describe the room and create things
 * to interact with.
 * 
 * Created by Paul Dennis on 9/6/2017.
 */
public class Feature extends Container {
    
	//This isn't great. While all Features are Containers in a Java sense,
	//only some Features are containers in a game sense.
    private boolean isContainer;

    //This is kinda useless. think about removing
    private transient boolean bumped;
    
    private String status;

    public Feature () {

    }
    
    public Feature (String name) {
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