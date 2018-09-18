package paul.TextQuest.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import paul.TextQuest.LeavingRoomAction;
import paul.TextQuest.entities.obstacles.Obstacle;
import paul.TextQuest.entities.obstacles.SmashableObstacle;
import paul.TextQuest.enums.Direction;
import paul.TextQuest.enums.EquipSlot;
import paul.TextQuest.enums.LevelUpCategory;
import paul.TextQuest.enums.SpeakingVolume;
import paul.TextQuest.interfaces.*;
import paul.TextQuest.new_interfaces.EquipableItem;
import paul.TextQuest.parsing.InputType;
import paul.TextQuest.parsing.MagicUniversity;
import paul.TextQuest.parsing.TextInterface;
import paul.TextQuest.parsing.UserInterfaceClass;
import paul.TextQuest.utils.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Paul Dennis on 8/8/2017.
 */

public class Hero extends UserInterfaceClass implements Serializable {

    private String name;

    private int health;
    private int maxHealth;

    private int might;
    private int magic;
    private int sneak;
    private int defense;
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
    
    private List<String> spellbook;
    
    private List<String> clearedDungeons;

    private transient int mightMod;
    private transient int magicMod;
    private transient int sneakMod;
    private transient int defenseMod;
    private transient int numSpellsAvailable;
    
    private transient int block;
    private transient int disabledForRounds;

    private transient DungeonRoom location;
    private transient DungeonRoom previousLocation;
    private transient boolean isSneaking;

    private transient Map<String, VoidAction> heroVoidActions;
    private transient Map<String, ParamAction> heroParamActions;
    private transient ItemActionMap itemActions;
    private transient Map<String, VoidAction> views;
    
    private transient Map<String, SpellAction> spellMap;
    
    //TODO concept:
    //Hero has a list of statuses the dungeon can modify (poisoned, diseased, buffed, etc)
    //Buffs and debuffs
    private transient List<String> statusEffects;
    
    private Map<EquipSlot, EquipableItem> equippedItems;
    
    private static Map<String, SpellAction> possibleSpellMap;

    private transient Random random;
    private transient TextInterface textOut;

    public static final double TORCH_LIGHT = 1.0;
    public static final int POTION_VALUE = 9;
    
    public static final String SAVE_PATH = "save_data/";


    public Hero () {
        random = new Random();
        backpack = new Backpack();
        spellbook = new ArrayList<>();
        clearedDungeons = new ArrayList<>();
        equippedItems = new HashMap<>();
        initMaps();
    }
    
    public Hero (Hero startingInfo, String name) {
    	this();
    	if (name.contains(" ") || name.contains("/")) {
    		throw new AssertionError("Hero names cannot contain spaces or slashes. Name was: " + name);
    	}
    	this.name = name;
        health = startingInfo.health;
        maxHealth = startingInfo.maxHealth;
        might = startingInfo.might;
        magic = startingInfo.magic;
        sneak = startingInfo.sneak;
        level = startingInfo.level;
        exp = startingInfo.exp;
        maxSpellsPerDay = startingInfo.maxSpellsPerDay;
        backpack = startingInfo.backpack;
    }

    public Hero (String name) {
    	this();
    	if (name.contains(" ") || name.contains("/")) {
    		throw new AssertionError("Hero names cannot contain spaces or slashes. Name was: " + name);
    	}
    	this.name = name;
        health = 50;
        maxHealth = 50;
        might = 4;
        magic = 2;
        sneak = 0;

        level = 0;
        exp = 0;
        maxSpellsPerDay = 1;
        EquipableItem sword = new EquipableItem("Sword");
        sword.setMightMod(1);
        sword.setUndroppable(true);
        backpack.add(new BackpackItem("Torch"));
        backpack.add(sword);
        backpack.add(new BackpackItem("Bow"));
        
        EquipableItem noiseHelm = new EquipableItem("Noisehelm");
        noiseHelm.setOnEquip("print HELM_ON");
        noiseHelm.setOnUnequip("print HELM_OFF");
        backpack.add(noiseHelm);
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
                    levelUpTodo.remove(0);
                } else if (response.equals("health") || response.equals("hp") || response.equals("hitpoints")) {
                    maxHealth += 5;
                    health = maxHealth;
                    textOut.println("Max HP increased by 5");
                    levelUpTodo.remove(0);
                } else if (response.startsWith("def")){
                    defense++;
                    textOut.println("Defense increased by 1 permanently");
                    levelUpTodo.remove(0);
                } else {
                    textOut.println("Could not read a stat");
                }
                break;

