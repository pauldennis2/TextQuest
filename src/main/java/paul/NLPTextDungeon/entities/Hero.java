package paul.NLPTextDungeon.entities;

import paul.NLPTextDungeon.parsing.MagicUniversity;
import paul.NLPTextDungeon.entities.obstacles.SmashableObstacle;
import paul.NLPTextDungeon.enums.LevelUpCategory;
import paul.NLPTextDungeon.interfaces.*;
import paul.NLPTextDungeon.parsing.InputType;
import paul.NLPTextDungeon.parsing.TextInterface;
import paul.NLPTextDungeon.parsing.UserInterfaceClass;
import paul.NLPTextDungeon.utils.*;
import paul.NLPTextDungeon.entities.obstacles.Chasm;
import paul.NLPTextDungeon.enums.Direction;
import paul.NLPTextDungeon.enums.SpeakingVolume;
import paul.NLPTextDungeon.interfaces.listeners.OnPickup;

import java.util.*;

import static paul.NLPTextDungeon.enums.LevelUpCategory.INC_STATS;
import static paul.NLPTextDungeon.enums.LevelUpCategory.NEW_SKILL;
import static paul.NLPTextDungeon.enums.LevelUpCategory.NEW_SPELL;

/**
 * Created by Paul Dennis on 8/8/2017.
 */
public class Hero extends UserInterfaceClass {

    private String name;

    private int health;
    private int maxHealth;

    private int might;
    private int magic;
    private int sneak;
    private int defence;
    private int maxSpellsPerDay;

    //Base chance to be hit is 50%
    //Every 2 points of defense reduces chance to be hit 5%

    //Damage is reduced by 2 for every 5 points of defense
    //Damage is increased by 1 for every point of might
    //Damage = might + roll(0-might)
    //Every 4 points of might increases hit chance by 5%

    //Mine: might 5, def 3
    //Yours: might 3, def 1

    //My chance to hit: 50%, 5-10 damage
    //Your chance to hit: 45%, 3-6

    //Mine: might 10, def 8
    //Yours: attack 12, def 4

    //My chance to hit: 10-20 damage, 55% accurate
    //Your chance to hit: 10-22, 45% accurate

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
        might = 4;
        magic = 2;
        sneak = 0;

        level = 0;
        exp = 0;
        maxSpellsPerDay = 1;

