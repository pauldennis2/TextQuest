/**
 * @author Paul Dennis
 * Aug 28, 2018
 */
package paul.TextQuest.entities;

public class EnteringRoomAction {
	
	private String action;
	private boolean doOnce;
	
	private transient boolean done = false;
	
	public EnteringRoomAction () {
		
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public boolean isDoOnce() {
		return doOnce;
	}

	public void setDoOnce(boolean doOnce) {
		this.doOnce = doOnce;
	}
	
	public boolean wantsToTrigger () {
		if (doOnce) {
			return !done; //If done, we want to return false. If not done, return true
		}
		return true;
	}
	
	public void setDone (boolean done) {
		this.done = done;
	}
	
}
