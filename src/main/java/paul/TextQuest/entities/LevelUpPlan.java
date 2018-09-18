/**
 * @author Paul Dennis
 * Sep 17, 2018
 */
package paul.TextQuest.entities;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import paul.TextQuest.enums.LevelUpCategory;
import paul.TextQuest.utils.StringUtils;

public class LevelUpPlan {
	
	private List<Integer> expAmounts;
	private Map<Integer, List<LevelUpCategory>> levelUpActions;
	
	public LevelUpPlan () {
		levelUpActions = new HashMap<>();
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

	public Map<Integer, List<LevelUpCategory>> getLevelUpActions() {
		return levelUpActions;
	}

	public void setLevelUpActions(Map<Integer, List<LevelUpCategory>> levelUpActions) {
		this.levelUpActions = levelUpActions;
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println(buildFromFile("content_files/game/leveling/default_plan.json"));
	}

	@Override
	public String toString() {
		return "LevelUpPlan [expAmounts=" + expAmounts + ", levelUpActions=" + levelUpActions + "]";
	}
	
}
