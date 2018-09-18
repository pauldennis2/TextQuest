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
	
	private String levelingPlanLocation;
	private String lookAndFeelLocation;
	private String heroStartingInfoLocation;
	private List<String> dungeonGroupLocations;
	private List<String> spellbookLocations;
	
	private LevelUpPlan levelingPlan;
	private LookAndFeel lookAndFeel;
	private Hero heroStartingInfo;
	private List<DungeonGroup> dungeonGroups;
	private List<Spellbook> spellbooks;
	
	public GamePlan () {
		spellbookLocations = new ArrayList<>();
		
		dungeonGroups = new ArrayList<>();
		spellbooks = new ArrayList<>();
		
	}
	
	public void build () {

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
				spellbooks.add(Spellbook.buildFromFile(spellbookLocation));
			} catch (IOException ex) {
				throw new AssertionError(ex);
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		GamePlan plan = buildFromFile("content_files/game/default_gameplan.json");
		System.out.println(plan);
		plan.build();
		System.out.println("======");
		System.out.println(plan);
	}
	
	
	
	@Override
	public String toString() {
		return "GamePlan ["
				+ (levelingPlanLocation != null ? "levelingPlanLocation=" + levelingPlanLocation + ", " : "")
				+ (lookAndFeelLocation != null ? "lookAndFeelLocation=" + lookAndFeelLocation + ", " : "")
				+ (heroStartingInfoLocation != null ? "heroStartingInfoLocation=" + heroStartingInfoLocation + ", "
						: "")
				+ (dungeonGroupLocations != null ? "dungeonGroupLocations=" + dungeonGroupLocations + ", " : "")
				+ (spellbookLocations != null ? "spellbookLocations=" + spellbookLocations + ", " : "")
				+ (levelingPlan != null ? "levelingPlan=" + levelingPlan + ", " : "")
				+ (lookAndFeel != null ? "lookAndFeel=" + lookAndFeel + ", " : "")
				+ (heroStartingInfo != null ? "heroStartingInfo=" + heroStartingInfo + ", " : "")
				+ (dungeonGroups != null ? "dungeonGroups=" + dungeonGroups + ", " : "")
				+ (spellbooks != null ? "spellbooks=" + spellbooks : "") + "]";
	}

	private static GamePlan jsonRestore(String levelUpJson) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(levelUpJson, GamePlan.class);
    }
	
	public static GamePlan buildFromFile (String fileName) throws IOException {
		return jsonRestore(StringUtils.readFile(fileName));
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

	public List<Spellbook> getSpellbooks() {
		return spellbooks;
	}
}
