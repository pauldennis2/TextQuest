/**
 * @author Paul Dennis (pd236m)
 * Sep 14, 2018
 */
package paul.TextQuest.entities;

import java.util.HashMap;
import java.util.Map;

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
