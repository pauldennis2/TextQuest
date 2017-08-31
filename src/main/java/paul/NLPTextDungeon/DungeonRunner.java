package paul.NLPTextDungeon;

import paul.NLPTextDungeon.entities.NormalCombat;
import paul.NLPTextDungeon.parsing.UserInterfaceClass;
import paul.NLPTextDungeon.parsing.InputType;
import paul.NLPTextDungeon.parsing.TextInterface;
import paul.NLPTextDungeon.entities.Dungeon;
import paul.NLPTextDungeon.entities.DungeonRoom;
import paul.NLPTextDungeon.entities.Hero;
import paul.NLPTextDungeon.parsing.StatementAnalysis;
import paul.NLPTextDungeon.parsing.StatementAnalyzer;
import paul.NLPTextDungeon.utils.VictoryException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Paul Dennis on 8/8/2017.
 */
public class DungeonRunner extends UserInterfaceClass {

    private Dungeon dungeon;
    private Hero hero;

    private DungeonRoom currentRoom;
    private StatementAnalyzer analyzer;

    private NormalCombat normalCombat;


    private static final List<String> CLEAR_REQUIRED_FOR_ACTION = Arrays.asList("move", "loot", "plunder", "rescue", "search");

    //public static final String DUNGEON_FILE_PATH = "content_files/dungeons/" + "first_dungeon.json";
    public static final String DUNGEON_FILE_PATH = "content_files/dungeons/" + "new_dungeon.json";

    public DungeonRunner () throws IOException {
        hero = new Hero("default");
        analyzer = StatementAnalyzer.getInstance();

        dungeon = Dungeon.buildDungeonFromFile(DUNGEON_FILE_PATH);
    }

    @Override
    public void start (TextInterface textOut) {
        this.textOut = textOut;
        currentRoom = dungeon.getEntrance();
        textOut.println("Welcome to the " + dungeon.getDungeonName());

        children = new ArrayList<>(dungeon.getRooms());
        children.add(hero);
        children.forEach(child -> child.start(textOut));

        hero.setLocation(currentRoom);
    }

    @Override
    protected InputType handleResponse (String response) {
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
        return InputType.NONE;
    }

    @Override
    public InputType show () {
        if (normalCombat == null) {
            InputType type = currentRoom.show();

            if (type != InputType.NONE) {
                switch (type) {
                    case STD:
                        textOut.println("What would you like to do?");
                        break;
                    case NUMBER:
                        textOut.println("Please enter a number.");
                        break;
                    case SOLUTION_STRING:
                        textOut.println("Please enter your solution.");
                        textOut.tutorial("Try \"jump before\"!");
                        break;
                }
                requester = currentRoom;
            }
            return type;
        } else {
            InputType type = normalCombat.show();
            if (type == InputType.FINISHED) {
                normalCombat = null;
                return InputType.STD;
            } else if (type == InputType.COMBAT) {
                return type;
            } else {
                throw new AssertionError("Type should be one of two above cases.");
            }
        }
    }

    public void doActionFromAnalysis (StatementAnalysis analysis) {
        if (analysis.isActionable()) {
            try {
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
            } catch (VictoryException ex) {
                textOut.println("Victory!");
                textOut.println(ex.getMessage());
                textOut.println("The bards will sing of this day.");
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
        normalCombat = new NormalCombat(currentRoom);
        normalCombat.start(textOut);
    }

}
