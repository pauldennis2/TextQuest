package paul.NLPTextDungeon.entities;



import paul.NLPTextDungeon.enums.DungeonGoalType;
import paul.NLPTextDungeon.interfaces.listeners.OnPickup;
import paul.NLPTextDungeon.utils.VictoryException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static paul.NLPTextDungeon.enums.Direction.*;

/**
 * Created by Paul Dennis on 8/8/2017.
 */
public class Dungeon extends MetaLocation {

    private List<DungeonRoom> rooms;
    private DungeonGoalType goal;
    private String goalDescription;
    private String dungeonName;

    private int level;

    private DungeonRoom finalRoom;
    private DungeonRoom entrance;
    private DungeonRoom middleRoom;

    public static final String GOAL_INTRO = "You must venture into the ";

    Map<BackpackItem, OnPickup> pickupListenerMap;

    public Dungeon (int level) {
        this.level = level;
        Random random = new Random();
        rooms = new ArrayList<>();
        Monster boss;
        BackpackItem itemToRecover;
        rooms = new ArrayList<>();

        entrance = new DungeonRoom(false, true);
        entrance.setRoomName("Entryway");
        middleRoom = new DungeonRoom(random);
        finalRoom = new DungeonRoom(true, false);

        entrance.connectTo(EAST, middleRoom);
        middleRoom.connectTo(EAST, finalRoom);

        middleRoom.addItem(new BackpackItem("Potion", middleRoom));

        BackpackItem bomb = new BackpackItem("Bomb", entrance);
        bomb.setPickupListener(() -> System.out.println("You picked up a bomb you dummy! Have some sense!"));

        dungeonName = getRandomDungeonName();
        goalDescription = GOAL_INTRO + dungeonName;
        goal = DungeonGoalType.values()[random.nextInt(DungeonGoalType.values().length)];
        switch (goal) {
            case SLAY_MONSTER:
                boss = new Monster(true, random);
                finalRoom.addMonster(boss);
                goalDescription += " and slay the vicious monster " + boss.getName() + ".";
                break;
            case RECOVER_ITEM:
                itemToRecover = new BackpackItem(true, finalRoom);
                itemToRecover.setPickupListener(() -> {
                    throw new VictoryException("Recovered the " + itemToRecover.getName());
                });
                Chest goalChest = new Chest();
                goalChest.addItem(itemToRecover);
                finalRoom.addContainer(goalChest);
                finalRoom.addMonster(new Monster(false, random));

                Chest.getKeys().stream().forEach(e -> {
                        middleRoom.addItem(e);
                        e.setLocation(middleRoom);
                });

                goalDescription += " and recover the " + itemToRecover.getName() + ".";
                break;
            case RESCUE_PRINCE:
                goalDescription += " and rescue Prince Charming.";
                finalRoom.addMonster(new Monster(false, random));
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

    public String getDungeonName() {
        return dungeonName;
    }

    public static final String[] DUNGEON_DESCRIPTORS = {"Spooky", "Scary", "Haunted", "Weird", "Shadowy", "Drip-Drip"};
    public static final String[] OFS = {"of Doom", "of Doom and Gloom", "of Truly, Horrible, Epicly Bad Doom",
            "of Jet-Skis and Rainbows", "of Weirdness", "of \"Hey, do we have enough names yet?\""};
    private static String getRandomDungeonName() {
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