            case NEW_SKILL:
                if (response.contains("sneak") || response.contains("stealth")) {
                    sneak++;
                    textOut.println("You've learned basic sneaking.");
                    levelUpTodo.remove(0);
                } else {
                    textOut.println("Could not find a skill (only one available is sneak - try that).");
                    return InputType.LEVEL_UP;
                }
                break;


            case NEW_SPELL:
                MagicUniversity magicUniversity = MagicUniversity.getInstance();
                String spellMatch = magicUniversity.getSpellMatch(response);
                if (spellMatch != null) {
                    spellMap.put(spellMatch, possibleSpellMap.get(spellMatch));
                    textOut.println("You've learned a " + spellMatch + " spell.");
                    spellbook.add(spellMatch);
                    levelUpTodo.remove(0);
                    maxSpellsPerDay++;
                    numSpellsAvailable = maxSpellsPerDay;
                } else {
                    textOut.println("Could not find the spell you want to learn.");
                }
                break;

        }
        if (levelUpTodo.size() == 0) {
            return InputType.FINISHED;
        }
        return InputType.LEVEL_UP;
    }

    private static Map<Integer, List<LevelUpCategory>> levelUpActions;
    public static final String LEVEL_UP_PLAN_LOCATION = "content_files/game/leveling/default_plan.json";
    //Defines what we can do at each level (i.e. what new skills, stat increases, etc are possible)
    private static void initLevelUpActionMap () {
    	try {
    		LevelUpPlan levelUpPlan = LevelUpPlan.buildFromFile(LEVEL_UP_PLAN_LOCATION);
    		levelUpActions = levelUpPlan.getLevelUpActions();
    	} catch (IOException ex) {
    		throw new AssertionError(ex);
    	}
    }

    private static Hero jsonRestore(String heroJson) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(heroJson, Hero.class);
    }

    //Don't name this method like a getter or it causes SO Error
    public String createJsonString() {
    	ObjectMapper objectMapper = new ObjectMapper();
    	
    	objectMapper.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true);    	
    	
    	try {
    		return objectMapper.writeValueAsString(this);
    	} catch (JsonProcessingException ex) {
    		ex.printStackTrace();
    		throw new AssertionError("Error");
    	}
    }
    
    public static void saveHeroToFile (String username, Hero hero) {
    	String fileName = SAVE_PATH + username + "/" + hero.getName() + ".json";
    	new File(SAVE_PATH + username).mkdirs();
    	try (FileWriter fileWriter = new FileWriter(new File(fileName))){
    		fileWriter.write(hero.createJsonString());
    	} catch (IOException ex) {
    		ex.printStackTrace();
    	}
    }
    
    public static Hero loadHeroFromFile (String fileName) {
    	try (Scanner fileScanner = new Scanner(new File(fileName))) {
    		String json = fileScanner.nextLine();
    		return jsonRestore(json);
    	} catch (FileNotFoundException ex) {
    		System.out.println("Could not find hero file at " + fileName);
    		return null;
    	} catch (IOException ex) {
    		throw new AssertionError(ex);
    	}
    }
    
    public static Hero loadHeroFromFile (String username, String heroName) {
    	String fileName = SAVE_PATH + username + "/" + heroName + ".json";
    	return loadHeroFromFile(fileName);
    }
    
    public static List<String> getHeroListForUser (String username) {
    	File folder = new File(SAVE_PATH + username);
    	return Arrays.stream(folder.listFiles())
    			.map(file -> file.getName().split("\\.")[0])
    			.collect(Collectors.toList());
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
    }

    public void takeAction (String action) {
    	String onHeroAction = location.getOnHeroAction().get(action);
    	boolean stops = false;
    	if (onHeroAction != null) {
    		location.doAction(onHeroAction);
    		if (onHeroAction.contains("!STOPS")) {
        		onHeroAction.replaceAll("!STOPS", "");
        		textOut.debug("This trigger stops the action.");
        		stops = true;
        	}
    	}
    	if (!stops) {
	        VoidAction voidAction = heroVoidActions.get(action);
	        if (voidAction != null) {
	            voidAction.doAction(location);
	        } else {
	            if (location.getSpecialRoomActions().get(action) != null) {
	                String roomAction = location.getSpecialRoomActions().get(action);
	                location.doAction(roomAction);
	            } else {
	                textOut.debug("Action not in map.");
	                throw new AssertionError();
	            }
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
            textOut.println("Drank a potion.");
        });
        itemActions.put("bow", room -> textOut.println("You don't know how to use that yet."));
    }

    private void initViews () {
        views.put("status", room -> printStats());
        views.put("map", room -> textOut.println("Map not yet implemented."));
        views.put("backpack", room -> {
        	textOut.println("You have the following items in your pack: " + StringUtils.prettyPrintList(backpack.getItems()));
        });
        views.put("spellbook", room -> {
            if (spellMap.keySet().size() == 0) {
                textOut.println("You don't know any spells yet.");
            } else {
                textOut.println("Spells: (Available/Max): (" + numSpellsAvailable + "/" + maxSpellsPerDay + ")");
                textOut.println("Known Spells:");
                spellbook.forEach(textOut::println);
            }
        });
    }

    private void initActionMap () {
    	
    	//TODO remove this when no longer needed
    	heroVoidActions.put("save", room -> {
    		saveHeroToFile("paul", this);
    	});

        heroVoidActions.put("describe", DungeonRoom::describe);

        heroVoidActions.put("douse", room -> room.setLighting(0.0));

        heroVoidActions.put("smash", room -> {
            room.getObstacles().stream()
                    .filter(obs -> obs.getClass() == SmashableObstacle.class)
                    .filter(obs -> !obs.isCleared())
                    .forEach(obs -> {
                        boolean success = obs.attempt("smash", room.getHero());
                        if (success) {
                            textOut.println("You smashed a " + obs.getName() + ".");
                        } else {
                            textOut.println("Ouch! " + obs.getName() + " is hard.");
                        }
                    });
        });
        heroVoidActions.put("loot", room -> {
        	room.lootRoom()
            .stream()
            .filter(item -> item.isVisible(location.getLighting()))
            .forEach(item -> {
                if (item.hasPickupAction()) {
                    room.doAction(item.getOnPickup());
                }
                backpack.add(item);
                textOut.println("Picked up " + item.getName());
            });
        	Chest chest = room.getChest();
        	if (chest != null && chest.isOpen()) {
        		List<BackpackItem> chestContents = chest.removeContents();
                chestContents.forEach(item -> {
                    if (item.hasPickupAction()) {
                        room.doAction(item.getOnPickup());
                    }
                    textOut.println("Looted " + item.getName() + " from chest.");
                    backpack.add(item);
                });
        	} else if (room.getChest() != null) {
        		textOut.println("You can't loot from a chest that's not open.");
        	}
        });
        
        heroVoidActions.put("open", room -> {
        	Chest chest = room.getChest();
        	if (chest == null) {
        		textOut.println("There's no chest to open.");
        	} else {
        		chest.open();
	        	room.getHero().backpack.stream()
	            	.filter(item -> item.getName().contains("Key"))
	            	.forEach(chest::open);
	        	if (chest.isLocked()) {
	        		textOut.println("You don't have the key to " + chest.getName() + ".");
	        	}
        	}
        });
        
        heroVoidActions.put("plunder", room -> {
            Chest chest = room.getChest();
            if (chest == null) {
                textOut.println("Nothing to plunder here.");
            }
            room.getHero().backpack.stream()
                .filter(item -> item.getName().contains("Key"))
                .forEach(chest::unlock);
            if (chest.isLocked()) {
                textOut.println("You don't have the key to " + chest.getName() + ".");
            } else {
                List<BackpackItem> chestContents = chest.removeContents();
                chestContents.forEach(item -> {
                    if (item.hasPickupAction()) {
                        room.doAction(item.getOnPickup());
                    }
                    textOut.println("Looted " + item.getName() + " from chest.");
                    backpack.add(item);
                });
            }
        });
        
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
        
        heroVoidActions.put("leave", room -> {
        	boolean cleared = room.getDungeon().isCleared();
        	if (cleared) {
        		textOut.println("OK, you want to leave the dungeon, and it is cleared. No problem.");
        		textOut.println("No problem, we'll implement that soon.");
        	} else {
        		textOut.println("You are trying to leave the dungeon but it isn't cleared.");
        		textOut.println("If you leave you'll lose all progress on this dungeon.");
        		textOut.debug("Leaving isn't supported right now. You can check out any time you like but you can never leave.");
        		textOut.debug("(OK seriously, to leave just close the browser.)");
        	}
        });
        
        heroVoidActions.put("clean", room -> {
        	room.getFeatures().stream()
        		.filter(feature -> feature.getName().contains("Mirror") && feature.isVisible(room.getLighting()))
        		.filter(mirror -> { //Filter out mirrors that are already clean.
        			if (mirror.getStatus() != null) {
        				return !mirror.getStatus().equals("clean");
        			} else {
        				return true;
        			}
        		})
        		.forEach(feature -> {
        			feature.setStatus("clean");
        			textOut.println("You polished the " + feature.getName());
        		});
        });
        
        heroVoidActions.put("shine", room -> {
        	if (backpack.contains("Mirror Shield")) {
        		room.getObstacles().stream()
        			.filter(obs -> obs.getSolution().equals("shine"))
        			.forEach(shinePuzzle -> {
        				shinePuzzle.attempt("shine", this);
        			});
        	} else {
        		textOut.println("Shine off of what?");
        	}
        
        });
        //TODO: once equippable items are real, change these implementations
        //(shouldn't be backpack.contains() - should be if equipped
        heroVoidActions.put("jump", room -> {
            if (backpack.contains("Boots of Vaulting")) {
                if (room.getObstacles().size() == 0) {
                    textOut.println("Nothing to jump.");
                } else {
                    room.getObstacles().stream()
                            .filter(obs -> obs.getSolution().equals("jump")) //Filter out non-chasms
                            .forEach(chasm -> {
                                chasm.attempt("jump", this);
                                textOut.println("You made it across!");
                            });
                }
            } else {
                textOut.println("Hmm... not much happened.");
            }
        });
        
        heroVoidActions.put("block", room -> {
        	textOut.debug("Blocking doesn't do anything right now except against the boss.");
        	
        });

        heroParamActions.put("cast", (room, param) -> {
            if (numSpellsAvailable < 1) {
                textOut.println("Cannot cast anymore spells today.");
            } else {
                SpellAction action = spellMap.get(param);
                if (action != null) {
                    textOut.println("Casting " + param + " spell.");
                    if (room.getOnSpellCast() != null) {
                    	Map<String, String> onSpellCast = room.getOnSpellCast();
                    	if (onSpellCast.containsKey("any")) {
                    		room.doAction(onSpellCast.get("any"));
                    	}
                    	if (onSpellCast.containsKey(param)) {
                    		room.doAction(onSpellCast.get(param));
                    	}
                    }
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
                if (room.getOnItemUse() != null) {
	                if (room.getOnItemUse().containsKey("any")) {
	                	room.doAction(room.getOnItemUse().get("any"));
	                }
	                if (room.getOnItemUse().containsKey(param)) {
	                	room.doAction(room.getOnItemUse().get(param));
	                }
                }
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
            boolean triggerFlag = false;
            if (room.getOnSearch() != null && room.getOnSearch().containsKey(param)) {
            	textOut.debug("From searching near " + param + ", action = " + room.getOnSearch().get(param));
            	room.doAction(room.getOnSearch().get(param));
            	//onSearch triggers don't persist
            	room.getOnSearch().remove(param);
            	triggerFlag = true;
            }
            if (hiddenItems == null && !triggerFlag) {
                textOut.println("You didn't find anything from searching there.");
            } else if (hiddenItems != null) {
                textOut.println("Searching around " + param + ", you found:");
                hiddenItems.forEach(item -> {
                    textOut.println(item);
                    backpack.add(item);
                });
            }
        });
        
        heroParamActions.put("drop", (room, param) -> {
        	List<BackpackItem> items = backpack.getItems();
        	boolean found = false;
        	for (BackpackItem item : items) {
        		if (item.getName().toLowerCase().equals(param)) {
        			if (item.isUndroppable()) {
        				textOut.println("You can't drop " + item.getName());
        			} else {
        				backpack.remove(item);
        				textOut.println("Dropped " + item.getName());
        				room.addItem(item);
        				if (item.getOnDrop() != null) {
        					room.doAction(item.getOnDrop());
        				}
        			}
        			found = true;
        			break;
        		}
        	}
        	if (!found) {
        		textOut.println("You don't have a " + param + " to drop.");
        	}
        });
        
        heroParamActions.put("insert", (room, param) -> {
        	List<Container> containers = new ArrayList<>();
        	if (room.getChest() != null) {
        		containers.add(room.getChest());
        	}
        	room.getFeatures().stream()
        		.filter(Feature::isContainer)
        		.forEach(feature -> containers.add(feature));
        	if (containers.size() > 1) {
        		textOut.println("There are multiple containers. Requires disambiguation (unsupported)");
        		return;
        	} else if (containers.size() == 0) {
        		textOut.println("There are no containers into which to insert items.");
        		return;
        	}
        	Container container = containers.get(0);
        	List<BackpackItem> matchingItems = backpack.getItems().stream()
        		.filter(item -> item.getName().toLowerCase().contains(param.toLowerCase()))
        		.collect(Collectors.toList());
        	
        		matchingItems.forEach(item -> {
        			if (item.isUndroppable()) {
        				textOut.println("You can't drop/insert " + item.getName());
        			} else {
        				textOut.println("Inserted " + item.getName());
        				backpack.remove(item);
        				container.add(item);
        				String onInsert = container.getOnInsert().get(item.getName().toLowerCase());
        				if (onInsert != null) {
        					room.doAction(onInsert);
        				}
        			}
        		});
        });

        heroParamActions.put("equip", (room, param) -> {
        	List<BackpackItem> items = backpack.getItems();
        	boolean found = false;
        	for (BackpackItem item : items) {
        		if (item.getName().toLowerCase().equals(param)) {
        			if (item.getClass() == EquipableItem.class) {
        				
        			} else {
        				textOut.println("That item isn't equipable, sorry.");
        			}
        			found = true;
        		}
        	}
        	if (!found) {
        		textOut.println("You don't have a " + param + " to equip.");
        	}
        });
        
        heroParamActions.put("unequip", (room, param) -> {
        	//We'll attempt to find by item name first, then by slot
        	boolean found = false;
        	for (EquipSlot slot : equippedItems.keySet()) {
        		EquipableItem item = equippedItems.get(slot);
        		if (item.getName().toLowerCase().equals(param)) {
        			unequip(slot);
        			found = true;
        			textOut.println("Unequipped " + item.getName());
        		} else if (slot.toString().toLowerCase().equals(param)) {
        			unequip(slot);
        			found = true;
        			textOut.println("Unequipped " + item.getName());
        		}
        	}
        	if (!found) {
        		textOut.println("You don't have a " + param + " equipped.");
        	}
        });
        
    }

    private static void initPossibleSpellMap () {
        possibleSpellMap = new HashMap<>();
        possibleSpellMap.put("heal", hero -> {
            hero.restoreHealth(15);
            hero.textOut.println("You are healed for 15 health.");
        });
        possibleSpellMap.put("shadow", hero -> {
            hero.sneakMod = 5;
            hero.textOut.println("The shadows surround you.");
        });
        possibleSpellMap.put("fire", hero -> {
            DungeonRoom room = hero.getLocation();
            room.getMonsters().forEach(e -> e.takeDamage(5));
            room.updateMonsters();
            hero.textOut.println("All monsters are hit by a small fireblast, and take 5 damage.");
        });
        possibleSpellMap.put("lightning", hero -> {
            DungeonRoom room = hero.getLocation();
            Monster target = CollectionUtils.getRandom(room.getMonsters());
            if (target != null) {
            	hero.textOut.println(target.getName() + " took 10 lightning damage.");
                target.takeDamage(10);
                room.updateMonsters();
            } else {
            	hero.textOut.println("There were no monsters to use lightning on. Spell wasted.");
            }
        });
        possibleSpellMap.put("ice", hero -> {
            DungeonRoom room = hero.getLocation();
            Monster target = CollectionUtils.getRandom(room.getMonsters());
            if (target != null) {
            	hero.textOut.println(target.getName() + " took 8 cold damage and is disabled 1 round.");
                target.takeDamage(8);
                target.disable(1);
                room.updateMonsters();
            } else {
            	hero.textOut.println("No targets for ice spell. It was wasted.");
            }
        });
        possibleSpellMap.put("light", hero -> {
        	hero.textOut.debug("This spell doesn't really do what it should yet.");
        	hero.textOut.println("The room brightens up.");
            hero.getLocation().setLighting(TORCH_LIGHT);
        });
        possibleSpellMap.put("aegis", hero -> {
            hero.defenseMod = 5;
            hero.textOut.debug("Aegis lasts forever.");
            hero.textOut.println("A magic shield surrounds you.");
        });
        possibleSpellMap.put("push", hero -> {
            List<Monster> monsters = hero.getLocation().getMonsters();
            monsters.forEach(monster -> {
                monster.takeDamage(2);
                monster.disable(1);
            });
            hero.textOut.println("All monsters knocked down and damaged.");
        });
        possibleSpellMap.put("weaken", hero ->  {
            hero.getLocation().getMonsters()
                    .forEach(e -> e.setMight(e.getMight() - 1));
            hero.textOut.println("All enemies weakened.");
        });
    }
    
    public static void reagentSpells () {
    	//TODO: use
    	possibleSpellMap.put("flight", hero -> {
    		String reagentName = "Swan Feather";
    		if (hero.getBackpack().contains(reagentName) && hero.spellbook.contains("air")) {
    			hero.getBackpack().remove(reagentName);
    			hero.textOut.println("You're flying!");
    		} else if (!hero.spellbook.contains("air")){
    			hero.textOut.println("You can't fly without air magic.");
    		} else {
    			hero.textOut.println("You're missing the reagent (" + reagentName + ").");
    		}
    	});
    	
    	possibleSpellMap.put("dig", hero -> {
    		String reagentName = "Monster Claw";
    		if (hero.getBackpack().contains(reagentName) && hero.spellbook.contains("earth")) {
    			hero.getBackpack().remove(reagentName);
    			hero.textOut.println("You're digging!");
    		} else if (!hero.spellbook.contains("earth")){
    			hero.textOut.println("You can't dig without earth magic.");
    		} else {
    			hero.textOut.println("You're missing the reagent (" + reagentName + ").");
    		}
    	});
    	
    	possibleSpellMap.put("fireshield", hero -> {
    		String reagentName = "Copper Shield";
    		if (hero.getBackpack().contains(reagentName) && hero.spellbook.contains("fire")) {
    			hero.getBackpack().remove(reagentName);
    			hero.textOut.println("You're protected from heat!");
    		} else if (!hero.spellbook.contains("air")){
    			hero.textOut.println("You can't protect yourself from heat without fire magic.");
    		} else {
    			hero.textOut.println("You're missing the reagent (" + reagentName + ").");
    		}
    	});
    }

    public void rescuePrince () {
        throw new VictoryException("Rescued the handsome Prince Charming.");
    }

    public void removeItem (String itemName) {
        backpack.stream()
                .filter(e -> e.getName().equals(itemName))
                .findAny().ifPresent(item -> backpack.remove(item));
    }
    
    public void equip (EquipableItem item) {
    	unequip(item.getSlot());
    	
    	mightMod += item.getMightMod();
    	magicMod += item.getMagicMod();
    	sneakMod += item.getSneakMod();
    	defenseMod += item.getDefenseMod();
    	
    	if (item.getOnEquip() != null) {
    		location.doAction(item.getOnEquip());
    	}
    	backpack.remove(item);
    }
    
    public void unequip (EquipSlot slot) {
    	if (equippedItems.get(slot) != null) {
    		EquipableItem item = equippedItems.get(slot);
    		mightMod -= item.getMightMod();
    		magicMod -= item.getMagicMod();
    		sneakMod -= item.getSneakMod();
    		defenseMod -= item.getDefenseMod();
    		
    		if (item.getOnUnequip() != null) {
    			location.doAction(item.getOnUnequip());
    		}
    		backpack.add(equippedItems.remove(slot));
    	}
    }

    public void restoreHealth (int healthAmount) {
    	if (health < maxHealth) {
    		health += healthAmount;
    		if (health > maxHealth) {
                health = maxHealth;
            } 
    		textOut.println("Restored up to " + healthAmount + " health. (" + health + "/" + maxHealth + ")");
    	} else {
        	textOut.debug("Health was already at max.");
        }       
    }

    public void printStats () {
        textOut.println("Hero: " + name + "\nHealth: " + health + "/" + maxHealth + "  (Might, Magic, Defense) (" +
                might + ", " + magic + ", " + defense + ") Level: " + level + ", Exp: " + exp);
    }

    private void proceed (Direction direction) {
        List<Obstacle> obstacles = location.getObstacles().stream()
                .filter(obs -> !obs.isCleared())
                .filter(obs -> {
                    List<Direction> blockedDirections = obs.getBlockedDirections();
                    if (blockedDirections == null || blockedDirections.isEmpty()) {
                    	return false;
                    }
                    if (blockedDirections.get(0) == Direction.ALL) {
                        return true;
                    }
                    if (blockedDirections.contains(direction)) {
                        return true;
                    }
                    return false;
                })
                .collect(Collectors.toList());
        if (obstacles.size() > 0) {
            textOut.println("Travel is blocked in that direction (" + direction + ")");
        } else {
            Map<Direction, LeavingRoomAction> map = location.getOnHeroLeave();
            boolean proceed = true;
            if (map != null && map.get(direction) != null) {
                LeavingRoomAction lra = map.get(direction);
                String action = lra.getAction();
                location.doAction(action);
                proceed = lra.isStops();
                if (lra.isDoOnce()) {
                    map.remove(direction);
                }
            }
            if (proceed) {
                DungeonRoom nextRoom = location.getConnectedRooms().get(direction);
                if (nextRoom == null) {
                    textOut.println("Cannot go that way (no connected room).");
                    return;
                }
                location.removeHero();
                setLocation(nextRoom);
            }
        }
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
        damage -= (defense / 5) * 2;
        if (damage <= 0) {
            textOut.println("Damage completely mitigated.");
        } else {
            health -= damage;
            textOut.println("You took " + damage + " damage.");
            if (health <= 0) {
                health = 0;
                //TODO : fix
                throw new AssertionError("Died from damage. Or perhaps dafighter. Har har.");
            }
        }
    }

    public void takeNonMitigatedDamage (int damage) {
        health -= damage;
        textOut.println("You took " + damage + " damage.");
        if (health <= 0) {
        	//TODO : fix
            throw new AssertionError("Died from non-combat damage.");
        }
    }
    
    

    @Override
	public String toString() {
		return "Hero [name=" + name + ", health=" + health + ", maxHealth=" + maxHealth + ", might=" + might
				+ ", magic=" + magic + ", sneak=" + sneak + ", defense=" + defense + ", maxSpellsPerDay="
				+ maxSpellsPerDay + ", level=" + level + ", exp=" + exp + ", backpack=" + backpack + "]";
	}

	public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    @JsonIgnore
    public boolean isSneaking() {
        return isSneaking;
    }

    //TODO: investigate. This could cause problems in serialization
    public int getMight() {
        return might + mightMod;
    }

    public int getMagic() {
        return magic + magicMod;
    }

    public int getSneak() {
        return sneak + sneakMod;
    }
    
    public int getDefense() {
        return defense + defenseMod;
    }

    public void setDefense(int defense) {
        this.defense = defense;
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

    public DungeonRoom getPreviousLocation() {
        return previousLocation;
    }

    public void setPreviousLocation(DungeonRoom previousLocation) {
        this.previousLocation = previousLocation;
    }
    
    public void setMightMod (int mightMod) {
    	this.mightMod = mightMod;
    }
    
    public void setDefenseMod (int defenseMod) {
    	this.defenseMod = defenseMod;
    }
    
    public void setSneakMod (int sneakMod) {
    	this.sneakMod = sneakMod;
    }
    
    public void setMagicMod (int magicMod) {
    	this.magicMod = magicMod;
    }
    
    public List<String> getSpellbook () {
    	return spellbook;
    }
    
    /**
     * Alert - this method does more than just a regular setter.
     * It also attempts to rebuild a Map<String, SpellAction>
     * @param spellbook
     */
    public void setSpellbook (List<String> spellbook) {
    	this.spellbook = spellbook;
    	//TODO - verify if this works
    	//Goal is to rebuild the actual map of spells during deserialization
    	spellMap = new HashMap<>();
    	for(String spell : spellbook) {
    		spellMap.put(spell, possibleSpellMap.get(spell));
    	}
    }
    
    public List<String> getClearedDungeons () {
    	return clearedDungeons;
    }
    
    public void setClearedDungeons (List<String> clearedDungeons) {
    	this.clearedDungeons = clearedDungeons;
    }
    
    public void addClearedDungeon (String clearedDungeonName) {
    	if (clearedDungeons == null) {
    		textOut.debug("clearedDungeons was null for some reason...");
    		clearedDungeons = new ArrayList<>();
    	}
    	clearedDungeons.add(clearedDungeonName);
    }
    
    public boolean addSpell (String spell) {
    	//If it's a legit spell that we don't already have
    	if (possibleSpellMap.containsKey(spell) && !spellbook.contains(spell)) {
    		spellbook.add(spell);
    		spellMap.put(spell, possibleSpellMap.get(spell));
    		return true;
    	}
    	return false;
    }
    
    public int getNumSpellsAvailable () {
    	return numSpellsAvailable;
    }
    
    public void setNumSpellsAvailable (int numSpellsAvailable) {
    	this.numSpellsAvailable = numSpellsAvailable;
    }

	public Map<EquipSlot, EquipableItem> getEquippedItems() {
		return equippedItems;
	}

	public void setEquippedItems(Map<EquipSlot, EquipableItem> equippedItems) {
		this.equippedItems = equippedItems;
	}
	
	public int getBlock () {
		return block;
	}
    
	public void setTextOut (TextInterface textOut) {
		this.textOut = textOut;
	}
	
	//TODO: seems like there's some commonality between this and monster - shared base class?
	public boolean isDisabled () {
		return disabledForRounds > 0;
	}
	
	public void disable (int numRounds) {
		disabledForRounds += numRounds;
	}
	
	public void unDisable () {
		disabledForRounds = 0;
	}
	
	public void nextRound () {
		if (disabledForRounds > 0) {
			disabledForRounds--;
		}
		if (block > 0) {
			block--;
		}
	}
	
	public Integer getIntField (String fieldName) {
		Class <? extends Hero> heroClass = getClass();
		try {
			String methodName = "get" + StringUtils.capitalize(fieldName);
			Method method = heroClass.getDeclaredMethod(methodName);
			Object response = method.invoke(this);
			return (Integer) response;
		} catch (Exception ex) {
			return null;
		}
	}
}