import entities.Dungeon;
import entities.DungeonRoom;
import entities.Hero;
import entities.parsing.StatementAnalysis;
import entities.parsing.StatementAnalyzer;
import interfaces.ParamAction;
import utils.SafeNumScanner;
import utils.VictoryException;

import java.util.List;
import java.util.Scanner;

/**
 * Created by Paul Dennis on 8/8/2017.
 */
public class DungeonRunner {

    Dungeon dungeon;
    Hero hero;

    DungeonRoom currentRoom;
    StatementAnalyzer analyzer;
    Scanner scanner;

    boolean done = false;

    public DungeonRunner () {
        hero = new Hero();
        dungeon = new Dungeon();
        analyzer = new StatementAnalyzer();
        scanner = new Scanner(System.in);
    }

    public void run () {
        currentRoom = dungeon.getEntrance();
        hero.setLocation(currentRoom);
        currentRoom.addHero(hero);
        System.out.println("Welcome to the " + dungeon.getDungeonName());
        System.out.println("Your goal:");
        System.out.println(dungeon.getGoalDescription());
        mainActionMenu();
    }

    public void mainActionMenu () {
        currentRoom.describeRoom();
        System.out.println("What would you like to do?");
        String response = scanner.nextLine();
        StatementAnalysis analysis = analyzer.analyzeStatement(response);
        if (analysis.isActionable()) {
            try {
                analysis.printFinalAnalysis();
                if (analysis.getActionParam() != null) {
                    hero.takeAction(analysis.getActionWord(), analysis.getActionParam());
                } else {
                    hero.takeAction(analysis.getActionWord());
                }
            } catch (VictoryException ex) {
                System.out.println("Victory!");
                System.out.println(ex.getMessage());
                System.out.println("The bards will sing of this day.");
                done = true;
            }
            currentRoom = hero.getLocation();
            if (!done) {
                mainActionMenu();
            }
        } else {
            System.out.println("Could not analyze to an actionable statement.\nComments:");
            analysis.getComments().forEach(System.out::print);
        }
    }

    public static void main(String[] args) {
        new DungeonRunner().run();
    }
}
