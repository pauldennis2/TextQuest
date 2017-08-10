package entities;

import enums.Direction;
import interfaces.ParamAction;
import interfaces.VoidAction;
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

    private Map<String, VoidAction> heroVoidActions;
    private Map<String, ParamAction> heroParamActions;

    private Map<String, VoidAction> itemActions;
    private Map<String, VoidAction> views;

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
        heroVoidActions = new HashMap<>();
        heroParamActions = new HashMap<>();
        itemActions = new HashMap<>();
        views = new HashMap<>();
        initActionMap();
        initItemActions();
        initViews();
        backpack.add(new BackpackItem("Torch"));
        backpack.add(new BackpackItem("Sword"));
        backpack.add(new BackpackItem("Bow & Arrows"));
    }

    private void initItemActions () {
        itemActions.put("torch", room -> room.setLighting(TORCH_LIGHT));
        itemActions.put("potion", room -> {
            room.getHero().restoreHealth(POTION_VALUE);
            room.getHero().removeItem("Potion");
        });
    }

    private void initViews () {
        views.put("status", room -> room.getHero().printStats());
        views.put("map", room -> System.out.println("Map not yet implemented."));
    }

    private void initActionMap () {
        heroVoidActions.put("loot", room -> room.lootRoom().forEach(backpack::add));
        heroVoidActions.put("retreat", room -> room.getHero().retreat());
        heroVoidActions.put("fight", room -> {
            room.getMonsters().forEach(room.getHero()::fightMonster);
            room.updateMonsters();
        });
        heroVoidActions.put("rescue", room -> rescuePrince());

        heroVoidActions.put("plunder", room -> {
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

        heroParamActions.put("use", (room, param) -> itemActions.get(param).doAction(room));
        heroParamActions.put("move", (room, param) -> proceed(Direction.valueOf(param.toUpperCase())));
        heroParamActions.put("view", (room, param) -> views.get(param).doAction(room));
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

    private void retreat () {
        setLocation(previousLocation);
    }

    public void takeAction (String action) {
        VoidAction voidAction = heroVoidActions.get(action);
        if (voidAction != null) {
            voidAction.doAction(location);
        } else {
            System.out.println("Action not in map.");
            throw new AssertionError();
        }
    }

    public void takeAction (String action, String param) {
        ParamAction paramAction = heroParamActions.get(action);
        if (paramAction != null) {
            paramAction.doAction(location, param);
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