        backpack = new Backpack();
        backpack.add(new BackpackItem("Torch"));
        backpack.add(new BackpackItem("Sword"));
        backpack.add(new BackpackItem("Bow"));
        initMaps();
    }

    @Override
    public void start (TextInterface textOut) {
        this.textOut = textOut;
    }

    private transient List<LevelUpCategory> levelUpTodo;
    @Override
    //This is for level-up
    public InputType show () {
        if (levelUpActions == null) {
            initLevelUpActionMap();
        }
        if (LEVEL_AMTS[level] < exp) {
            level++;
            levelUpTodo = levelUpActions.get(level);
            textOut.println("You are now level " + level + ". You can:");
            levelUpTodo.stream()
                    .map(LevelUpCategory::getPrettyName)
                    .forEach(e -> textOut.println(e));
            //Else if there are remaining level up actions to take
            //return show();
            return InputType.LEVEL_UP;
        } else if (levelUpTodo != null && levelUpTodo.size() > 0) {
            LevelUpCategory category = levelUpTodo.get(0);
            textOut.println("Right now you can: " + LevelUpCategory.getPrettyName(category));
            textOut.println(LevelUpCategory.getPrompt(category));
            return InputType.LEVEL_UP;
        } else {
            textOut.release(this);
            return InputType.FINISHED;
        }
    }

    @Override
    public InputType handleResponse (String response) {
        LevelUpCategory category = levelUpTodo.get(0);
        response = response.trim().toLowerCase();
        switch (category) {
            case INC_STATS:
                if (response.equals("might") || response.equals("strength")) {
                    might++;
                    textOut.println("Might permanently increased by 1");
                } else if (response.equals("health") || response.equals("hp") || response.equals("hitpoints")) {
                    maxHealth += 5;
                    textOut.println("Max HP increased by 5");
                } else if (response.startsWith("def")){
                    defence++;
                    textOut.println("Defence increased by 1 permanently");
                } else {
                    textOut.println("Could not read a stat");
                }
                levelUpTodo.remove(0);

            case NEW_SKILL:
                if (response.contains("sneak") || response.contains("stealth")) {
                    sneak++;
                    textOut.println("You've learned basic sneaking.");
                } else {
                    textOut.println("Could not find a skill (only one available is sneak - try that).");
                    return InputType.LEVEL_UP;
                }
                levelUpTodo.remove(0);

            case NEW_SPELL:
                MagicUniversity magicUniversity = MagicUniversity.getInstance();
                String spellMatch = magicUniversity.getSpellMatch(response);
                if (spellMatch != null) {
                    spellMap.put(spellMatch, possibleSpellMap.get(spellMatch));
                    textOut.println("You've learned a " + spellMatch + " spell.");
                } else {
                    textOut.println("Could not find the spell you want to learn.");
                }
                levelUpTodo.remove(0);
                maxSpellsPerDay++;
                numSpellsAvailable = maxSpellsPerDay;
        }
        return InputType.LEVEL_UP;
    }

    private static Map<Integer, ArrayList<LevelUpCategory>> levelUpActions;

    //Defines what we can do at each level (i.e. what new skills, stat increases, etc are possible)
    private static void initLevelUpActionMap () {
        levelUpActions = new HashMap<>();
        ArrayList<LevelUpCategory> list = new ArrayList<>();
        list.add(NEW_SPELL);
        list.add(NEW_SKILL);
        list.add(INC_STATS);
        levelUpActions.put(1, list);
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
        initPossibleSpellMap();
        initPickupListenerMap();
    }

    public void takeAction (String action) {
        VoidAction voidAction = heroVoidActions.get(action);
        if (voidAction != null) {
            voidAction.doAction(location);
        } else {
            if (location.getSpecialRoomActions().get(action) != null) {
                String roomAction = location.getSpecialRoomActions().get(action);
                String[] splits = roomAction.split(" ");
                switch (splits[0]) {
                    case "heal":
                        int amt = Integer.parseInt(splits[1]);
                        this.restoreHealth(amt);
                        break;
                    default:
                        throw new AssertionError("No other ops supported.");
                }
            } else {
                textOut.debug("Action not in map.");
                throw new AssertionError();
            }
        }
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
        views.put("status", room -> printStats());
        views.put("map", room -> textOut.println("Map not yet implemented."));
        views.put("backpack", room -> textOut.println(backpack));
        views.put("spellbook", room -> {
            textOut.println("Spells: (Available/Max): (" + numSpellsAvailable + "/" + maxSpellsPerDay + ")");
            textOut.println("Known Spells:");
            spellMap.keySet().forEach(textOut::println);
        });
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

        heroVoidActions.put("smash", room -> {
            textOut.println("Starting a smashing spree.");
            room.getObstacles().stream()
                    .filter(obs -> obs.getClass() == SmashableObstacle.class)
                    .filter(obs -> !obs.isCleared())
                    .forEach(obs -> {
                        boolean success = obs.attempt("smash", room.getHero());
                        if (success) {
                            textOut.println("You smashed " + obs.getName() + ".");
                        } else {
                            textOut.println("Ouch! " + obs.getName() + " is hard.");
                        }
                    });
        });
        heroVoidActions.put("loot", room -> room.lootRoom().forEach(item -> {
            if (item.hasPickupAction()) {
                OnPickup action = listenerMap.get(item.getPickupAction());
                action.doAction();
            }
            backpack.add(item);
        }));
        heroVoidActions.put("retreat", room -> room.getHero().retreat());
        heroVoidActions.put("sneak", room -> {
            switch (sneak) {
                case 0:
                    textOut.println("You don't know how to sneak yet.");
                    break;
                case 1:
                    isSneaking = random.nextBoolean();
                    if (isSneaking) {
                        textOut.println("You disappeared into the shadows.");
                        textOut.debug("Sneaking does nothing right now.");
                    } else {
                        textOut.println("You failed to sneak.");
                    }
                    break;
            }
        });
        heroVoidActions.put("learn", room -> textOut.println("That function is not available at this time."));

        heroVoidActions.put("fight", room -> textOut.getRunner().startCombat());

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

        heroParamActions.put("cast", (room, param) -> {
            if (numSpellsAvailable < 1) {
                textOut.println("Cannot cast anymore spells today.");
            } else {
                SpellAction action = spellMap.get(param);
                if (action != null) {
                    textOut.println("Casting " + param + " spell.");
                    action.doAction(this);
                    numSpellsAvailable--;
                } else {
                    textOut.println("You do not know a " + param + " spell.");
                }
            }
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
                                e.attempt("jump", this);
                                textOut.println("You made it across!");
                            });
                }
            } else {
                textOut.println("Hmm... not much happened.");
            }
        });
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

    public void rescuePrince () {
        throw new VictoryException("Rescued the handsome Prince Charming.");
    }

    public void removeItem (String itemName) {
        backpack.stream()
                .filter(e -> e.getName().equals(itemName))
                .findAny().ifPresent(item -> backpack.remove(item));
    }

    public void restoreHealth (int healthAmount) {
        health += healthAmount;
        if (health > maxHealth) {
            health = maxHealth;
        }
        textOut.println("Restored up to " + healthAmount + ". Current health = " + health);
    }

    public void printStats () {
        textOut.println("Health: " + health + "/" + maxHealth + "  (Might, Magic, Defence) (" +
                might + ", " + magic + ", " + defence + ") Level: " + level + ", Exp: " + exp);
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
    public void addExp (int expToAdd) {
        if (expToAdd < 0) {
            throw new AssertionError();
        }
        this.exp += expToAdd;
        textOut.println("Gained " + expToAdd + " exp.");
        if (LEVEL_AMTS[level] < exp) {
            textOut.println("***Ding! Level up.");
            textOut.request(this);
        }
    }

    public void takeDamage (int damage) {
        damage -= (defence / 5) * 2;
        if (damage <= 0) {
            textOut.println("Damage completely mitigated.");
        } else {
            health -= damage;
            textOut.println("You took " + damage + " damage.");
            if (health <= 0) {
                health = 0;
                throw new DefeatException("Died from damage. Or perhaps dafighter. Har har.");
            }
        }
    }

    public void takeNonMitigatedDamage (int damage) {
        health -= damage;
        textOut.println("You took " + damage + " damage.");
        if (health <= 0) {
            throw new DefeatException("Died from non-combat damage.");
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

    public TextInterface getTextOut() {
        return textOut;
    }
}