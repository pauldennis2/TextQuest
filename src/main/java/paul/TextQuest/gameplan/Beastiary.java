/**
 * @author Paul Dennis
 * Sep 14, 2018
 */
package paul.TextQuest.gameplan;

import java.util.HashMap;
import java.util.Map;

import paul.TextQuest.entities.Monster;

public class Beastiary {
	
	private Map<String, Monster> monsterMap;
	
	public Beastiary () {
		monsterMap = new HashMap<>();
	}

	public Map<String, Monster> getMonsterMap() {
		return monsterMap;
	}

	public void setMonsterMap(Map<String, Monster> monsterMap) {
		this.monsterMap = monsterMap;
	}
	
	
}
