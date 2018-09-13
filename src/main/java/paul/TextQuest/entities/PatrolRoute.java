/**
 * @author Paul Dennis (pd236m)
 * Sep 13, 2018
 */
package paul.TextQuest.entities;

import java.util.List;
import java.util.Random;

public class PatrolRoute {
	
	private List<Integer> patrolRoute;
	private boolean loops; //if not loops, it snakes
	private int patrollerId;
	
	private transient int patrolIndex;
	
	public PatrolRoute () {
		patrolIndex = 0;
	}
	
	public int getNextRoomIdAndUpdateIndex () {
    	patrolIndex++;
    	if (patrolIndex >= patrolRoute.size()) {
    		patrolIndex = 0;
    	}
    	return patrolRoute.get(patrolIndex);
    }
	
	public int getRandomRoomId () {
		return patrolRoute.get(new Random().nextInt(patrolRoute.size() - 1));
	}

	public List<Integer> getPatrolRoute() {
		return patrolRoute;
	}

	public void setPatrolRoute(List<Integer> patrolRoute) {
		this.patrolRoute = patrolRoute;
	}

	public boolean isLoops() {
		return loops;
	}

	public void setLoops(boolean loops) {
		this.loops = loops;
	}

	public int getPatrollerId() {
		return patrollerId;
	}

	public void setPatrollerId(int patrollerId) {
		this.patrollerId = patrollerId;
	}
	
	
}
