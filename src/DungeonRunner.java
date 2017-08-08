import entities.Dungeon;
import entities.DungeonRoom;
import entities.Hero;

/**
 * Created by Paul Dennis on 8/8/2017.
 */
public class DungeonRunner {

    Dungeon dungeon;
    Hero hero;

    DungeonRoom currentRoom;
    SafeNumScanner numScanner;

    public DungeonRunner () {
        hero = new Hero();
        dungeon = new Dungeon();
        numScanner = new SafeNumScanner(System.in);
    }

    public void run () {
        currentRoom = dungeon.getEntrance();
        hero.setLocation(currentRoom);
        System.out.println("Welcome to the " + dungeon.getDungeonName());
        System.out.println("Your goal:");
        System.out.println(dungeon.getGoalDescription());
        currentRoom.describeRoom();

        System.out.println("What would you like to do?");
        System.out.println("1. Move to another room");
        System.out.println("2. View backpack items");
        System.out.println("3. View room actions");
        int response = numScanner.getSafeNum(1, 3);

        switch (response) {
            case 1:
                currentRoom.getTravelDirections();
                break;
            case 2:
                hero.getBackpack().stream().forEach(System.out::println);
        }
    }

    public static void main(String[] args) {
        new DungeonRunner().run();
    }
}
