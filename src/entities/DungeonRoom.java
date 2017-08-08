package entities;

import enums.Direction;

import java.util.*;

/**
 * Created by Paul Dennis on 8/8/2017.
 */
public class DungeonRoom {

    private Map<Direction, DungeonRoom> connectedRooms;

    private double lighting;

    private List<Monster> monsters;
    private List<BackpackItem> items;
    private Container chest;

    private boolean finalRoom; //Contains either boss, item, or prince
    private boolean dungeonEntrance;
    private boolean hasPrince;

    public DungeonRoom (Random random) {
        finalRoom = false;
        dungeonEntrance = false;
        connectedRooms = new HashMap<>();
        monsters = new ArrayList<>();
        items = new ArrayList<>();
        lighting = Math.random();

        int numMonsters = random.nextInt(3);
        for (int i = 0; i < numMonsters; i++) {
            monsters.add(new Monster(false, random));
        }
    }

    public DungeonRoom (boolean finalRoom, boolean dungeonEntrance) {
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

    public void addContainer (Container container) {
        this.chest = container;
    }

    public void addItem (BackpackItem item) {
        items.add(item);
    }

    public void connectTo (Direction direction, DungeonRoom other) {
        if (connectedRooms.get(direction) != null || other == null) {
            throw new AssertionError();
        }

        connectedRooms.put(direction, other);
        other.connectTo(direction.getOpposite(), this);
    }

    public Set<Direction> getTravelDirections () {
        return connectedRooms.keySet();
    }

    public void describeRoom () {
        Random random = new Random();
        if (lighting > 0.8) {
            System.out.println("The room is well lit. You can clearly see:");
            monsters.stream().forEach(System.out::println);
            items.stream().forEach(System.out::println);
            if (chest != null) {
                System.out.println(chest);
            }
            if (monsters.size() + items.size() == 0) {
                System.out.println("(There is nothing in the room.)");
            }
        } else if (lighting == 0) {
            System.out.println("The room is pitch black. You cannot see anything.");
        } else {
            System.out.println("The room is not well lit. You can only make out a few shapes.");
            System.out.println("You can see " + monsters.size() + " figures moving in the darkness.");
            System.out.println("You think you can see the following items:");
            items.stream()
                    .filter(e -> Math.random() < lighting * 2)
                    .forEach(System.out::println);
        }
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

    public Container getChest() {
        return chest;
    }

    public boolean isFinalRoom() {
        return finalRoom;
    }

    public boolean isDungeonEntrance() {
        return dungeonEntrance;
    }

    public boolean isHasPrince() {
        return hasPrince;
    }
}
