package paul.NLPTextDungeon.entities;

import paul.NLPTextDungeon.interfaces.*;
import paul.NLPTextDungeon.utils.BufferedOutputTextStream;
import paul.NLPTextDungeon.entities.obstacles.Chasm;
import paul.NLPTextDungeon.enums.Direction;
import paul.NLPTextDungeon.enums.SpeakingVolume;
import paul.NLPTextDungeon.interfaces.listeners.OnPickup;
import paul.NLPTextDungeon.utils.DefeatException;
import paul.NLPTextDungeon.utils.ItemActionMap;
import paul.NLPTextDungeon.utils.VictoryException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Paul Dennis on 8/8/2017.
 */
public class Hero implements TextOuter {

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

    private Map<String, VoidAction> heroVoidActions;
    private Map<String, ParamAction> heroParamActions;

    private ItemActionMap itemActions;
    //private Map<String, VoidAction> itemActions;
    private Map<String, VoidAction> views;
    Map<String, LevelUpAction> levelUpActionMap;
    Map<String, SpellAction> possibleSpellMap;
    Map<String, SpellAction> spellMap;

    private int maxSpellsPerDay;

    public static final double TORCH_LIGHT = 1.0;
    public static final int POTION_VALUE = 9;
    private Random random;

    BufferedOutputTextStream textOut;

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
        heroVoidActions = new HashMap<>();
        heroParamActions = new HashMap<>();
        itemActions = new ItemActionMap();
        views = new HashMap<>();
        initActionMap();
        initItemActions();
        initViews();
        
