package paul.TextQuest.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import paul.TextQuest.TextInterface;
import paul.TextQuest.entities.obstacles.Obstacle;
import paul.TextQuest.entities.obstacles.SmashableObstacle;
import paul.TextQuest.enums.Direction;
import paul.TextQuest.enums.EquipSlot;
import paul.TextQuest.enums.SpeakingVolume;
import paul.TextQuest.gameplan.LevelUpPlan;
import paul.TextQuest.interfaces.*;
import paul.TextQuest.utils.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Paul Dennis on 8/8/2017.
 */

public class Hero extends CombatEntity implements Serializable {

    private int maxHealth;

    private int magic;
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
    
    private List<String> levelUpTodo;
    private Map<EquipSlot, EquippableItem> equippedItems;
    
    private SkillMap skillMap;
    private transient SkillMap skillMods;

    private transient int mightMod;
    private transient int magicMod;
    private transient int defenseMod;
    private transient int numSpellsAvailable;
    
    private transient int block;

    private transient DungeonRoom location;
    private transient DungeonRoom previousLocation;
    private transient boolean isSneaking;

    private transient Map<String, VoidAction> heroVoidActions;
    private transient Map<String, ParamAction> heroParamActions;
    private transient Map<String, VoidAction> views;

    private transient List<String> buffs;
    private transient List<String> debuffs;

    private transient Random random;
    private transient TextInterface textOut;

    public static final double TORCH_LIGHT = 1.0;
    public static final int POTION_VALUE = 9;
    
    public static final String SAVE_PATH = "save_data/";

    private static LevelUpPlan levelUpPlan;
    public static final String LEVEL_UP_PLAN_LOCATION = "content_files/game/leveling/default_plan.json";


    public Hero () {
        random = new Random();
        backpack = new Backpack();
        spellbook = new ArrayList<>();
        clearedDungeons = new ArrayList<>();
        equippedItems = new HashMap<>();
        skillMap = new SkillMap();
        skillMods = new SkillMap();
        
        buffs = new ArrayList<>();
        debuffs = new ArrayList<>();
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
        skillMap = startingInfo.skillMap;
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
        skillMap.put("sneak", 0); //Kind of meaningless since it returns 0 by default...

        level = 0;
        exp = 0;
        numSpellsAvailable = 5;
        maxSpellsPerDay = 1;
        backpack.add(new BackpackItem("Torch"));
        //backpack.add(boots);
        backpack.add(new BackpackItem("Bow"));
    }
    
    //Defines what we can do at each level (i.e. what new skills, stat increases, etc are possible)
    private static void initLevelUpPlan () {
    	try {
    		levelUpPlan = LevelUpPlan.buildFromFile(LEVEL_UP_PLAN_LOCATION);
    	} catch (IOException ex) {
    		throw new AssertionError(ex);
    	}
    }
    
    private transient Map<Integer, Boolean> messagePrintedForLevel;
    public void addExp (int expToAdd) {
        if (expToAdd < 0) {
            throw new AssertionError();
        }
        this.exp += expToAdd;
        textOut.println("Gained " + expToAdd + " exp.");
        if (levelUpPlan == null) {
        	initLevelUpPlan();
        }
        if (messagePrintedForLevel == null) {
        	messagePrintedForLevel = new HashMap<>();
        }
        if (levelUpPlan.getExpAmounts().get(level) <= exp) {
        	if (messagePrintedForLevel.get(level) == false) {
	            textOut.println("***Ding! Level up.");
	            textOut.println("After the dungeon is complete you'll be able to level up.");
	            messagePrintedForLevel.put(level, true);
        	}
        }
    }
    
    public void prepareLevelUpTodos () {
    	while (levelUpPlan.getExpAmounts().get(level) <= exp) {
    		levelUpTodo.addAll(levelUpPlan.getLevelUpActions().get(level));
    		level++;
    	}
    }

    
    public static void saveHeroToFile (String username, Hero hero) {
    	String fileName = SAVE_PATH + username + "/" + hero.getName() + ".json";
    	System.err.println("!Attempting to save hero to " + fileName);
    	new File(SAVE_PATH + username).mkdirs();
    	try (FileWriter fileWriter = new FileWriter(new File(fileName))){
    		fileWriter.write(StringUtils.serializeIgnoringTransient(hero));
    	} catch (IOException ex) {
    		ex.printStackTrace();
    	}
    }
    
