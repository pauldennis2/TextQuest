package paul.NLPTextDungeon;

import paul.NLPTextDungeon.awebappexp.BufferedOutputTextStream;
import paul.NLPTextDungeon.entities.Dungeon;
import paul.NLPTextDungeon.entities.DungeonRoom;
import paul.NLPTextDungeon.entities.Hero;
import paul.NLPTextDungeon.entities.MetaLocation;
import paul.NLPTextDungeon.parsing.StatementAnalysis;
import paul.NLPTextDungeon.parsing.StatementAnalyzer;
import paul.NLPTextDungeon.utils.VictoryException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Paul Dennis on 8/8/2017.
 */
public class DungeonRunner {

    private Dungeon dungeon;
    private Hero hero;

    private DungeonRoom currentRoom;
    private StatementAnalyzer analyzer;

    private boolean done = false;

    private static final List<String> CLEAR_REQUIRED_FOR_ACTION = Arrays.asList("move", "loot", "plunder", "rescue", "search");

    public static final String DUNGEON_FILE_PATH = "content_files/dungeons/" + "first_dungeon.json";

    private BufferedOutputTextStream textOut;

    private List<MetaLocation> metaLocations;
    public DungeonRunner () throws IOException {
        hero = new Hero();
        analyzer = new StatementAnalyzer();

        dungeon = Dungeon.buildDungeonFromFile(DUNGEON_FILE_PATH);
        metaLocations = new ArrayList<>();
        metaLocations.add(dungeon);
        textOut = new BufferedOutputTextStream();
        hero.setTextOut(textOut);
        dungeon.setTextOut(textOut);
    }

    public void start () {
        currentRoom = dungeon.getEntrance();
        hero.setLocation(currentRoom);
        currentRoom.setHero(hero);
        textOut.println("Welcome to the " + dungeon.getDungeonName());
        textOut.println("Your goal:");
    }

    public void analyzeAndExecuteStatement (String userInput) {
        StatementAnalysis analysis = analyzer.analyzeStatement(userInput);
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

    public void describeRoom () {
        currentRoom.describeRoom();
        textOut.println("What would you like to do?");
    }

    public void doActionFromAnalysis (StatementAnalysis analysis) {
        if (analysis.isActionable()) {
            try {
                analysis.printFinalAnalysis();
                String actionWord = analysis.getActionWord();
                if (CLEAR_REQUIRED_FOR_ACTION.contains(actionWord) && !currentRoom.isCleared()) {
                    if (currentRoom.getMonsters().size() > 0) {
                        textOut.println("You have to clear the room of monsters first.");
                    } else {
                        textOut.println("You have to clear the room of obstacles first.");
                    }
                } else {
                    if (analysis.getActionParam() != null) {
                        hero.takeAction(actionWord, analysis.getActionParam());
                    } else {
                        hero.takeAction(actionWord);
                    }
                }
            } catch (VictoryException ex) {
                textOut.println("Victory!");
                textOut.println(ex.getMessage());
                textOut.println("The bards will sing of this day.");
                done = true;
            }
            currentRoom = hero.getLocation();
        } else {
            textOut.println("Could not analyze to an actionable statement.");
            textOut.debug("Comments:");
            analysis.getComments().forEach(textOut::debug);
        }
    }

    public BufferedOutputTextStream getTextOut() {
        return textOut;
    }
}
