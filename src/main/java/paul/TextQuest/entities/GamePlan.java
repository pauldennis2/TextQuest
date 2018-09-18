/**
 * @author Paul Dennis
 * Sep 18, 2018
 */
package paul.TextQuest.entities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A class to group together different categories of game information.
 */
public class GamePlan {
	
	private String levelingPlanLocation;
	private String lookAndFeelLocation;
	private List<String> dungeonGroupLocations;
	private List<String> spellbookLocations;
	
	private LevelUpPlan levelingPlan;
	private LookAndFeel lookAndFeel;
	private List<DungeonGroup> dungeonGroups;
	private List<Spellbook> spellbooks;
	
	public GamePlan () {
		spellbookLocations = new ArrayList<>();
	}
	
	public void build () {
		
		lookAndFeel = null;
		try {
			lookAndFeel = LookAndFeel.buildFromFile(lookAndFeelLocation);
		} catch (IOException ex) {
			throw new AssertionError(ex);
		}
		try {
			levelingPlan = LevelUpPlan.buildFromFile(levelingPlanLocation);
		} catch (IOException ex) {
			throw new AssertionError(ex);
		}
		
		for (String dungeonGroupLocation : dungeonGroupLocations) {
			try {
				dungeonGroups.add(DungeonGroup.buildGroupFromFile(dungeonGroupLocation));
			} catch (IOException ex) {
				throw new AssertionError(ex);
			}
		}
		
		for (String spellbookLocation : spellbookLocations) {
			try {
				spellbooks.add(Spellbook.buildFromFile(spellbookLocation));
			} catch (IOException ex) {
				throw new AssertionError(ex);
			}
		}
	}

	public String getLevelingPlanLocation() {
		return levelingPlanLocation;
	}

	public void setLevelingPlanLocation(String levelingPlanLocation) {
		this.levelingPlanLocation = levelingPlanLocation;
	}

	public List<String> getSpellbookLocations() {
		return spellbookLocations;
	}

	public void setSpellbookLocations(List<String> spellbookLocations) {
		this.spellbookLocations = spellbookLocations;
	}

	public String getLookAndFeelLocation() {
		return lookAndFeelLocation;
	}

	public void setLookAndFeelLocation(String lookAndFeelLocation) {
		this.lookAndFeelLocation = lookAndFeelLocation;
	}

	public List<String> getDungeonGroupLocations() {
		return dungeonGroupLocations;
	}

	public void setDungeonGroupLocations(List<String> dungeonGroupLocations) {
		this.dungeonGroupLocations = dungeonGroupLocations;
	}

	public LevelUpPlan getLevelingPlan() {
		return levelingPlan;
	}

	public LookAndFeel getLookAndFeel() {
		return lookAndFeel;
	}

	public List<DungeonGroup> getDungeonGroups() {
		return dungeonGroups;
	}

	public List<Spellbook> getSpellbooks() {
		return spellbooks;
	}
	
	
}
