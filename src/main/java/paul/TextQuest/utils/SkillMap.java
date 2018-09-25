/**
 * @author Paul Dennis
 * Sep 18, 2018
 */
package paul.TextQuest.utils;

import java.util.HashMap;

/**
 * Class to keep a "safe" mapping of skills
 * (if something isn't mapped it'll come back 0, not null). 
 */
public class SkillMap extends HashMap<String, Integer>{
	
	
	public SkillMap () {
		super();
	}
	
	@Override
	public Integer get(Object key) {
		Integer v = super.get(key);
		if (v == null) {
			v = 0;
		}
		return v;
	}
	
	public void add (String key, int value) {
		if (!this.containsKey(key)) {
			this.put(key, value);
		} else {
			this.put(key, value + this.get(key));
		}
	}
}
