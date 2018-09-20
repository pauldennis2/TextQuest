/**
 * @author Paul Dennis
 * Sep 12, 2018
 */
package paul.TextQuest.entities;

public abstract class TickTock {

	private String onTick;
	private String onTock;
	
	public TickTock () {
		
	}
	
	public String getOnTick() {
		return onTick;
	}
	
	public void setOnTick(String onTick) {
		this.onTick = onTick;
	}
	
	public String getOnTock() {
		return onTock;
	}
	
	public void setOnTock(String onTock) {
		this.onTock = onTock;
	}
	
	public boolean tickTocks () {
		return (onTick != null) || (onTock != null);
	}
	
}
