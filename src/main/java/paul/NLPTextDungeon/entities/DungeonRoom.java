package paul.NLPTextDungeon.entities;


import paul.NLPTextDungeon.enums.Direction;
import paul.NLPTextDungeon.enums.LightingLevel;

import java.util.*;
import java.util.stream.Collectors;

import static paul.NLPTextDungeon.enums.LightingLevel.DIM;
import static paul.NLPTextDungeon.enums.LightingLevel.PITCH_BLACK;

/**
 * Created by Paul Dennis on 8/8/2017.
 */
public class DungeonRoom extends Location {

    private Map<Direction, DungeonRoom> connectedRooms;

    private double lighting;

    private List<Monster> monsters;
    private List<BackpackItem> items;
    private Chest chest;

    private boolean finalRoom; //Contains either boss, item, or prince
    private boolean dungeonEntrance;
    private boolean hasPrince;

    private String roomName;

    private Hero hero;

    private int id;

    private static int nextId = 1;

    public DungeonRoom (Random random) {
        id = nextId;
        nextId++;
        finalRoom = false;
        dungeonEntrance = false;
        connectedRooms = new HashMap<>();
        monsters = new ArrayList<>();
        items = new ArrayList<>();
        lighting = Math.random();
    }

    public DungeonRoom (boolean finalRoom, boolean dungeonEntrance) {
        id = nextId;
        nextId++;
        if (!finalRoom && !dungeonEntrance) {
            //Should only be used with one of these == true
            throw new AssertionError();
        }
        this.finalRoom = finalRoom;
        this.dungeonEntrance = dungeonEntrance;

        connectedRooms = new HashMap<>();
        monsters = new ArrayList<>();
        items = new ArrayList<>();

        lighting = 0.9;
    }

    public void setHasPrince(boolean hasPrince) {
        this.hasPrince = hasPrince;
    }

    public void addMonster (Monster monster) {
        monsters.add(monster);
    }

    public void addContainer (Chest chest) {
        this.chest = chest;
    }

    public void addItem (BackpackItem item) {
        items.add(item);
    }

    public void connectTo (Direction direction, DungeonRoom other) {
        if (connectedRooms.get(direction) != null) {
            System.out.println("Already connected in that direction");
            throw new AssertionError();
        }
        if (other == null) {
            System.out.println("Cannot connect to null room");
            throw new AssertionError();
        }

        connectedRooms.put(direction, other);
        other.connectedRooms.put(direction.getOpposite(), this);
    }

    public Set<Direction> getTravelDirections () {
        return connectedRooms.keySet();
    }

    public void describeRoom () {
        Random random = new Random();
        System.out.println("\n\n\n\nYou are in room " + id);
        LightingLevel lightingLevel = LightingLevel.getLightingLevel(lighting);
        switch (lightingLevel) {
            case WELL_LIT:
                System.out.println("The room is well lit. You can clearly see:");
                monsters.stream().forEach(System.out::println);
                items.stream().forEach(System.out::println);
                if (chest != null) {
                    System.out.println(chest);
                }
                if (monsters.size() + items.size() == 0) {
                    System.out.println("(There is nothing in the room.)");
                }
                if (hasPrince) {
                    System.out.println("You see a handsome prince tied to a chair. " +
                            "He looks like he'd really like to be rescued.");
                }
                break;

            case PITCH_BLACK:
                System.out.println("The room is pitch black. You cannot see anything.");
                break;

            case DIM:
                if (monsters.size() + items.size() > 0) {
                    System.out.println("The room is not well lit. You can only make out a few shapes.");
                    if (monsters.size() > 0) {
                        System.out.println("You can see " + monsters.size() + " figures moving in the darkness.");
                    }
                    if (items.size() > 0) {
                        System.out.println("You think you can see the following items:");
                        items.stream()
                                .filter(e -> Math.random() < lighting * 2)
                                .forEach(System.out::println);
                    }
                }
                if (hasPrince) {
                    System.out.println("You think you see a handsome prince but it's hard to tell.");
                }
                break;
        }
    }

    public List<BackpackItem> lootRoom () {
        List<BackpackItem> lootedItems = items;
        items = new ArrayList<>();
        return lootedItems;
    }

    public void updateMonsters () {
        monsters = monsters.stream()
                .filter(e -> e.getHealth() > 0)
                .collect(Collectors.toList());
    }

    public double getLighting () {
        return lighting;
    }

    public void setLighting (double lighting) {
        if (lighting > 1.0) {
            this.lighting = 1.0;
        } else if (lighting < 0.0) {
            this.lighting = 0.0;
        } else {
            this.lighting = lighting;
        }
    }

    public Map<Direction, DungeonRoom> getConnectedRooms() {
        return connectedRooms;
    }

    public List<Monster> getMonsters() {
        return monsters;
    }

    public List<BackpackItem> getItems() {
        return items;
    }

    public Chest getChest() {
        return chest;
    }

    public boolean isFinalRoom() {
        return finalRoom;
    }

    public boolean isDungeonEntrance() {
        return dungeonEntrance;
    }

    public boolean hasPrince() {
        return hasPrince;
    }

    public boolean hasChest () {
        if (chest == null) {
            return false;
        }
        return true;
    }

    public Hero getHero () {
        return hero;
    }

    public void addHero (Hero hero) {
        this.hero = hero;
    }

    public void removeHero () {
        hero = null;
    }

    public void setRoomName (String roomName) {
        this.roomName = roomName;
    }

    public void setRoomName () {
        throw new AssertionError("todo: make this method come up with a name");
    }

    public boolean isCleared () {
        return monsters.size() == 0;
    }
}
