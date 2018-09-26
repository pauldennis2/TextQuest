package paul.TextQuest;

import paul.TextQuest.entities.BackpackItem;
import paul.TextQuest.entities.Dungeon;
import paul.TextQuest.entities.DungeonRoom;
import paul.TextQuest.entities.Hero;
import paul.TextQuest.entities.NormalCombat;
import paul.TextQuest.entities.Spellbook;
import paul.TextQuest.parsing.StatementAnalysis;
import paul.TextQuest.parsing.StatementAnalyzer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Paul Dennis on 8/8/2017.
 */
public class DungeonRunner  {

    private Dungeon dungeon;
    private Hero hero;

    private DungeonRoom currentRoom;
    private StatementAnalyzer analyzer;

    private NormalCombat normalCombat;

    private static final List<String> CLEAR_REQUIRED_FOR_ACTION = Arrays.asList("move", "loot", "plunder", "rescue", "search");
    
    private TextInterface textOut;
    
    private Spellbook spellbook;
    
    public DungeonRunner(Hero hero, String fileName) throws IOException {
    	this.hero = hero;
    	analyzer = StatementAnalyzer.getInstance();
    	dungeon = Dungeon.buildDungeonFromFile(fileName);
    	dungeon.setDungeonRunner(this);
    	
    	
    	textOut = TextInterface.getInstance();
    	dungeon.getRooms().forEach(room -> {
    		room.setTextOut(textOut);
    		room.buildObjectsFromKeys();
    	});
        currentRoom = dungeon.getEntrance();
        textOut.println(dungeon.getDescription());
        hero.setTextOut(textOut);

        hero.setLocation(currentRoom);
         
        //Temporary
        hero.getBackpack().add(new BackpackItem("Boots of Vaulting"));
        
        
    }

    protected void handleResponse (String response) {
        StatementAnalysis analysis = analyzer.analyzeStatement(response, currentRoom);
        doActionFromAnalysis(analysis);
        if (analysis.hasAnd() && analysis.isSecondActionable()) {
            String nextActionWord = analysis.getSecondActionWord();
            String nextParamWord = analysis.getSecondActionParam();
            textOut.debug("Next words: " + nextActionWord + " param " + nextParamWord);
            textOut.debug("Running 2nd half of AND statement.");
            analysis = new StatementAnalysis(nextActionWord, nextParamWord);
            textOut.debug(analysis);
            doActionFromAnalysis(analysis);
        }
    }

    public void show () {
        if (normalCombat == null) {
        	currentRoom.setTextOut(textOut);
            currentRoom.show();
        } else {
            normalCombat.show();
        }
    }

    public void doActionFromAnalysis (StatementAnalysis analysis) {
        if (analysis.isActionable()) {
            analysis.printFinalAnalysis();
            String actionWord = analysis.getActionWord();
            if (CLEAR_REQUIRED_FOR_ACTION.contains(actionWord) && !currentRoom.isCleared()) {
                textOut.println("You have to clear the room of monsters first.");
            } else {
                if (analysis.getActionParam() != null) {
                    hero.takeAction(actionWord, analysis.getActionParam());
                } else {
                    hero.takeAction(actionWord);
                }
            }
            currentRoom = hero.getLocation();
        } else {
            if (!analysis.getOriginalStatement().equals("")) {
                textOut.println("Could not analyze to an actionable statement.");
                textOut.debug("Comments:");
                analysis.getComments().forEach(textOut::debug);
            } else {
                textOut.debug("Input was empty (no action taken)");
            }
        }
    }

    public TextInterface getTextOut() {
        return textOut;
    }

    public Hero getHero() {
        return hero;
    }

    public Dungeon getDungeon () {
        return dungeon;
    }

    public void startCombat () {
        normalCombat = new NormalCombat(this, currentRoom);
        if (currentRoom.getOnCombatStart() != null) {
        	String action = currentRoom.getOnCombatStart();
        	currentRoom.doAction(action);
        }
        if (currentRoom.getOnCombatEnd() != null) {
        	normalCombat.setOnCombatEnd(currentRoom.getOnCombatEnd());
        }
    }
    
    public void endCombat () {
    	if (normalCombat == null) {
    		throw new AssertionError("No combat to end");
    	}
    	if (normalCombat.isFinished()) {
    		normalCombat = null;
    	} else {
    		throw new AssertionError("Combat didn't think it was finished");
    	}
    }

	public Spellbook getSpellbook() {
		return spellbook;
	}

	public void setSpellbook(Spellbook spellbook) {
		this.spellbook = spellbook;
	}
}
