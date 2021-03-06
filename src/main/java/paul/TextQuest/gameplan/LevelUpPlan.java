/**
 * @author Paul Dennis
 * Sep 17, 2018
 */
package paul.TextQuest.gameplan;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import paul.TextQuest.utils.StringUtils;

public class LevelUpPlan {
	
	private boolean levelingRestoresHealth;
	private boolean levelingRestoresSpells;
	
	private List<Integer> expAmounts;
	private Map<Integer, List<String>> levelUpActions;
	
	public LevelUpPlan () {
		levelUpActions = new HashMap<>();
		levelingRestoresHealth = true;
		levelingRestoresSpells = true;
	}
	
	private static LevelUpPlan jsonRestore(String levelUpJson) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(levelUpJson, LevelUpPlan.class);
    }
	
	public static LevelUpPlan buildFromFile (String fileName) throws IOException {
		return jsonRestore(StringUtils.readFile(fileName));
	}

	public List<Integer> getExpAmounts() {
		return expAmounts;
	}

	public void setExpAmounts(List<Integer> expAmounts) {
		this.expAmounts = expAmounts;
		for (int i = 1; i < expAmounts.size(); i++) {
			if (expAmounts.get(i) < expAmounts.get(i - 1)) {
				throw new AssertionError("Exp amounts must increase");
			}
		}
	}

	public Map<Integer, List<String>> getLevelUpActions() {
		return levelUpActions;
	}

	public void setLevelUpActions(Map<Integer, List<String>> levelUpActions) {
		this.levelUpActions = levelUpActions;
	}
	
	public boolean levelingRestoresHealth () {
		return levelingRestoresHealth;
	}
	
	public boolean levelingRestoresSpells () {
		return levelingRestoresSpells;
	}
	
	public void setLevelingRestoresHealth(boolean levelingRestoresHealth) {
		this.levelingRestoresHealth = levelingRestoresHealth;
	}

	public void setLevelingRestoresSpells(boolean levelingRestoresSpells) {
		this.levelingRestoresSpells = levelingRestoresSpells;
	}

	@Override
	public String toString() {
		return "LevelUpPlan [expAmounts=" + expAmounts + ", levelUpActions=" + levelUpActions + "]";
	}
	
}