        backpack = new Backpack();
        backpack.add(new BackpackItem("Torch"));
        backpack.add(new BackpackItem("Sword"));
        backpack.add(new BackpackItem("Bow"));
        spellMap = new HashMap<>();
        initLevelUpMap();
        initPossibleSpellMap();
        initPickupListenerMap();
    }

    public void takeAction (String action) {
        VoidAction voidAction = heroVoidActions.get(action);
        if (voidAction != null) {
            voidAction.doAction(location);
        } else {
            textOut.debug("Action not in map.");
            throw new AssertionError();
        }
    }

    public void setTextOut (BufferedOutputTextStream textOut) {
        this.textOut = textOut;
    }

    public void takeAction (String action, String param) {
        ParamAction paramAction = heroParamActions.get(action);
        if (paramAction != null) {
            paramAction.doAction(location, param);
        } else {
            textOut.debug("Action not in map.");
            throw new AssertionError();
        }
    }

    private void initItemActions () {
        itemActions.put("torch", room -> {
            room.setLighting(TORCH_LIGHT);
            textOut.println("The room brightens up.");
        });
        itemActions.put("potion", room -> {
            this.restoreHealth(POTION_VALUE);
            this.removeItem("Potion");
            textOut.println("Drank a potion and restored up to 9 health.");
        });
        itemActions.put("bow", room -> textOut.println("You don't know how to use that yet."));
    }

    private void initViews () {
        views.put("status", room -> room.getHero().printStats());
        views.put("map", room -> textOut.println("Map not yet implemented."));
        views.put("backpack", room -> textOut.println(room.getHero().backpack));
    }


    private Map<String, OnPickup> listenerMap;
    private void initPickupListenerMap () {
        listenerMap = new HashMap<>();
        listenerMap.put("victory", () -> {
            throw new VictoryException("You win!");
        });
        listenerMap.put("crackFloor", () -> {
            textOut.println("CRAAACK!!!! The floor of the room splits and a giant chasm appears.");
            getLocation().addObstacle(new Chasm());
            textOut.tutorial("Try using your new Boots of Vaulting to JUMP across the chasm.");
            previousLocation = null; //Prevent retreating
        });
    }

    private void initActionMap () {
        heroVoidActions.put("loot", room -> room.lootRoom().forEach(item -> {
            if (item.hasPickupAction()) {
                OnPickup action = listenerMap.get(item.getPickupAction());
                action.doAction();
            }
            backpack.add(item);
        }));
        heroVoidActions.put("retreat", room -> room.getHero().retreat());
        heroVoidActions.put("sneak", room -> textOut.println("You don't know how to sneak yet."));
        heroVoidActions.put("cast", room -> textOut.println("You don't know any spells yet."));
        heroVoidActions.put("learn", room -> textOut.println("That function is not available at this time."));

        heroVoidActions.put("fight", room -> {
            room.getMonsters().forEach(room.getHero()::fightMonster);
            room.updateMonsters();
        });
        heroVoidActions.put("rescue", room -> textOut.println("No princes to rescue right now."));

        heroVoidActions.put("plunder", room -> {
            Chest chest = room.getChest();
            if (chest == null) {
                textOut.println("Nothing to plunder here.");
            }
            room.getHero().backpack.stream()
                    .filter(item -> item.getName().contains("Key"))
                    .forEach(chest::unlock);
            if (chest.isLocked()) {
                textOut.println("You don't have the key");
            } else {
                textOut.println("Trademarked chest-opening music, rapid ascending style.");
            }
            List<BackpackItem> chestContents = chest.removeContents();
            chestContents.forEach(item -> {
                if (item.hasPickupAction()) {
                    OnPickup action = listenerMap.get(item.getPickupAction());
                    action.doAction();
                }
                backpack.add(item);
            });
        });

        heroParamActions.put("use", (room, param) -> {
            if (room.getHero().getBackpack().contains(param)) {
                itemActions.get(param).doAction(room);
            } else {
                textOut.println("You don't have a " + param + " to use.");
            }
        });
        heroParamActions.put("move", (room, param) -> proceed(Direction.valueOf(param.toUpperCase())));
        heroParamActions.put("view", (room, param) -> views.get(param).doAction(room));

        heroParamActions.put("say", (room, param) -> room.vocalize(param, SpeakingVolume.SAY));
        heroParamActions.put("whisper", (room, param) -> room.vocalize(param, SpeakingVolume.WHISPER));
        heroParamActions.put("shout", (room, param) -> room.vocalize(param, SpeakingVolume.SHOUT));

        heroParamActions.put("search", (room, param) -> {
            List<BackpackItem> hiddenItems = room.getHiddenItems().get(param);
            if (hiddenItems == null) {
                textOut.println("You didn't find anything near " + param);
            } else {
                textOut.println("Searching around " + param + ", you found:");
                hiddenItems.forEach(item -> {
                    textOut.println(item);
                    backpack.add(item);
                });
            }
        });

        heroVoidActions.put("jump", room -> {
            if (backpack.contains("Boots of Vaulting")) {
                if (room.getObstacles().size() == 0) {
                    textOut.println("Nothing to jump.");
                } else {
                    room.getObstacles().stream()
                            .filter(e -> e.getSolution().equals("jump")) //Filter out non-chasms
                            .forEach(e -> {
                                boolean success = e.attempt("jump", this);
                                textOut.println("You made it across!");
                            });
                }
            } else {
                textOut.println("Hmm... not much happened.");
            }
        });
    }

    private void levelUp () {
        if (level >= 12) {
            textOut.println("You are max level");
            return;
        }
        textOut.println("What would you like to do?");
        List<String> levelUpActionStrings = new ArrayList<>(levelUpActionMap.keySet());
        int index = 1;
        for (String action : levelUpActionStrings) {
            textOut.println(index + ". " + action);
            index++;
        }
        int response = 1;
        if (response == 1) {
            //todo fix
            throw new AssertionError("fix");
        }
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
        levelUpActionMap.put("Learn Spell", Hero::learnNewSpell);
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
                .filter(e -> e.length() > 0) //filter out spells we know. TODO write actual filter
                .collect(Collectors.toList());
        int index = 1;
        textOut.println("Which spell to learn?");
        for (String spell : spellNames) {
           textOut.println(index + ". " + spell);
            index++;
        }
        int response = 1;
        if (response == 1) {
            //Todo fix
            throw new AssertionError("fix");
        }
        String actionString = spellNames.get(response - 1);
        spellMap.put(actionString, possibleSpellMap.get(actionString));
        textOut.println("You have learned " + actionString);
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
        textOut.println("Restored up to " + healthAmount + ". Current health = " + health);
    }

    public void printStats () {
        textOut.println("Health: " + health + "/" + maxHealth + "  (Might, Magic, Sneak) (" +
                might + ", " + magic + ", " + sneak + ") Level: " + level + ", Exp: " + exp);
    }

    private void proceed (Direction direction) {
        DungeonRoom nextRoom = location.getConnectedRooms().get(direction);
        if (nextRoom == null) {
            textOut.println("Cannot go that way (no connected room).");
            return;
        }
        location.removeHero();
        setLocation(nextRoom);
    }

    private void retreat () {
        if (previousLocation != null) {
            setLocation(previousLocation);
        } else {
            textOut.println("Cannot retreat right now.");
        }
    }

    public static final int[] LEVEL_AMTS = {250, 1000, 2500, 4500, 6500, 9000, 12000, 15000, 18500, 21500, 25000, 35000, 50000};
    //Max level 12
    public void addExp (int exp) {
        if (exp < 0) {
            throw new AssertionError();
        }
        this.exp += exp;
        textOut.println("Gained " + exp + " exp.");
        if (LEVEL_AMTS[level] < exp) {
            levelUp();
        }
    }

    public void fightMonster (Monster monster) {
        textOut.println("Fighting " + monster.getName());
        while (true) {
            int damageRoll = random.nextInt(might) + 1;
            textOut.println("\tYou did " + damageRoll + " damage.");
            monster.takeDamage(damageRoll);

            if (monster.getHealth() == 0) {
                addExp(monster.getExp());
                textOut.println("Won fight against " + monster.getName() + ".");
                break;
            }

            int monsterDamage = random.nextInt(monster.getMight() + 1);
            textOut.println("\t" + monster.getName() + " did " + monsterDamage + " to you.");
            takeDamage(monsterDamage);
            if (health == 0) {
                textOut.println("You lost the fight against " + monster.getName());
                break;
            }
        }
    }

    public void takeDamage (int damage) {
        health -= damage;
        textOut.println("You took " + damage + " damage.");
        if (health <= 0) {
            health = 0;
            throw new DefeatException("Died from damage. Or perhaps dafighter.");
        }
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
        this.location.setHero(this);
        if (previousLocation != null) {
            previousLocation.removeHero();
        }
    }
}