    public static Hero loadHeroFromFile (String fileName) {
    	try {
    		return StringUtils.buildObjectFromFile(fileName, Hero.class);
    	} catch (IOException ex) {
    		System.err.println("!Not found, returing null.");
    		return null;
    	}
    }
    
    public static Hero loadHeroFromFile (String username, String heroName) {
    	String fileName = SAVE_PATH + username + "/" + heroName + ".json";
    	return loadHeroFromFile(fileName);
    }
    
    public static List<String> getHeroListForUser (String username) {
    	File folder = new File(SAVE_PATH + username);
    	return Arrays.stream(folder.listFiles())
    			.map(file -> file.getName().split("\\.")[0]) //Files are named as heroName.json. We just want heroName
    			.collect(Collectors.toList());
    }

    private void initMaps () {
        heroVoidActions = new HashMap<>();
        heroParamActions = new HashMap<>();
        views = new HashMap<>();
        initActionMap();
        initViews();
    }

    public void takeAction (String action) {
    	String onHeroAction = location.getOnHeroAction().get(action);
    	if (onHeroAction != null) {
    		location.doAction(onHeroAction);
    		if (onHeroAction.contains("!STOPS")) {
        		onHeroAction.replaceAll("!STOPS", "");
        		textOut.debug("This trigger stops the action.");
        		return;
        	}
    	}
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

    public void takeAction (String action, String param) {
        ParamAction paramAction = heroParamActions.get(action);
        if (paramAction != null) {
            paramAction.doAction(location, param);
        } else {
            textOut.debug("Action not in map.");
            throw new AssertionError();
        }
    }

    private void initViews () {
        views.put("status", room -> printStats());
        views.put("map", room -> textOut.println("Map not yet implemented."));
        views.put("backpack", room -> {
        	textOut.println("You have the following items in your pack: " + StringUtils.prettyPrintList(backpack.getItems()));
        });
        views.put("spellbook", room -> {
            
            textOut.println("Spells: (Available/Max): (" + numSpellsAvailable + "/" + maxSpellsPerDay + ")");
            if (spellbook.size() == 0) {
                textOut.println("You don't know any areas of magic yet.");
            } else {
                textOut.println("Known Areas of Magic:");
                textOut.println(StringUtils.prettyPrintList(spellbook.stream()
                	.map(StringUtils::capitalize)
                	.collect(Collectors.toList())));
            }
        });
        views.put("equipment", room -> {
        	if (equippedItems.keySet().size() == 0) {
        		textOut.println("You do not have any items equipped.");
        		return;
        	}
        	textOut.println("You have the following items equipped:");
        	equippedItems.keySet().forEach(slot -> {
        		EquippableItem item = equippedItems.get(slot);
        		textOut.println(slot + ": " + item.getName());
        	});
        	
        });
        views.put("skills", room -> {
        	skillMap.keySet().forEach(skill -> {
        		List<String> skillMessages = new ArrayList<>();
        		int skillAmt = skillMap.get(skill);
        		int skillModAmt = skillMods.get(skill);
        		if (skillAmt != 0 || skillModAmt != 0) {
        			String out = StringUtils.capitalize(skill) + ": " + skillAmt;
        			if (skillModAmt != 0) {
        				out += StringUtils.appendModifierWithSignInParens(skillModAmt);
        			}
        			skillMessages.add(out);
        		}
        		if (skillMessages.size() > 0) {
        			textOut.println("You have the following skills:");
        			skillMessages.forEach(textOut::println);
        		} else {
        			textOut.println("You do not have any skills.");
        		}
        	});
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
        	int sneak = skillMap.get("sneak");
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

        heroVoidActions.put("fight", room -> room.doAction("startFight"));

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
            	Spellbook spellbook = room.getDungeon().getDungeonRunner().getSpellbook();
            	Spell spell = spellbook.getSpell(param);
            	if (spell != null) {
            		castSpell(spell);
            	} else {
            		textOut.println("Could not find spell " + param);
            	}
            }
        });

        heroParamActions.put("use", (room, param) -> {
        	
            if (getBackpack().contains(param)) {
            	BackpackItem item = getBackpack().getItem(param);
                String onUse = item.getOnUse();
                if (onUse == null) {
                	textOut.println("You can't use that item directly.");
                } else {
                	if (onUse.contains(BackpackItem.CONSUMES)) {
                		onUse = onUse.replaceAll(BackpackItem.CONSUMES, "").trim();
                		getBackpack().remove(item);
                	}
                	room.doAction(onUse);
                }
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
        	EquippableItem equippableItem = null;
        	for (BackpackItem item : items) {
        		if (item.getName().toLowerCase().equals(param)) {
        			if (item.getClass() == EquippableItem.class) {
        				textOut.println("OK you are equipping " + item.getName());
        				equippableItem = (EquippableItem) item;
        			} else {
        				textOut.println("That item isn't equippable, sorry.");
        			}
        			found = true;
        		}
        	}
        	if (equippableItem != null) {
        		equip(equippableItem);
        	}
        	if (!found) {
        		textOut.println("You don't have a " + param + " to equip.");
        	}
        });
        
        heroParamActions.put("unequip", (room, param) -> {
        	//We'll attempt to find by item name first, then by slot
        	boolean found = false;
        	EquipSlot slotToBeRemoved = null;
        	for (EquipSlot slot : equippedItems.keySet()) {
        		EquippableItem item = equippedItems.get(slot);
        		if (item.getName().toLowerCase().equals(param)) {
        			slotToBeRemoved = slot;
        			found = true;
        			textOut.println("Unequipped " + item.getName());
        		} else if (slot.toString().toLowerCase().equals(param)) {
        			slotToBeRemoved = slot;
        			found = true;
        			textOut.println("Unequipped " + item.getName());
        		}
        	}
        	if (!found) {
        		textOut.println("You don't have a " + param + " equipped.");
        	} else {
        		unequip(slotToBeRemoved);
        	}
        });
        
        heroParamActions.put("viewspells", (room, param) -> {
        	Spellbook possibleSpells = room.getDungeon().getDungeonRunner().getSpellbook();
        	List<Spell> spellsOfType = possibleSpells.getSpellsOfType(param);
        	List<String> spellTypes = possibleSpells.getSpellTypes();
        	
        	if (param.equals("")) {
        		if (spellsOfType.size() != 0) {
        			textOut.println("The following spells do not require any special knowledge to cast:");
        			spellsOfType.forEach(textOut::println);
        		} else {
        			textOut.println("All spells require special knowledge of an area of magic in order to cast them");
        		}
        		return;
        	}
        	
        	if (!spellTypes.contains(param)) {
        		textOut.println("That type of magic does not exist (" + param + ")");
        		return;
        	}
        	
        	textOut.println("The following spells use " + param + " magic:");
        	spellsOfType.forEach(textOut::println);
        });
        
        heroParamActions.put("detail", (room, param) -> {
        	//Look for an Item
        	BackpackItem item = backpack.getItem(param);
        	if (item != null) {
        		textOut.println(item.toDetailedString());
        	}
        	//Look for a Spell
        	Spell spell = room.getDungeon().getDungeonRunner().getSpellbook().getSpell(param);
        	if (spell != null) {
        		textOut.println(spell.toDetailedString());
        	}
        	if (item == null && spell == null) {
        		textOut.println("Unable to find a spell or item called " + param + " on which to provide detail.");
        	}
        });
        
    }
    
    public void castSpell (Spell spell) {
    	for (String prereq : spell.getPrereqs()) {
			if (prereq.contains(" ")) {
				String[] splits = prereq.split(" ");
				int requiredLevel = Integer.parseInt(splits[1]);
				String type = splits[0];
				if (spellbook.contains(type)) {
					if (requiredLevel > 1) {
						textOut.println("Your knowledge of " + StringUtils.capitalize(splits[0]) + " magic is not strong enough.");
						return;
					}
				} else {
					textOut.println("You don't know the neccessary type of magic (" + StringUtils.capitalize(prereq) + ")");
					return;
				}
			} else {
				if (!spellbook.contains(prereq)) {
					textOut.println("You don't know the neccessary type of magic (" + StringUtils.capitalize(prereq) + ")");
					return;
				}
			}
		}
		//Check required items
		for (String itemName : spell.getRequiredItems()) {
			if (!backpack.contains(itemName)) {
				textOut.println("You are missing a required item.");
				return;
			}
		}
		
		//Check and remove reagents
		for (String reagent : spell.getReagents()) {
			if (backpack.contains(reagent)) {
				backpack.remove(reagent);
			} else {
				textOut.println("You are missing a required reagent: " + reagent + ".");
				return;
			}
		}
		
		//Check status string
		String statusString = spell.getStatusString();
		if (statusString != null) {
			if (hasStatus(statusString)) {
				textOut.println("You are already affected by that spell.");
				return;
			} else {
				addStatus(statusString);
			}
		}
		
		//Do spell actions
		spell.getActions().forEach(location::doAction);
    }

    public void removeItem (String itemName) {
        backpack.stream()
                .filter(e -> e.getName().equals(itemName))
                .findAny().ifPresent(item -> backpack.remove(item));
    }
    
    public void equip (EquippableItem item) {
    	unequip(item.getSlot());
    	
    	mightMod += item.getMightMod();
    	magicMod += item.getMagicMod();
    	skillMods.put("sneak", skillMods.get("sneak") + item.getSneakMod());
    	defenseMod += item.getDefenseMod();
    	
    	if (item.getOnEquip() != null) {
    		location.doAction(item.getOnEquip());
    	}
    	equippedItems.put(item.getSlot(), item);
    	backpack.remove(item);
    }
    
    public void unequip (EquipSlot slot) {
    	if (equippedItems.get(slot) != null) {
    		EquippableItem item = equippedItems.get(slot);
    		mightMod -= item.getMightMod();
    		magicMod -= item.getMagicMod();
    		skillMods.put("sneak", skillMods.get("sneak") - item.getSneakMod());
    		defenseMod -= item.getDefenseMod();
    		
    		if (item.getOnUnequip() != null) {
    			location.doAction(item.getOnUnequip());
    		}
    		backpack.add(equippedItems.remove(slot));
    		equippedItems.remove(slot);
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
    	textOut.println("Hero: " + name + "\nHealth: " + health + "/" + maxHealth);
    	textOut.println("Level: " + level + ", Exp: " + exp);
        textOut.println("Might: " + might + StringUtils.appendModifierWithSignInParens(mightMod));
        textOut.println("Defense: " + defense + StringUtils.appendModifierWithSignInParens(defenseMod));
        textOut.println("Magic: " + magic + StringUtils.appendModifierWithSignInParens(magicMod));
        
        if (buffs.size() > 0) {
        	textOut.println("You are affected by the following positive statuses: " + StringUtils.prettyPrintList(buffs));
        }
        if (debuffs.size() > 0) {
        	textOut.println("You are affected by the following negative statuses: " + StringUtils.prettyPrintList(debuffs));
        }
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

    public int takeDamage (int damage) {
        damage -= (defense / 5) * 2;

        health -= damage;
        if (health <= 0) {
            health = 0;
            throw new DefeatExceptionMessage("Died from damage - or perhaps dafighter, har har");
        }
        return damage;
    }

    public void takeNonMitigatedDamage (int damage) {
        health -= damage;
        if (health <= 0) {
        	throw new DefeatExceptionMessage("Died from non-combat damage.");
        }
    }
    
    public void modStat (String statName, int amount) {
    	switch (statName.toLowerCase()) {
    	case "might":
    		mightMod += amount;
    		break;
    	case "defense":
    		defenseMod += amount;
    		break;
    	case "magic":
    		magicMod += amount;
    		break;
    	case "numSpellsAvailable":
    		numSpellsAvailable += amount;
    		if (numSpellsAvailable < 0) {
    			numSpellsAvailable = 0;
    		}
    		break;
		default:
			throw new AssertionError("Stat not recognized: " + statName);
    	}
    }

    
	@Override
	public String toString() {
		return "Hero [name=" + name + ", health=" + health + ", maxHealth=" + maxHealth + ", might=" + might
				+ ", magic=" + magic + ", defense=" + defense + ", maxSpellsPerDay=" + maxSpellsPerDay + ", level="
				+ level + ", exp=" + exp + ", backpack=" + backpack + ", spellbook=" + spellbook + ", clearedDungeons="
				+ clearedDungeons + ", skillMap=" + skillMap + ", equippedItems=" + equippedItems + "]";
	}

    public int getMaxHealth() {
        return maxHealth;
    }

    @JsonIgnore
    public boolean isSneaking() {
        return isSneaking;
    }

    @JsonIgnore
    public int getModdedMight() {
        return might + mightMod;
    }

    @JsonIgnore
    public int getModdedMagic() {
        return magic + magicMod;
    }

    @JsonIgnore
    public int getModdedDefense() {
        return defense + defenseMod;
    }
    
	public int getMagic() {
		return magic;
	}

	public int getSkill (String skillName) {
    	return skillMap.get(skillName) + skillMods.get(skillName);
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

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void setMagic(int magic) {
        this.magic = magic;
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
    
    public void setMagicMod (int magicMod) {
    	this.magicMod = magicMod;
    }
    
    public List<String> getSpellbook () {
    	return spellbook;
    }
    
    public void setSpellbook (List<String> spellbook) {
    	this.spellbook = spellbook;
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
    	if (!spellbook.contains(spell)) {
    		spellbook.add(spell);
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

	public Map<EquipSlot, EquippableItem> getEquippedItems() {
		return equippedItems;
	}

	public void setEquippedItems(Map<EquipSlot, EquippableItem> equippedItems) {
		this.equippedItems = equippedItems;
	}
	
	public int getBlock () {
		return block;
	}
    
	public void setTextOut (TextInterface textOut) {
		this.textOut = textOut;
	}
	
	public List<String> getLevelUpTodo() {
		return levelUpTodo;
	}

	public void setLevelUpTodo(List<String> levelUpTodo) {
		this.levelUpTodo = levelUpTodo;
	}

	@Override
	@JsonIgnore
	public boolean isDisabled () {
		return disabledForRounds > 0;
	}
	
	public void addStatus (String status) {
		if (status.startsWith("+")) {
			buffs.add(status.substring(1));
		} else if (status.startsWith("-")) {
			debuffs.add(status.substring(1));
		} else {
			throw new AssertionError("Status must start with + or - to indicate buff/debuff.");
		}
	}
	
	public void removeStatus (String toRemove) {
		buffs.removeIf(status -> status.equals(toRemove));
		debuffs.removeIf(status -> status.equals(toRemove));
	}
	
	public void removeAllBuffs () {
		buffs = new ArrayList<>();
	}
	
	public void removeAllDebuffs () {
		debuffs = new ArrayList<>();
	}
	
	public boolean hasItem (String itemName) {
		return backpack.contains(itemName);
	}
	
	public boolean hasStatus (String status) {
		return buffs.contains(status) || debuffs.contains(status);
	}
	
	public Integer getIntField (String fieldName) {
		try {
			String methodName = "get" + StringUtils.capitalize(fieldName);
			Method method = getClass().getDeclaredMethod(methodName);
			Object response = method.invoke(this);
			return (Integer) response;
		} catch (ReflectiveOperationException ex) {
			throw new AssertionError(ex);
		}
	}
	
	public boolean getHasField (String methodName, String arg) {
		try {
			Method method = getClass().getDeclaredMethod(methodName, String.class);
			return (boolean) method.invoke(this, arg);
		} catch (ReflectiveOperationException ex) {
			throw new AssertionError(ex);
		}
	}
	
	public static LevelUpPlan getLevelUpPlan () {
		if (levelUpPlan == null) {
        	initLevelUpPlan();
        }
		return levelUpPlan;
	}
}