import entities.Dungeon;
import entities.DungeonRoom;
import entities.Hero;
import entities.MetaLocation;
import utils.SafeNumScanner;
import utils.VictoryException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul Dennis on 8/8/2017.
 */
public class DungeonRunner {

    Dungeon dungeon;
    Hero hero;

    DungeonRoom currentRoom;
    SafeNumScanner numScanner;

    boolean done = false;

    List<MetaLocation> metaLocations;

    public DungeonRunner () {
        hero = new Hero();
        System.out.println("Input dungeon level");

        numScanner = new SafeNumScanner(System.in);
        int level = numScanner.getSafeNum(0, 2);
        dungeon = new Dungeon(level);
        metaLocations = new ArrayList<>();
        metaLocations.add(dungeon);
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
        List<String> actions = hero.getRoomActions();
        System.out.println("What would you like to do?");
        int index = 1;
        for (String action : actions) {
            System.out.println(index + ". " + action);
            index++;
        }
        int response = numScanner.getSafeNum(1, actions.size());

        System.out.println("Ok, you want to " + actions.get(response - 1));
        String chosenAction = actions.get(response - 1);
        try {
            hero.takeAction(chosenAction);
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
    }

    public static void main(String[] args) {
        new DungeonRunner().run();
    }
}
