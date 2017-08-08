package entities;

import enums.DungeonGoalType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static enums.Direction.*;

/**
 * Created by Paul Dennis on 8/8/2017.
 */
public class Dungeon {

    private List<DungeonRoom> rooms;
    private DungeonGoalType goal;
    private String goalDescription;
    private String dungeonName;

    private DungeonRoom finalRoom;
    private DungeonRoom entrance;
    private DungeonRoom middleRoom;

    public static final String GOAL_INTRO = "You must venture into the ";

    public Dungeon () {
        Random random = new Random();
        rooms = new ArrayList<>();
        Monster boss;
        BackpackItem itemToRecover;
        rooms = new ArrayList<>();

        entrance = new DungeonRoom(false, true);
        finalRoom = new DungeonRoom(true, false);
        middleRoom = new DungeonRoom(random);

        entrance.connectTo(EAST, middleRoom);
        middleRoom.connectTo(EAST, finalRoom);

        dungeonName = getDungeonName();
        goalDescription = GOAL_INTRO + dungeonName;
        goal = DungeonGoalType.values()[random.nextInt(DungeonGoalType.values().length)];
        switch (goal) {
            case SLAY_MONSTER:
                boss = new Monster(true, random);
                finalRoom.addMonster(boss);
                goalDescription += " and slay the vicious monster " + boss.getName() + ".";
                break;
            case RECOVER_ITEM:
                itemToRecover = new BackpackItem(true);
                Container goalContainer = new Container();
                goalContainer.addItem(itemToRecover);
                finalRoom.addContainer(goalContainer);

                Container.getKeys().stream().forEach(e -> middleRoom.addItem(e));

                goalDescription += " and recover the " + itemToRecover.getName() + ".";
                break;
            case RESCUE_PRINCE:
                goalDescription += " and rescue Prince Charming.";
                finalRoom.setHasPrince(true);
                break;
        }
    }

    public List<DungeonRoom> getRooms() {
        return rooms;
    }

    public DungeonGoalType getGoal() {
        return goal;
    }

    public String getGoalDescription() {
        return goalDescription;
    }

    public DungeonRoom getFinalRoom() {
        return finalRoom;
    }

    public DungeonRoom getEntrance() {
        return entrance;
    }

    public DungeonRoom getMiddleRoom() {
        return middleRoom;
    }

    public static final String[] DUNGEON_DESCRIPTORS = {"Spooky", "Scary", "Haunted", "Weird", "Shadowy", "Drip-Drip"};
    public static String getDungeonName () {
        Random random = new Random();
        String response = "";
        int numDescriptors = random.nextInt(2) + 1;
        for (int i = 0; i < numDescriptors; i++) {
            response += DUNGEON_DESCRIPTORS[random.nextInt(DUNGEON_DESCRIPTORS.length)];
        }
        response += " Dungeon";
        return response;
    }

}
