package paul.TextQuest.entities;

import java.util.List;
import java.util.Random;

/**
 * Class representing all the information a monster needs
 * to patrol around. Owned by Monster.
 * 
 * @author Paul Dennis
 * Sep 13, 2018
 */
public class PatrolRoute {
	
	private List<Integer> roomIds;
	private boolean loops; //if not loops, it snakes
	private int patrollerId;
	
	private transient int patrolIndex;
	
	public PatrolRoute () {
		patrolIndex = 0;
	}
	
	public int getNextRoomIdAndUpdateIndex () {
    	patrolIndex++;
    	if (patrolIndex >= roomIds.size()) {
    		patrolIndex = 0;
    	}
    	return roomIds.get(patrolIndex);
    }
	
	public int getRandomRoomId () {
		return roomIds.get(new Random().nextInt(roomIds.size() - 1));
	}

	public int getPatrolIndex() {
		return patrolIndex;
	}

	public void setPatrolIndex(int patrolIndex) {
		this.patrolIndex = patrolIndex;
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

	public List<Integer> getRoomIds() {
		return roomIds;
	}

	public void setRoomIds(List<Integer> roomIds) {
		this.roomIds = roomIds;
	}
}
