package paul.NLPTextDungeon.entities;

import paul.NLPTextDungeon.interfaces.*;
import paul.NLPTextDungeon.utils.TextInterface;
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

    private String name;

    private int health;
    private int maxHealth;

    private int might;
    private int magic;
    private int sneak;
    private int defence;
    private int maxSpellsPerDay;

    private int level;
    private int exp;

    private Backpack backpack;

    private transient int mightMod;
    private transient int magicMod;
    private transient int sneakMod;
    private transient int defenceMod;
    private transient int numSpellsAvailable;

    private transient DungeonRoom location;
    private transient DungeonRoom previousLocation;
    private transient boolean isSneaking;

    private transient Map<String, VoidAction> heroVoidActions;
    private transient Map<String, ParamAction> heroParamActions;
    private transient ItemActionMap itemActions;
    private transient Map<String, VoidAction> views;
    private transient Map<String, LevelUpAction> levelUpActionMap;
    private transient Map<String, SpellAction> possibleSpellMap;
    private transient Map<String, SpellAction> spellMap;

    private transient Random random;
    private transient TextInterface textOut;

    public static final double TORCH_LIGHT = 1.0;
    public static final int POTION_VALUE = 9;


    public Hero () {
        random = new Random();
        backpack = new Backpack();
        initMaps();
    }

    public Hero (String standard) {
        random = new Random();
        health = 50;
        maxHealth = 50;
        might = 10;
        magic = 2;
        sneak = 0;

        level = 0;
        exp = 0;
        maxSpellsPerDay = 0;

        backpack = new Backpack();
        backpack.add(new BackpackItem("Torch"));
        backpack.add(new BackpackItem("Sword"));
        backpack.add(new BackpackItem("Bow"));
        initMaps();
    }


    private void initMaps () {
        heroVoidActions = new HashMap<>();
        heroParamActions = new HashMap<>();
        itemActions = new ItemActionMap();
        views = new HashMap<>();
        spellMap = new HashMap<>();
        initActionMap();
        initItemActions();
        initViews();
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

    public void setTextOut (TextInterface textOut) {
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
        possibleSpellMap.put("heal", hero -> {
            hero.restoreHealth(15);
            textOut.println("You are healed for 15 health.");
        });
        possibleSpellMap.put("shadow", hero -> {
            hero.sneakMod = 5;
            textOut.println("The shadows surround you.");
        });
        possibleSpellMap.put("fire", hero -> {
            DungeonRoom room = hero.getLocation();
            room.getMonsters().forEach(e -> e.takeDamage(5));
            room.updateMonsters();
            textOut.println("All monsters are hit by a small fireblast, and take 5 damage.");
        });
        possibleSpellMap.put("lightning", hero -> {
            DungeonRoom room = hero.getLocation();
            Monster target = getRandomTarget(room.getMonsters());
            if (target != null) {
                textOut.println(target.getName() + " took 10 lightning damage.");
                target.takeDamage(10);
                room.updateMonsters();
            } else {
                textOut.println("There were no monsters to use lightning on. Spell wasted.");
            }
        });
        possibleSpellMap.put("ice", hero -> {
            DungeonRoom room = hero.getLocation();
            Monster target = getRandomTarget(room.getMonsters());
            if (target != null) {
                textOut.println(target.getName() + " took 8 cold damage and is disabled 1 round.");
                target.takeDamage(8);
                target.disable(1);
                room.updateMonsters();
            } else {
                textOut.println("No targets for ice spell. It was wasted.");
            }
        });
        possibleSpellMap.put("light", hero -> {
            textOut.debug("This spell doesn't really do what it should yet.");
            textOut.println("The room brightens up.");
            hero.getLocation().setLighting(TORCH_LIGHT);
        });
        possibleSpellMap.put("aegis", hero -> {
            hero.defenceMod = 5;
            textOut.debug("Aegis lasts forever.");
            textOut.println("A magic shield surrounds you.");
        });
        possibleSpellMap.put("push", hero -> {
            List<Monster> monsters = hero.getLocation().getMonsters();
            monsters.forEach(monster -> {
                monster.takeDamage(2);
                monster.disable(1);
            });
            textOut.println("All monsters knocked down and damaged.");
        });
        possibleSpellMap.put("weaken", hero ->  {
            hero.getLocation().getMonsters()
                    .forEach(e -> e.setMight(e.getMight() - 1));
            textOut.println("All enemies weakened.");
        });
    }

    private static Monster getRandomTarget (List<Monster> monsters) {
        if (monsters != null && monsters.size() > 1) {
            return monsters.get(new Random().nextInt(monsters.size() - 1));
        } else if (monsters != null && monsters.size() == 1) {
            return monsters.get(0);
        } else {
            return null;
        }
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

    public void setHealth(int health) {
        this.health = health;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void setMight(int might) {
        this.might = might;
    }

    public void setMagic(int magic) {
        this.magic = magic;
    }

    public void setSneak(int sneak) {
        this.sneak = sneak;
    }

    public int getDefence() {
        return defence;
    }

    public void setDefence(int defence) {
        this.defence = defence;
    }

    public int getMaxSpellsPerDay() {
        return maxSpellsPerDay;
    }

    public void setMaxSpellsPerDay(int maxSpellsPerDay) {
        this.maxSpellsPerDay = maxSpellsPerDay;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public void setBackpack(Backpack backpack) {
        this.backpack = backpack;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}