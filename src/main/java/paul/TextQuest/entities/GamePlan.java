/**
 * @author Paul Dennis
 * Sep 18, 2018
 */
package paul.TextQuest.entities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import paul.TextQuest.utils.StringUtils;

/**
 * A class to group together different categories of game information, including:
 *   Hero's starting stats, known spells and items
 *   General look and feel of the game
 *   Leveling information
 *   Associated dungeon groups
 *   General spellbooks available in this game
 */
public class GamePlan {
	
	//These properties get deserialized
	private String levelingPlanLocation;
	private String lookAndFeelLocation;
	private String heroStartingInfoLocation;
	private List<String> dungeonGroupLocations;
	private List<String> spellbookLocations;
	
	//These properties are built from file IO (don't need setters)
	private LevelUpPlan levelingPlan;
	private LookAndFeel lookAndFeel;
	private Hero heroStartingInfo;
	private List<DungeonGroup> dungeonGroups;
	private Spellbook spellbook;
	
	public GamePlan () {
		spellbookLocations = new ArrayList<>();
		dungeonGroups = new ArrayList<>();
		spellbook = new Spellbook();
	}
	
	private void build () {

		heroStartingInfo = Hero.loadHeroFromFile(heroStartingInfoLocation);
		
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
				spellbook.addSpellbook(Spellbook.buildFromFile(spellbookLocation));
			} catch (IOException ex) {
				throw new AssertionError(ex);
			}
		}
	}

	private static GamePlan jsonRestore(String levelUpJson) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(levelUpJson, GamePlan.class);
    }
	
	public static GamePlan buildFromFile (String fileName) throws IOException {
		GamePlan gamePlan = jsonRestore(StringUtils.readFile(fileName));
		gamePlan.build();
		return gamePlan;
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

	public String getHeroStartingInfoLocation() {
		return heroStartingInfoLocation;
	}

	public void setHeroStartingInfoLocation(String heroStartingInfoLocation) {
		this.heroStartingInfoLocation = heroStartingInfoLocation;
	}

	public Hero getHeroStartingInfo() {
		return heroStartingInfo;
	}

	public LookAndFeel getLookAndFeel() {
		return lookAndFeel;
	}

	public List<DungeonGroup> getDungeonGroups() {
		return dungeonGroups;
	}

	public Spellbook getSpellbook () {
		return spellbook;
	}
}
