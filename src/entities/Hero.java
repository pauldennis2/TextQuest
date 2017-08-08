package entities;

import enums.Direction;
import interfaces.RoomAction;
import utils.SafeNumScanner;
import utils.VictoryException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Paul Dennis on 8/8/2017.
 */
public class Hero {

    private int curHealth;
    private int maxHealth;
    private int might;
    private int magic;
    private int sneak;

    private int level;
    private int exp;

    private List<BackpackItem> backpack;
    private DungeonRoom location;
    private DungeonRoom previousLocation;

    private boolean isSneaking;

    private Map<String, RoomAction> heroActionMap;

    public static final double TORCH_LIGHT = 1.0;
    public static final int POTION_VALUE = 9;
    private Random random;

    public Hero () {
        random = new Random();
        curHealth = 50;
        maxHealth = 50;
        might = 10;
        magic = 2;
        sneak = 0;

        level = 1;
        exp = 0;

        backpack = new ArrayList<>();
        heroActionMap = new HashMap<>();
        initActionMap();
        backpack.add(new BackpackItem("Torch"));
        backpack.add(new BackpackItem("Sword"));
        backpack.add(new BackpackItem("Bow & Arrows"));
    }

    private void initActionMap () {
        heroActionMap.put("Torch", room -> room.setLighting(TORCH_LIGHT));
        heroActionMap.put("Fight", room -> {
            room.getMonsters().stream().forEach(room.getHero()::fightMonster);
            room.updateMonsters();
        });
        heroActionMap.put("Potion", room -> {
            room.getHero().restoreHealth(POTION_VALUE);
            room.getHero().removeItem("Potion");
        });

        heroActionMap.put("Loot", room -> room.lootRoom().stream().forEach(backpack::add));
        heroActionMap.put("Proceed", room -> proceed(chooseDirection(room.getTravelDirections())));
        heroActionMap.put("Retreat", room -> room.getHero().retreat());
        heroActionMap.put("Character Status", room -> room.getHero().printStats());
        heroActionMap.put("Show Map (NYI)", room -> System.out.println("Showing map is Not Yet Implemented"));
        heroActionMap.put("Rescue Prince", room -> rescuePrince());


        heroActionMap.put("Key", room -> room.getChest().unlock(room.getChest().getKey()));

        heroActionMap.put("Loot Chest", room -> {
            Container chest = room.getChest();
            room.getHero().backpack.stream()
                    .filter(item -> item.getName().contains("Key"))
                    .forEach(chest::unlock);

            List<BackpackItem> contents = chest.removeContents();
            boolean victoryFlag = false;
            //contents.stream().forEach(backpack::add);
            for (BackpackItem item : contents) {
                backpack.add(item);
                if (item.isQuestItem()) {
                    victoryFlag = true;
                }
            }
            if (victoryFlag) {
                throw new VictoryException("Found the special item.");
            }
        });
    }

    public void rescuePrince () {
        throw new VictoryException("Rescued the handsome Prince Charming.");
    }

    public void removeItem (String itemName) {
        BackpackItem toBeRemoved = backpack.stream()
                .filter(e -> e.getName().equals(itemName))
                .findFirst()
                .get();

        backpack.remove(toBeRemoved);
    }

    public void restoreHealth (int healthAmount) {
        curHealth += healthAmount;
        if (curHealth > maxHealth) {
            curHealth = maxHealth;
        }
    }

    public void printStats () {
        System.out.println("Health: " + curHealth + "/" + maxHealth + "  (Might, Magic, Sneak, Exp)  4" +
                might + ", " + magic + ", " + sneak + ", " + exp);
    }

    private void proceed (Direction direction) {
        DungeonRoom nextRoom = location.getConnectedRooms().get(direction);
        if (nextRoom == null) {
            throw new AssertionError();
        }

        setLocation(nextRoom);
    }

    private Direction chooseDirection (Set<Direction> directions) {
        if (directions.size() > 1) {
            int index = 1;
            System.out.println("Which direction do you want to go?");
            List<Direction> directionList = directions.stream().collect(Collectors.toList());
            for (Direction direction : directionList) {
                System.out.println("" + index + ". " + direction);
                index++;
            }
            SafeNumScanner safeNumScanner = new SafeNumScanner(System.in);
            int response = safeNumScanner.getSafeNum(1, directionList.size());
            return directionList.get(response - 1);
        } else {
            System.out.println("Only one possible way to go.");
            return directions.stream().findFirst().get();
        }
    }

    private void retreat () {
        setLocation(previousLocation);
    }

    public void takeAction (String action) {
        RoomAction roomAction = heroActionMap.get(action);
        if (roomAction != null) {
            roomAction.doAction(location);
        } else {
            System.out.println("Action not in map.");
            throw new AssertionError();
        }
    }

    public void fightMonster (Monster monster) {
        System.out.println("Fighting " + monster.getName());
        while (true) {
            int damageRoll = random.nextInt(might) + 1;
            System.out.println("\tYou did " + damageRoll + " damage.");
            monster.takeDamage(damageRoll);

            if (monster.getHealth() == 0) {
                exp += monster.getExp();
                System.out.println("Won fight against " + monster.getName() + ". Gained " + monster.getExp() + " exp.");
                if (monster.isBoss()) {
                    throw new VictoryException("Beat the evil boss, " + monster.getName() + ".");
                }
                break;
            }

            int monsterDamage = random.nextInt(monster.getStrength() + 1);
            System.out.println("\t" + monster.getName() + " did " + monsterDamage + " to you.");
            takeDamage(monsterDamage);
            if (curHealth == 0) {
                System.out.println("You lost the fight against " + monster.getName());
                break;
            }
        }
    }

    private void takeDamage (int damageAmount) {
        curHealth -= damageAmount;
        if (curHealth < 0) {
            curHealth = 0;
        }
    }

    public List<String> getRoomActions () {
        List<String> actions = getBackpackItemActions();

        //Always available, informational (for now)
        actions.add("Show Map (NYI)");
        actions.add("Character Status");

        if (location.getMonsters().size() > 0) {
            //If monsters are present, can only do these:
            actions.add("Fight");
            actions.add("Sneak");
            actions.add("Retreat");
        } else if (location.getMonsters().size() == 0 || isSneaking) {
            //If monsters are gone or we are sneaking
            if (location.getItems().size() > 0 || location.hasChest()) {
                actions.add("Loot");
            }
            if (location.hasChest()) {
                Container chest = location.getChest();
                if (chest.getContents().size() > 0) {
                    actions.add("Loot Chest");
                }
            }
            if (location.hasPrince()) {
                actions.add("Rescue Prince");
            }
            actions.add("Proceed");
        }
        return actions;
    }

    private List<String> getBackpackItemActions () {
        Set<String> actionMapKeys = heroActionMap.keySet();

        return backpack.stream()
                .map(e -> e.getName())
                .filter(e -> actionMapKeys.contains(e))
                .collect(Collectors.toList());
    }

    public int getCurHealth() {
        return curHealth;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public boolean isSneaking() {
        return isSneaking;
    }

    public int getMight() {
        return might;
    }

    public int getMagic() {
        return magic;
    }

    public int getSneak() {
        return sneak;
    }

    public int getLevel() {
        return level;
    }

    public int getExp() {
        return exp;
    }

    public List<BackpackItem> getBackpack() {
        return backpack;
    }

    public DungeonRoom getLocation() {
        return location;
    }

    public void setLocation (DungeonRoom location) {
        previousLocation = this.location;
        this.location = location;
        this.location.addHero(this);
        if (previousLocation != null) {
            previousLocation.removeHero();
        }
    }

}
