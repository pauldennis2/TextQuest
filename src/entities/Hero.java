package entities;

import enums.Direction;
import interfaces.LevelUpAction;
import interfaces.RoomAction;
import interfaces.SpellAction;
import utils.SafeNumScanner;
import utils.VictoryException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Paul Dennis on 8/8/2017.
 */
public class Hero {

    private int health;
    private int maxHealth;

    private int might;
    private int maxMight;

    private int magic;

    private int sneak;
    private int maxSneak;

    private int defense;
    private int maxDefense;

    private int level;
    private int exp;

    //private List<BackpackItem> backpack;
    private Backpack backpack;
    private DungeonRoom location;
    private DungeonRoom previousLocation;

    private boolean isSneaking;

    private Map<String, RoomAction> heroActionMap;
    Map<String, LevelUpAction> levelUpActionMap;
    Map<String, SpellAction> possibleSpellMap;
    Map<String, SpellAction> spellMap;

    private int maxSpellsPerDay;

    public static final double TORCH_LIGHT = 1.0;
    public static final int POTION_VALUE = 9;
    private Random random;

    SafeNumScanner safeNumScanner;

    public Hero () {
        random = new Random();
        health = 50;
        maxHealth = 50;
        might = 10;
        maxMight = 10;
        magic = 2;
        sneak = 0;
        maxSneak = 2;

        level = 0;
        exp = 0;
        maxSpellsPerDay = 0;

        backpack = new Backpack();
        heroActionMap = new HashMap<>();
        spellMap = new HashMap<>();
        safeNumScanner = new SafeNumScanner(System.in);
        initLevelUpMap();
        initPossibleSpellMap();
        initActionMap();
        backpack.add(new BackpackItem("Torch", backpack));
        backpack.add(new BackpackItem("Sword", backpack));
        backpack.add(new BackpackItem("Bow & Arrows", backpack));
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

        heroActionMap.put("Unlock Chest", room -> {
            Container chest = room.getChest();
            room.getHero().backpack.stream()
                    .filter(item -> item.getName().contains("Key"))
                    .forEach(chest::unlock);
            if (chest.isLocked()) {
                System.out.println("You don't have the key");
            } else {
                System.out.println("Trademarked chest-opening music, rapid ascending style.");
            }
        });

        heroActionMap.put("Loot Chest", room -> {
            Container chest = room.getChest();
            List<BackpackItem> contents = chest.removeContents();
            if(contents == null) {
                throw new AssertionError("Chest was probably locked. Option should not have been offered.");
            }
            boolean victoryFlag = false;
            //contents.stream().forEach(backpack::add);
            for (BackpackItem item : contents) {
                backpack.add(item);
                item.setLocation(backpack);
                if (item.isQuestItem()) {
                    victoryFlag = true;
                }
            }
            if (victoryFlag) {
                throw new VictoryException("Found the special item.");
            }
        });
    }

    private void levelUp () {
        if (level >= 12) {
            System.out.println("You are max level");
            return;
        }
        System.out.println("What would you like to do?");
        List<String> levelUpActionStrings = levelUpActionMap.keySet().stream().collect(Collectors.toList());
        int index = 1;
        for (String action : levelUpActionStrings) {
            System.out.println(index + ". " + action);
            index++;
        }
        int response = safeNumScanner.getSafeNum(1, levelUpActionStrings.size());
        LevelUpAction action = levelUpActionMap.get(levelUpActionStrings.get(response - 1));
        action.doAction(this);
        level++;
        printStats();
    }

    private void initLevelUpMap () {
        levelUpActionMap = new HashMap<>();
        levelUpActionMap.put("Increase HP", hero -> hero.maxHealth += 5);
        levelUpActionMap.put("Increase Might", hero -> hero.might++);
        levelUpActionMap.put("Sneak", hero -> hero.sneak++);
        levelUpActionMap.put("Learn Spell", hero -> hero.learnNewSpell());
    }

    private void initPossibleSpellMap () {
        possibleSpellMap = new HashMap<>();
        possibleSpellMap.put("Heal", hero -> hero.restoreHealth(15));
        possibleSpellMap.put("Moonlight Shadow", hero -> hero.sneak += 5);
        possibleSpellMap.put("Fireblast", hero -> {
            DungeonRoom room = hero.getLocation();
            room.getMonsters().forEach(e -> e.takeDamage(3));
            room.updateMonsters();
        });
        possibleSpellMap.put("Weaken", hero ->  hero.getLocation().getMonsters()
                .forEach(e -> e.setMight(e.getMight() - 1)));
    }

    private void learnNewSpell () {
        maxSpellsPerDay++;
        List<String> spellNames = possibleSpellMap.keySet().stream()
                //.filter(e -> ) //filter out spells we know
                .collect(Collectors.toList());
        int index = 1;
        System.out.println("Which spell to learn?");
        for (String spell : spellNames) {
            System.out.println(index + ". " + spell);
            index++;
        }
        int response = safeNumScanner.getSafeNum(1, spellNames.size());
        String actionString = spellNames.get(response - 1);
        spellMap.put(actionString, possibleSpellMap.get(actionString));
        System.out.println("You have learned " + actionString);
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
        health += healthAmount;
        if (health > maxHealth) {
            health = maxHealth;
        }
    }

    public void printStats () {
        System.out.println("Health: " + health + "/" + maxHealth + "  (Might, Magic, Sneak) (" +
                might + ", " + magic + ", " + sneak + ") Level: " + level + ", Exp: " + exp);
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

    public static final int[] LEVEL_AMTS = {250, 1000, 2500, 4500, 6500, 9000, 12000, 15000, 18500, 21500, 25000, 35000, 50000};
    //Max level 12
    public void addExp (int exp) {
        if (exp < 0) {
            throw new AssertionError();
        }
        this.exp += exp;
        System.out.println("Gained " + exp + " exp.");
        if (LEVEL_AMTS[level] < exp) {
            levelUp();
        }
    }

    public void fightMonster (Monster monster) {
        System.out.println("Fighting " + monster.getName());
        while (true) {
            int damageRoll = random.nextInt(might) + 1;
            System.out.println("\tYou did " + damageRoll + " damage.");
            monster.takeDamage(damageRoll);

            if (monster.getHealth() == 0) {
                addExp(monster.getExp());
                System.out.println("Won fight against " + monster.getName() + ".");
                if (monster.isBoss()) {
                    throw new VictoryException("Beat the evil boss, " + monster.getName() + ".");
                }
                break;
            }

            int monsterDamage = random.nextInt(monster.getMight() + 1);
            System.out.println("\t" + monster.getName() + " did " + monsterDamage + " to you.");
            takeDamage(monsterDamage);
            if (health == 0) {
                System.out.println("You lost the fight against " + monster.getName());
                break;
            }
        }
    }

    private void takeDamage (int damageAmount) {
        health -= damageAmount;
        if (health < 0) {
            health = 0;
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
            if (location.getItems().size() > 0) {
                actions.add("Loot");
            }
            if (location.hasChest()) {
                Container chest = location.getChest();
                if (chest.getContents().size() > 0) {
                    if (chest.isLocked()) {
                        actions.add("Unlock Chest");
                    } else {
                        actions.add("Loot Chest");
                    }
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

    public int getHealth() {
        return health;
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

    public Backpack getBackpack() {
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
