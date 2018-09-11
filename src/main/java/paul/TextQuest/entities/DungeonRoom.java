package paul.TextQuest.entities;

import paul.TextQuest.EnteringRoomAction;
import paul.TextQuest.LeavingRoomAction;
import paul.TextQuest.entities.obstacles.Chasm;
import paul.TextQuest.entities.obstacles.Obstacle;
import paul.TextQuest.entities.obstacles.RiddleObstacle;
import paul.TextQuest.enums.Direction;
import paul.TextQuest.enums.LightingLevel;
import paul.TextQuest.enums.SpeakingVolume;
import paul.TextQuest.interfaces.MultiParamAction;
import paul.TextQuest.interfaces.ParamAction;
import paul.TextQuest.interfaces.VoidAction;
import paul.TextQuest.interfaces.SpeechListener;
import paul.TextQuest.parsing.InputType;
import paul.TextQuest.parsing.TextInterface;
import paul.TextQuest.parsing.UserInterfaceClass;
import paul.TextQuest.utils.StringUtils;
import paul.TextQuest.utils.VictoryException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Paul Dennis on 8/8/2017.
 */
public class DungeonRoom extends UserInterfaceClass {

    private String name;
    private String description;
    private int id;
    private String tutorial;

    private double lighting;
    private List<Monster> monsters;
    private List<BackpackItem> items;
    //The "Key" for hidden items is a word location in the room. By convention the word should appear in the description
    //Of the room. For example if the description references a "fountain" than an item could be hidden by "fountain"
    private Map<String, List<BackpackItem>> hiddenItems;
    private List<Obstacle> obstacles;
    private Chest chest;
    private String bossFightFileLocation;
    private List<Feature> features;

    private Map<String, String> specialRoomActions;
    private Map<String, String> onLightingChange;
    private Map<Direction, LeavingRoomAction> onHeroLeave;
    private Map<String, String> onItemUse;
    
    private Map<String, String> onSpellCast;
    private Map<String, String> onSearch;
    private Map<String, String> onHeroAction;
    
    private String onCombatStart;
    private String onCombatEnd;
    
    private EnteringRoomAction onHeroEnter;

    //Temporary variables for JSONification
    private Map<Direction, Integer> connectedRoomIds;

    private transient List<SpeechListener> speechListeners;
    private transient TextInterface textOut;

    private transient Dungeon dungeon;
    private transient Map<Direction, DungeonRoom> connectedRooms;
    private transient Hero hero;
    
    //Used to find trigger maps from strings
    private transient Map<String, Map<String, String>> metaMap;
    
    private static Map<String, VoidAction> voidActionMap;
    private static Map<String, ParamAction> paramActionMap;
    private static Map<String, MultiParamAction> multiParamActionMap;

    public DungeonRoom () {
        hiddenItems = new HashMap<>();
        monsters = new ArrayList<>();
        items = new ArrayList<>();
        connectedRoomIds = new HashMap<>();
        connectedRooms = new HashMap<>();
        obstacles = new ArrayList<>();
        hiddenItems = new HashMap<>();
        speechListeners = new ArrayList<>();
        specialRoomActions = new HashMap<>();
        children = new ArrayList<>();
        features = new ArrayList<>();
        initUniversalSpeechListeners();
        
        onHeroAction = new HashMap<>();
        
        lighting = 1.0; //By default rooms should be well-lit.
    }
    
    public DungeonRoom (String name, String description) {
    	this();
        this.name = name;
        this.description = description;
    }
    
    private void initMetaMap () {
    	metaMap = new HashMap<>();
        metaMap.put("specialRoomActions", specialRoomActions);
        metaMap.put("onLightingChange", onLightingChange);
        metaMap.put("onItemUse", onItemUse);
        metaMap.put("onSpellCast", onSpellCast);
        metaMap.put("onSearch", onSearch);
        metaMap.put("onHeroAction", onHeroAction);
    }

    private static void initActionMaps () {
        voidActionMap = new HashMap<>();
        paramActionMap = new HashMap<>();
        multiParamActionMap = new HashMap<>();

        //Void Actions\\
        voidActionMap.put("douse", room -> room.setLighting(0.0));
        voidActionMap.put("light", room -> room.setLighting(1.0));
        voidActionMap.put("makeMinibossWeak", room -> {
            room.getMonsters().stream()
                .filter(Monster::isMiniboss)
                .forEach(miniboss -> {
                    miniboss.setMight(2);
                    miniboss.setDefense(1);
                    miniboss.disable(1);
                    room.getHero().getTextOut().println("Made " + miniboss.getName() + " weak.");
                });
        });
        voidActionMap.put("makeMinibossStrong", room -> {
            room.getMonsters().stream()
                .filter(Monster::isMiniboss)
                .forEach(miniboss -> {
                    miniboss.setMight(5);
                    miniboss.setDefense(12);
                    room.getHero().getTextOut().println("Made " + miniboss.getName() + " strong.");
                });
        });
        voidActionMap.put("startFight", room -> room.getHero().takeAction("fight"));
        voidActionMap.put("victory", room -> {
            throw new VictoryException("You win!");
        });
        voidActionMap.put("crackFloor", room -> {
            room.textOut.println("CRAAACK!!!! The floor of the room splits and a giant chasm appears.");
            Chasm chasm = new Chasm();
            chasm.addBlockedDirection(Direction.ALL);
            room.textOut.debug(chasm.toLongString());
            room.addObstacle(chasm);
            room.hero.setPreviousLocation(null); //Prevent retreating
        });
        
        voidActionMap.put("removeChest", room -> {
        	room.chest = null;
        	room.textOut.println("The chest vanished.");
        });
        
        voidActionMap.put("setDungeonCleared", room -> {
        	room.getDungeon().setCleared(true);
        });
        
        
        //Param Actions\\
        paramActionMap.put("createMonster", (room, param) -> {
            if (param.equals("Skeleton")) {
                room.addMonster(new Monster(2, 1, "Skeleton"));
            }
            Monster monster = room.getDungeon().getMonsterLibrary().get(param);
            if (monster != null) {
            	room.addMonster(new Monster(monster));
            } else {
            	room.textOut.debug("Cannot create monster - can't be found in library. Name was " + param);
            }
        });
        paramActionMap.put("explode", (room, param) -> {
            int damageAmt = Integer.parseInt(param);
            room.textOut.println("BOOM!! Explosions!");
            room.textOut.debug("@Deprecated");
            room.getHero().takeNonMitigatedDamage(damageAmt);
        });
        paramActionMap.put("takeDamage", (room, param) -> {
        	int damageAmt = Integer.parseInt(param);
        	room.getHero().takeNonMitigatedDamage(damageAmt);
        });
        multiParamActionMap.put("takeTypedDamage", (room, args) -> {
        	int damageAmt = Integer.parseInt(args[1]);
        	room.getHero().takeNonMitigatedDamage(damageAmt);
        	room.textOut.println("You took " + damageAmt + " " + args[2] + " damage.");
        });
        multiParamActionMap.put("takeSourcedDamage", (room, args) -> {
        	int damageAmt = Integer.parseInt(args[1]);
        	room.getHero().takeNonMitigatedDamage(damageAmt);
        	room.textOut.println("You took " + damageAmt + " damage from " + args[2] + ".");
        });
        multiParamActionMap.put("takeTypedSourcedTypedDamage", (room, args) -> {
        	int damageAmt = Integer.parseInt(args[1]);
        	room.getHero().takeNonMitigatedDamage(damageAmt);
        	room.textOut.println("You took " + damageAmt + " " + args[2] + " damage from " + args[3] + ".");
        });
        
        paramActionMap.put("giveExp", (room, param) -> room.getHero().addExp(Integer.parseInt(param)));
        paramActionMap.put("heal", (room, param) -> {
            int amt = Integer.parseInt(param);
            room.getHero().restoreHealth(amt);
        });
        paramActionMap.put("print", (room, param) -> room.textOut.println(param));
        paramActionMap.put("debug", (room, param) -> room.textOut.debug(param));
        paramActionMap.put("tutorial", (room, param) -> room.textOut.tutorial(param));
        paramActionMap.put("bump", (room, param) -> room.textOut.println("Ouch! You bumped into something."));
        
        //New 8/28
        
        paramActionMap.put("teachSpell", (room, param) -> {
        	boolean success = room.getHero().addSpell(param);
        	if (success) {
        		room.textOut.println("Hero learned a new spell: " + param);
        	} else {
        		room.textOut.debug("Attempted to teach spell: " + param);
        	}
        });
        
        paramActionMap.put("changeRoomDescription", (room, param) -> {
        	room.setDescription(param);
        	room.textOut.println(param);
        	room.textOut.debug("Room description changed to " + param);
        	room.textOut.debug("current doesn't matter since description only happens once");
        });
        
        paramActionMap.put("teleportHero", (room, param) -> {
        	DungeonRoom otherRoom = room.getDungeon().getRoom(param);
        	if (otherRoom != null) {
        		room.textOut.debug("Attempting to move hero to " + otherRoom.name);
        		Hero hero = room.getHero();
        		hero.setLocation(otherRoom);
        		hero.setPreviousLocation(null);
        		otherRoom.setHero(hero);
        		room.removeHero();
        	} else {
        		room.textOut.debug("Could not find room: " + param);
        	}
        });
        
        paramActionMap.put("createItem", (room, param) -> {
        	Map<String, BackpackItem> itemLibrary = room.getDungeon().getItemLibrary();
        	if (itemLibrary.containsKey(param)) {
        		BackpackItem fromLib = itemLibrary.get(param);
        		room.addItem(fromLib);
        	} else { //If it can't be found in the library just create a basic item
        		BackpackItem basic = new BackpackItem(param);
        		room.addItem(basic);
        	}
        	room.textOut.println("An object appeared in the room...");
        });
        
        paramActionMap.put("swapChest", (room, param) -> {
        	int id = Integer.parseInt(param);
        	DungeonRoom other = room.getDungeon().getRoom(id);
        	if (other.getChest() == null && room.getChest() == null) {
        		room.textOut.debug("Both chests are null");
        		return;
        	}
        	Chest otherChest = other.getChest();
        	other.setChest(room.getChest());
        	room.setChest(otherChest);
        	room.textOut.println("A pop of displaced air.");
        });
        
        paramActionMap.put("removeItem", (room, param) -> {
        	List<BackpackItem> items = room.getItems();
        	BackpackItem toBeRemoved = null;
        	for (BackpackItem item : items) {
        		if (item.getName().toLowerCase().equals(param.toLowerCase())) {
        			toBeRemoved = item;
        			break;
        		}
        	}
        	items.remove(toBeRemoved);
        	room.textOut.debug("Attempted to remove " + param + " from room.");
        });
        
        paramActionMap.put("removeItemFromHero", (room, param) -> {
        	room.getHero().removeItem(param);
        	room.textOut.debug("Attempted to remove " + param + " from hero.");
        });
        
        paramActionMap.put("changeRoomName", (room, param) -> {
        	String oldName = room.getName();
        	room.setName(param);
        	room.getDungeon().updateRoomName(room, oldName);
        });
        
        paramActionMap.put("removePassage", (room, param) -> {
        	Direction direction = Direction.getDirectionFromString(param);
        	Map<Direction, DungeonRoom> connectedRooms = room.getConnectedRooms();
        	connectedRooms.remove(direction);
        	room.setConnectedRooms(connectedRooms);
        	room.textOut.debug("A passage to the " + param + " closes.");
        });
        
        paramActionMap.put("clearObstacle", (room, param) -> {
        	room.getObstacles().stream()
        		.filter(obstacle -> obstacle.getName().equals(param))
        		.forEach(obstacle -> {
        			obstacle.setCleared(true);
        		});
        });
        
        paramActionMap.put("addFeature", (room, param) -> {
        	room.features.add(new Feature(param));
        	room.textOut.debug("Added " + param + " feature to room.");
        });
        
        paramActionMap.put("removeFeature", (room, param) -> {
        	List<Feature> toBeRemoved = room.features.stream()
        		.filter(feature -> feature.getName().equals(param))
        		.collect(Collectors.toList());
        	
        	for (Feature feature : toBeRemoved) {
        		room.features.remove(feature);
        		room.textOut.debug("Removed " + feature);
        	}
        });
        
        //MultiParam Actions\\
        multiParamActionMap.put("modStat", (room, args) -> {
        	/*
        	room.textOut.debug("Not yet implemented");
        	String statName = args[1];
        	String action = args[2];
        	
        	if (action.startsWith("+")) { //Add the amount
        		
        	} else if (action.startsWith("-")) { //Subtract the amount
        		
        	} else if (action.startsWith(":")) { //Set to this amount
        		
        	} else if (action.startsWith("?")) { //Randomize from 50% to 150%
        		
        	}*/
        	//TODO: fix/impl
        	room.textOut.debug("Gave hero 2 spells to work with");
        	room.getHero().setNumSpellsAvailable(2);
        });
        
        multiParamActionMap.put("createHiddenItem", (room, args) -> {
        	String itemName = args[1];
        	String location = args[2];
        	
        	BackpackItem item;
        	Map<String, BackpackItem> itemLibrary = room.getDungeon().getItemLibrary();
        	if (itemLibrary.containsKey(itemName)) {
        		item = itemLibrary.get(itemName);
        		
        	} else { //If it can't be found in the library just create a basic item
        		item = new BackpackItem(itemName);
        	}
        	
        	room.addHiddenItem(location, item);
        	room.textOut.debug("Added hidden item " + itemName + " at " + location + ".");
        });
        multiParamActionMap.put("castSpell", (room, args) -> {
        	room.textOut.println("Not yet implemented. Args: " + args);
        });
        
        multiParamActionMap.put("addTrigger", (room, args) -> {
        	if (room.metaMap == null) {
        		room.initMetaMap();
        	}
        	String triggerGroup = args[1];
        	String triggerWord = args[2];
        	String eventWord = "";
        	for (int i = 3; i < args.length; i++) {
        		eventWord += args[i];
        		if (i + 1 < args.length) {
        			eventWord += " ";
        		}
        	}
        	
        	Map<String, String> triggerMap = room.metaMap.get(triggerGroup);
        	
        	triggerMap.put(triggerWord, eventWord);
        	
        	room.textOut.debug("Added trigger " + triggerWord + " to " + triggerGroup + " with event: " + eventWord);
        });
        
        multiParamActionMap.put("removeTrigger", (room, args) -> {
        	if (room.metaMap == null) {
        		room.initMetaMap();
        	}
        	String triggerGroup = args[1];
        	String triggerWord = args[2];
        	
        	Map<String, String> triggerMap = room.metaMap.get(triggerGroup);
        	
        	triggerMap.remove(triggerWord);
        	
        	room.textOut.debug("Removed trigger " + triggerWord + " from " + triggerGroup);
        });
        
        multiParamActionMap.put("modMonsterStats", (room, args) -> {
        	throw new AssertionError("not yet implemented");
        });
        
        multiParamActionMap.put("createPassage", (room, args) -> {
        	Direction direction = Direction.getDirectionFromString(args[1]);
        	int id = Integer.parseInt(args[2]);
        	
        	Map<Direction, DungeonRoom> connectedRooms = room.getConnectedRooms();
        	if (connectedRooms.containsKey(direction)) {
        		room.textOut.debug("There was already a connection in the direction " + direction + ".");
        		room.textOut.debug("It was to " + connectedRooms.get(direction).getName() + ".");
        	}
        	DungeonRoom otherRoom = room.getDungeon().getRoom(id);
        	connectedRooms.put(direction, otherRoom);
        	room.setConnectedRooms(connectedRooms);
        	room.textOut.debug("A passage opens to the " + direction + ".");
        });
        
        multiParamActionMap.put("setDungeonVariable", (room, args) -> {
        	Dungeon dungeon = room.getDungeon();
        	dungeon.setDungeonVar(args[1], args[2]);
        	room.textOut.debug("Vars: Set " + args[1] + " to " + args[2]);
        	if (dungeon.getOnVariableSet().get(args[1]) != null) {
        		room.doAction(dungeon.getOnVariableSet().get(args[1]));
        	}
        });
        
        multiParamActionMap.put("setDungeonValue", (room, args) -> {
        	Dungeon dungeon = room.getDungeon();
        	dungeon.setDungeonVar(args[1], args[2]);
        	room.textOut.debug("Vars: Set " + args[1] + " to " + args[2]);
        	if (dungeon.getOnVariableSet().get(args[1]) != null) {
        		room.doAction(dungeon.getOnVariableSet().get(args[1]));
        	}
        });
        
        multiParamActionMap.put("addToDungeonValue", (room, args) -> {
        	Dungeon dungeon = room.getDungeon();
        	dungeon.addToDungeonVal(args[1], Integer.parseInt(args[2]));
        	room.textOut.debug("Vars: Added " + args[2] + " to " + args[1]);
        	if (room.getDungeon().getOnVariableSet().get(args[1]) != null) {
        		room.doAction(dungeon.getOnVariableSet().get(args[1]));
        	}
        });
        
        multiParamActionMap.put("setFeatureDescription", (room, args) -> {
        	room.getFeatures().stream()
        		.filter(feature -> feature.getName().equals(args[1]))
        		.forEach(feature -> feature.setDescription(args[2]));
        });
        
    }

    public List<BackpackItem> searchForHiddenItems (String location) {
        List<BackpackItem> hiddenItemList = hiddenItems.get(location);
        if (hiddenItemList != null) {
            hiddenItems.put(location, new ArrayList<>());
            return hiddenItemList;
        } else {
            return new ArrayList<>();
        }
    }

    public void addHiddenItem (String locationName, BackpackItem item) {
        if (hiddenItems.get(locationName) == null) {
            List<BackpackItem> singleItemList = new ArrayList<>();
            singleItemList.add(item);
            hiddenItems.put(locationName, singleItemList);
        } else {
        	List<BackpackItem> existingHiddenItems = hiddenItems.get(locationName);
        	existingHiddenItems.add(item);
        	hiddenItems.put(locationName, existingHiddenItems); //Probably not needed
        }
    }
    
    public void addHiddenItems (String locationName, List<BackpackItem> items) {
    	items.forEach(item -> addHiddenItem(locationName, item));
    }

    public void vocalize (String message, SpeakingVolume volume) {
        textOut.println(hero.getName() + " " + volume.toString().toLowerCase() + "s, \"" + message + "\".");
        speechListeners.forEach(e -> e.notify(message, volume));
    }

    private void initUniversalSpeechListeners () {
        SpeechListener riddleAnswerListener = (message, volume) -> {
            if (volume == SpeakingVolume.SAY) {
                List<RiddleObstacle> riddles = obstacles.stream()
                        .filter(e -> e.getClass() == RiddleObstacle.class)
                        .map(e -> (RiddleObstacle)e)
                        .collect(Collectors.toList());

                boolean oneCorrect = false;
                for (RiddleObstacle riddle : riddles) {
                    boolean response = riddle.attempt(message, hero);
                    if (response) {
                        textOut.println("You got it right!");
                        oneCorrect = true;
                    }
                }
                if (riddles.size() > 0 && !oneCorrect) {
                    textOut.println("Wrong. Feel the retribution of the sphinx.");
                    hero.takeDamage(5);
                }
            }
        };
        SpeechListener shoutAggroListener = (message, volume) -> {
            if (volume == SpeakingVolume.SHOUT) {
                List<DungeonRoom> adjacentRooms = new ArrayList<>(connectedRooms.values());
                int monstersNow = monsters.size();
                adjacentRooms.forEach(adjRoom -> addMonsters(adjRoom.removeMonsters()));
                if (monstersNow < monsters.size()) {
                    textOut.println("Looks like your shouting got some attention.");
                }
                hero.takeAction("fight");
                updateMonsters();
            }
        };
        speechListeners.add(riddleAnswerListener);
        speechListeners.add(shoutAggroListener);
    }

    public List<Monster> removeMonsters () {
        List<Monster> removed = monsters;
        monsters = new ArrayList<>();
        return removed;
    }


    public void addMonster (Monster monster) {
        monsters.add(monster);
        monster.addRoomReference(this);
    }

    public void addMonsters (List<Monster> monsters) {
        monsters.forEach(this::addMonster);
    }

    public void addContainer (Chest chest) {
        this.chest = chest;
    }

    public void addItem (BackpackItem item) {
        items.add(item);
    }

    public void connectTo (Direction direction, DungeonRoom other) {
        if (connectedRooms.get(direction) != null) {
            if (connectedRooms.get(direction) == other) {
            } else {
                throw new AssertionError("Already connected to a different room in that direction.");
            }
        }
        if (other == null) {
            throw new AssertionError("Cannot connect to a null room. This = " + this.getName());
        }

        connectedRooms.put(direction, other);
    }

    public Set<Direction> getTravelDirections () {
        return connectedRooms.keySet();
    }
    
    @Override
    public void start (TextInterface textOut) {
        this.textOut = textOut;
        children = new ArrayList<>();    
    }

    @Override
    public InputType show () {
    	monsters.forEach(monster -> monster.addRoomReference(this));
		describe();
    	return InputType.NONE;
    }

    public void describe () {
    	
    	/* Description Order
    	 * (if applicable)
    	 * 1. Light level
    	 * 2. Room Description
    	 * 3. Monsters
    	 * 4. Obstacles that block travel
    	 * 5. Chest
    	 * 6. Visible items
    	 * 7. Features with a description
    	 * 8. Riddle message
    	 * 9. Obstacles that don't block travel, features with no description
    	 * 10. Passages out
    	 * This is based on a rough concept of the priorities/importance,
    	 * with passages last because they're about leaving the room. 
    	 */
        LightingLevel lightingLevel = LightingLevel.getLightingLevel(lighting);
        //1. Light level
    	switch (lightingLevel) {
    	case WELL_LIT:
    		textOut.println("The room is well-lit.");
    		break;
    	case DIM:
    		textOut.println("The room is dimly lit.");
    		break;
    	case PITCH_BLACK:
    		textOut.println("The room is pitch black.");
    	}
    	//2. Room Description
        if (description != null) {
        	textOut.println(description);
        }
        //3. Monsters
        switch (lightingLevel) {
	        case WELL_LIT:
	        	List<Monster> undescribed = new ArrayList<>(monsters);
	        	for (Monster monster : monsters) {
	        		if (monster.hasDescription()) {
	        			textOut.println(monster.getDescription());
	        			undescribed.remove(monster);
	        		}
	        	}
	        	if (undescribed.size() > 1) {
	        		textOut.println("You see " + StringUtils.prettyPrintListNoPeriod(undescribed) + " moving around the room.");
	        	} else if (undescribed.size() == 1) {
	        		textOut.println("You see a " + undescribed.get(0) + " moving around the room.");
	        	}
	            break;
	        case DIM:
	            if (monsters.size() > 1) {
	                textOut.println("You can see " + monsters.size() + " figures moving around.");
	            } else if (monsters.size() == 1) {
	                textOut.println("You can see one figure moving around.");
	            }
	            break;
			default:
				break;
        }
        
        //4. Obstacles that block travel
        describeBlockingObstacles();
        
        //5. Chest
        if (chest != null && chest.isVisible(lighting)) {
        	String chestDescription;
        	if (chest.isOpen()) {
        		if (chest.getContents().size() != 0) {
        			chestDescription = "There's an open " + chest.getName() + " with " + StringUtils.prettyPrintList(chest.getContents());
        		} else {
        			chestDescription = "There's an open " + chest.getName() + " (empty)";
        		}
        	} else {
        		chestDescription = "There's " + StringUtils.addAOrAn(chest.getName());
            	if (chest.getContents().size() == 0) {
            		chestDescription += " (empty)";
            	}
            	chestDescription += ".";
        	}
        	
        	textOut.println(chestDescription);
        }
        
        //6. Visible items
        List<BackpackItem> visibleItems = items.stream().filter(item -> item.isVisible(lighting)).collect(Collectors.toList());

        if (visibleItems.size() > 1) {
        	textOut.println("You can see the following items: " + StringUtils.prettyPrintList(visibleItems));
        } else if (visibleItems.size() == 1) {
        	textOut.println("You can see " + StringUtils.addAOrAn(visibleItems.get(0).getName()) + ".");
        }
	    
        //7. Features with a description
        features.stream()
        	.filter(feature -> feature.isVisible(lighting))
        	.filter(feature -> feature.getDescription() != null)
        	.forEach(feature -> textOut.println(feature.getDescription()));
        
        //8. Riddle messages
        obstacles.stream()
            .filter(obs -> obs.getClass() == RiddleObstacle.class)
            .filter(riddle -> !riddle.isCleared())
            .forEach(riddle -> textOut.println("A riddle:\"" + ((RiddleObstacle) riddle).getRiddle() + "\""));
        
        //9. Obstacles that don't block travel, features with no description
        List<String> lowPrioFeatures = new ArrayList<>();
        obstacles.stream()
        	.filter(obs -> {
        		if (obs.isCleared()) {
        			return obs.isDisplayIfCleared();
        		}
        		return true;
        	})
        	.filter(obs -> {
        		List<Direction> blocked = obs.getBlockedDirections();
        		if (blocked == null || blocked.size() == 0) { //Safe because of short-circuiting
        			return true;
        		}
        		return false;
        	})
        	.forEach(obs -> lowPrioFeatures.add(obs.getName()));
        features.stream()
        	.filter(feature -> feature.isVisible(lighting))
        	.filter(feature -> feature.getDescription() == null)
        	.forEach(feature -> {
        		String shortDesc = feature.getName();
        		if (feature.getStatus() != null) {
        			shortDesc += " - " + feature.getStatus();
        		}
        		lowPrioFeatures.add(shortDesc);
        	});
        if (lowPrioFeatures.size() > 0) {
        	textOut.println("You can also see " + StringUtils.prettyPrintList(lowPrioFeatures));
        }

        //10. Passages out
        describePassages();
    }
    
    private void describeBlockingObstacles () {
    	obstacles.stream()
			.filter(obstacle -> { //Filter out obstacles we don't want to display
				if (obstacle.isCleared()) {
					return obstacle.isDisplayIfCleared();
				}
				return true;
			}) 
            .filter(obstacle -> { //Then filter for blockers that aren't cleared
                if (!obstacle.isCleared()) {
                	List<Direction> blocked = obstacle.getBlockedDirections();
                	if (blocked != null && blocked.size() > 0) {
                		return true;
                	} else {
                		return false;
                	}
                }
                return false;
            })
            .forEach(obs -> {
            	List<Direction> blocked = obs.getBlockedDirections();
        		String obstacleDescription = "A " + obs.getName() + " blocks travel ";
        		if (blocked.contains(Direction.ALL)) {
        			obstacleDescription += "in all directions.";
        			textOut.println(obstacleDescription);
        			return;
        		}
        		List<Direction> cardinals = new ArrayList<>();
        		List<Direction> nonCardinals = new ArrayList<>();
        		blocked.stream()
        			.forEach(direction -> {
        				if (direction.isCardinal()) {
        					cardinals.add(direction);
        				} else {
        					nonCardinals.add(direction);
        				}
        			});
        		if (nonCardinals.contains(Direction.PORTAL)) {
        			obstacleDescription += "through the Portal";
        			nonCardinals.remove(Direction.PORTAL);
        			if (nonCardinals.size() + cardinals.size() > 0) {
        				obstacleDescription += ", ";
        			}
        		}
        		if (nonCardinals.size() > 0) {
        			obstacleDescription += StringUtils.prettyPrintList(nonCardinals);
        			if (cardinals.size() > 0) {
        				obstacleDescription.replaceAll("\\.", ", ");
        			}
        		}
        		obstacleDescription += StringUtils.prettyPrintList(cardinals);
        		textOut.println(obstacleDescription);
            });
    }
    
    private void describePassages () {
    	Set<Direction> passages = connectedRooms.keySet();
        List<Direction> cardinals = passages.stream().filter(dir -> dir.isCardinal()).collect(Collectors.toList());
        int numPassages = cardinals.size();
        String passageDescription;
        if (numPassages > 1) {
        	passageDescription = "There are passages leading ";
        } else if (numPassages == 1) {
        	passageDescription = "There is a passage leading ";
        } else {
        	passageDescription = "";
        }
        passageDescription += StringUtils.prettyPrintList(cardinals);
        textOut.println(passageDescription);
        if (passages.contains(Direction.UP) || passages.contains(Direction.DOWN)) {
        	String stairsDescription = "There are stairs leading ";
        	if (passages.contains(Direction.UP)) {
        		stairsDescription += "up";
        		if (passages.contains(Direction.DOWN)) {
        			stairsDescription += " and down.";
        		} else {
        			stairsDescription += ".";
        		}
        	} else {
        		stairsDescription += "down.";
        	}
        	textOut.println(stairsDescription);
        }
        if (passages.contains(Direction.PORTAL)) {
        	textOut.println("There is also a strange portal.");
        }
    }

    public List<BackpackItem> lootRoom () {
    	boolean blocked = obstacles.stream().anyMatch(obs -> obs.blocksLooting() && !obs.isCleared());
    	List<BackpackItem> visibleItems;
    	if (!blocked) {
	        visibleItems = items.stream()
	                .filter(item -> item.isVisible(lighting))
	                .collect(Collectors.toList());
	        items.removeAll(visibleItems);
    	} else {
    		visibleItems = new ArrayList<>();
    		textOut.println("There's an obstacle blocking you from looting.");
    	}
    	return visibleItems;
    }
    
    public boolean isDirectlyConnectedTo (DungeonRoom otherRoom) {
    	return connectedRooms.containsValue(otherRoom);
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
        if (lighting != this.lighting) {
            if (onLightingChange != null) {
                String action = onLightingChange.get(LightingLevel.getLightingLevel(lighting).toString());
                if (action != null) {
                    doAction(action);
                }
            }
        }
        if (lighting > 1.0) {
            this.lighting = 1.0;
        } else if (lighting < 0.0) {
            this.lighting = 0.0;
        } else {
            this.lighting = lighting;
        }
    }
    
    public static String replaceVariables (String input, Map<String, String> variables, Map<String, Integer> values) {
    	while (input.contains("{")) {
    		int openIndex = input.indexOf("{");
    		int closeIndex = input.indexOf("}");
    		String varString = input.substring(openIndex + 1, closeIndex);
    		
    		String mappedValue;
    		if (values.containsKey(varString)) {
    			mappedValue = "" + values.get(varString);
    		} else if (variables.containsKey(varString)) {
    			mappedValue = variables.get(varString);
    		} else {
    			throw new AssertionError("Could not find the variable in any map. Input: " + input);
    		}
    		
    		input = input.substring(0, openIndex) + mappedValue + input.substring(closeIndex + 1);
    	}
    	return input;
    }
    
    private String replaceVariables (String input) {
    	Map<String, String> variables = getDungeon().getDungeonVariables();
    	Map<String, Integer> values = getDungeon().getDungeonValues();
    	while (input.contains("{")) {
    		int openIndex = input.indexOf("{");
    		int closeIndex = input.indexOf("}");
    		String varString = input.substring(openIndex + 1, closeIndex);
    		String value;
    		if (varString.contains(".")) {
    			String[] tokens = varString.split("\\.");
    			if (tokens[0].equals("dungeon")) {
    				varString = tokens[1];
    				
    				if (values.containsKey(varString)) {
    	    			value = "" + values.get(varString);
    	    		} else if (variables.containsKey(varString)) {
    	    			value = variables.get(varString);
    	    		} else {
    	    			throw new AssertionError("Could not find the variable in any map. Input: " + input);
    	    		}
    			} else if (tokens[0].equals("hero")) {
    				String fieldName = tokens[1];
    				if (fieldName.equals("name")) {
    					value = getHero().getName();
    				} else {
    					Integer result = getHero().getIntField(fieldName);
    					if (result != null) {
    						value = "" + result;
    					} else {
    						throw new AssertionError("Bad dot notation field with input: " + input);
    					}
    				}
    			} else {
    				throw new AssertionError("Bad dot notation in input: " + input);
    			}
    		} else {
	    		if (values.containsKey(varString)) {
	    			value = "" + values.get(varString);
	    		} else if (variables.containsKey(varString)) {
	    			value = variables.get(varString);
	    		} else {
	    			value = "0";
	    			textOut.debug("Could not find variable *" + varString + "* in any map. Input: " + input);
	    		}
    		}
    		
    		input = input.substring(0, openIndex) + value + input.substring(closeIndex + 1);
    	}
    	return input;
    }
    
    private static boolean evaluateCondition (String condition) {
    	String[] tokens = condition.split(" ");
    	String first = tokens[0];
    	String second = tokens[2];
    	String comparator = tokens[1];
    	
    	if (comparator.equals("=")) {
    		return first.equals(second);
    	} else if (comparator.equals("!=")) {
    		return !first.equals(second);
    	} else {
    		int firstVal = Integer.parseInt(first);
    		int secondVal = Integer.parseInt(second);
    		
    		if (comparator.equals(">")) {
    			return firstVal > secondVal;
    		} else if (comparator.equals(">=")) {
    			return firstVal >= secondVal;
    		} else if (comparator.equals("<")) {
    			return firstVal < secondVal;
    		} else if (comparator.equals("<=")) {
    			return firstVal <= secondVal;
    		} else {
    			throw new AssertionError("Comparison was illegal: " + comparator);
    		}
    	}
    }
    
    private static boolean evaluateConditionForBoolean (String condition) {
    	String[] splits;
    	boolean isAnd;
    	if (condition.contains("AND")) {
    		splits = condition.split("AND");
    		isAnd = true;
    	} else if (condition.contains("&&")) {
    		splits = condition.split("&&");
    		isAnd = true;
    	} else if (condition.contains("OR")) {
    		splits = condition.split("OR");
    		isAnd = false;
    	} else if (condition.contains("||")) {
    		splits = condition.split("||");
    		isAnd = false;
    	} else {
    		return evaluateCondition(condition);
    	}
    	if (isAnd) {
    		return evaluateCondition(splits[0].trim()) && evaluateCondition(splits[1].trim());
    	} else {
    		return evaluateCondition(splits[0].trim()) || evaluateCondition(splits[1].trim());
    	}
    }
    
    public void doAction (String action) {
    	String originalMessage = action;
        if (voidActionMap == null || paramActionMap == null || multiParamActionMap == null) {
            initActionMaps();
        }
        System.out.println("action = " + action);
        //If action contains a semi-colon it contains multiple sub-actions
        if (action.startsWith("$if")) {
        	if (action.contains("{")) {
            	action = replaceVariables(action);
            }
        	String condition = action.substring(action.indexOf("[") + 1, action.indexOf("]"));
        	boolean proceed = evaluateConditionForBoolean(condition);
        	if (proceed) {
        		action = action.substring(action.indexOf("]") + 2);
        		if (action.contains("$else")) {
        			action = action.substring(0, action.indexOf("$else"));
        		}
        	} else {
        		if (action.contains("$else")) {
        			action = action.substring(action.indexOf("$else") + 6);
        			textOut.debug("Found an else. Proceeding with action: " + action);
        		} else {
	        		textOut.debug("Not proceeding with action. Original statement: " + originalMessage);
	        		return;
        		}
        	}
        }
        if (action.contains(";")) {
        	String[] statements = action.split(";");
        	for (String statement : statements) {
        		doAction(statement);
        	}
        	return;
        }
        
        if (action.contains("{")) {
        	action = replaceVariables(action);
        }
        
        
        if (action.startsWith("@")) {
        	String[] tokens = action.split(" ");
        	String roomTargetString = tokens[0].replaceAll("@", "");
        	try {
        		int roomId = Integer.parseInt(roomTargetString);
        		DungeonRoom target = getDungeon().getRoom(roomId);
        		if (target != null) {
        			textOut.debug("Attempting to send instruction to " + target.getName());
        			String actionString = action.substring(action.indexOf(" ") + 1);
        			textOut.debug("Sending message: " + actionString);
        			try {
        				target.doAction(actionString);
        			} catch (Throwable t) {
        				t.printStackTrace();
        			}
        		} else {
        			textOut.debug("Could not find room with id " + roomId + ". Whole action message was " + action);
        		}
        	} catch (NumberFormatException ex) {
        		textOut.debug("NumberFormatException parsing @id. Message was: " + action);
        	}
        	textOut.printDebug();
        	return;
        }
        
        if (action.contains(" ")) {
        	if (action.contains("\"")) {
        		//Objective here is to pull out the quoted message, then split
        		//Then put the message back and evaluate. 
        		
        		//TODO - want to get this working:
        		//"setFeatureDescription Furnace \"A large furnace occupies this room. It's warm and burning away.\""
        		String message = action.split("\"")[1];
        		String actionWithoutQuote = action.substring(0, action.indexOf("\"")) 
        				+ "#message" + action.substring(action.lastIndexOf("\"") + 1);
        		String[] tokens = actionWithoutQuote.split(" ");
        		for (int i = 0; i < tokens.length; i++) {
        			if (tokens[i].equals("#message")) {
        				tokens[i] = message;
        			}
        		}
        		if (tokens.length == 2) {
        			paramActionMap.get(tokens[0]).doAction(this, message);
        		} else {
        			multiParamActionMap.get(tokens[0]).doAction(this, tokens);
        		}
        	} else {
	            String[] tokens = action.split(" ");
	            if (tokens.length == 2) {
	            	paramActionMap.get(tokens[0]).doAction(this, tokens[1]);
	            } else {
	            	multiParamActionMap.get(tokens[0]).doAction(this, tokens);
	            }
        	}
        } else {
            voidActionMap.get(action).doAction(this);
        }
        
    }
    
    /**
     * Attempts to take a "template" DungeonRoom and apply any fields present
     * that we are missing. We want to keep our own fields if they exist.
     * For example if we don't have an onCombatStart property but the template does
     * then we will use the template property. But if we already have that property
     * we'll keep it.
     * 
     * Fields skipped:
     * id - shouldn't be filled by templates.
     * bossFight - same
     * connectedRoomIds - same
     * lighting - problem with simple/complex types
     * (all transient fields)
     * @param template
     */
    public void applyTemplate (DungeonRoom template) {
    	if (name == null) {
    		name = template.name;
    	}
    	if (description == null) {
    		description = template.description;
    	}
    	if (tutorial == null) {
    		tutorial = template.tutorial;
    	}
    	/*
    	if (lighting == null) {
    		lighting = template.lighting;
    	}
    	*/
    	if (template.monsters != null) {
    		template.monsters.forEach(monster -> addMonster(new Monster(monster)));
    	}
    	if (template.items != null) {
    		template.items.forEach(item -> addItem(new BackpackItem(item)));
    	}
    	if (template.features != null) {
    		throw new AssertionError("Dunno what this is (see Feature class)");
    	}
    	if (template.hiddenItems != null) {
    		template.hiddenItems.keySet().forEach(locationName -> {
    			addHiddenItems(locationName, template.hiddenItems.get(locationName));
    		});
    	}
    	if (template.obstacles != null) {
    		if (template.obstacles.size() > 0) {
    			throw new AssertionError("Template obstacles not supported at this time.");
    		}
    	}
    	if (chest == null) {
    		chest = template.chest;
    	}
    	
    	//TODO: fix this very duplicative code
    	//Triggers
    	if (template.specialRoomActions != null) {
    		if (specialRoomActions == null) {
    			specialRoomActions = new HashMap<>();
    			specialRoomActions.putAll(template.specialRoomActions);
    		}
    		template.specialRoomActions.keySet().forEach(special -> {
    			specialRoomActions.putIfAbsent(special, template.specialRoomActions.get(special));
    		});
    	}
    	if (template.onLightingChange != null) {
    		if (onLightingChange == null) {
    			onLightingChange = new HashMap<>();
    			onLightingChange.putAll(template.onLightingChange);
    		}
    		template.onLightingChange.keySet().forEach(lighting -> {
    			onLightingChange.putIfAbsent(lighting, template.onLightingChange.get(lighting));
    		});
    	}
    	if (template.onHeroLeave != null) {
    		if (onHeroLeave == null) {
    			onHeroLeave = new HashMap<>();
    			onHeroLeave.putAll(template.onHeroLeave);
    		}
    		template.onHeroLeave.keySet().forEach(direction -> {
    			onHeroLeave.putIfAbsent(direction, template.onHeroLeave.get(direction));
    		});
    	}
    	if (template.onItemUse != null) {
    		if (onItemUse == null) {
    			onItemUse = new HashMap<>();
    			onItemUse.putAll(template.onItemUse);
    		} else {
	    		template.onItemUse.keySet().forEach(itemName -> {
	    			onItemUse.putIfAbsent(itemName, template.onItemUse.get(itemName));
	    		});
    		}
    	}
    	if (template.onSpellCast != null) {
    		if (onSpellCast == null) {
				onSpellCast = new HashMap<>();
				onSpellCast.putAll(template.onSpellCast);
			} else {
	    		template.onSpellCast.keySet().forEach(spell -> {
	    			onSpellCast.putIfAbsent(spell, template.onSpellCast.get(spell));
	    		});
			}
    	}
    	if (template.onSearch != null) {
    		if (onSearch == null) {
    			onSearch = new HashMap<>();
    			onSearch.putAll(template.onSearch);
    		} else {
	    		template.onSearch.keySet().forEach(search -> {
	    			onSearch.putIfAbsent(search, template.onSearch.get(search));
	    		});
    		}
    	}
    	
    	if (onCombatStart == null) {
    		onCombatStart = template.onCombatStart;
    	}
    	if (onCombatEnd == null) {
    		onCombatEnd = template.onCombatEnd;
    	}
    	if (onHeroEnter == null) {
    		onHeroEnter = template.onHeroEnter;
    	}
    }

    public boolean isCleared () {
        return monsters.size() == 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Monster> getMonsters() {
        return monsters;
    }

    public void setMonsters(List<Monster> monsters) {
        this.monsters = monsters;
    }

    public List<BackpackItem> getItems() {
        return items;
    }

    public void setItems(List<BackpackItem> items) {
        this.items = items;
    }

    public Chest getChest() {
        return chest;
    }

    public void setChest(Chest chest) {
        this.chest = chest;
    }

    public Map<Direction, Integer> getConnectedRoomIds() {
        return connectedRoomIds;
    }

    public void setConnectedRoomIds(Map<Direction, Integer> connectedRoomIds) {
        this.connectedRoomIds = connectedRoomIds;
    }

    public Hero getHero() {
        return hero;
    }

    private transient int numVisits = 0;

    public void setHero(Hero hero) {
        if (hero == null) {
            throw new AssertionError("Cannot be used to remove hero. Use removeHero() instead.");
        }
        this.hero = hero;
        numVisits++;
        if (tutorial != null) {
            if (numVisits == 1) {
                textOut.tutorial(tutorial);
            }
        }
        if (onHeroEnter != null) {
        	if (onHeroEnter.wantsToTrigger()) {
        		doAction(onHeroEnter.getAction());
        		onHeroEnter.setDone(true);
        	}
        }
    }

    public void removeHero () {
        hero = null;
    }

    public Map<Direction, DungeonRoom> getConnectedRooms() {
        return connectedRooms;
    }

    public void setConnectedRooms(Map<Direction, DungeonRoom> connectedRooms) {
        this.connectedRooms = connectedRooms;
    }

    public Map<String, List<BackpackItem>> getHiddenItems() {
        return hiddenItems;
    }

    public void setHiddenItems(Map<String, List<BackpackItem>> hiddenItems) {
        this.hiddenItems = hiddenItems;
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    /**
     * Sets up back references.
     * @param obstacles
     */
    public void setObstacles(List<Obstacle> obstacles) {
        this.obstacles = obstacles;
        this.obstacles.forEach(obs ->  obs.setLocation(this));
    }

    public void addObstacle (Obstacle obstacle) {
        obstacles.add(obstacle);
        obstacle.setLocation(this);
    }

    public String getBossFightFileLocation() {
        return bossFightFileLocation;
    }

    public String getTutorial() {
        return tutorial;
    }

    public void setTutorial(String tutorial) {
        this.tutorial = tutorial;
    }

    public Map<String, String> getSpecialRoomActions() {
        return specialRoomActions;
    }

    public void setSpecialRoomActions(Map<String, String> specialRoomActions) {
        this.specialRoomActions = specialRoomActions;
    }
    
    public Map<String, String> getOnLightingChange() {
		return onLightingChange;
	}

	public void setOnLightingChange(Map<String, String> onLightingChange) {
		this.onLightingChange = onLightingChange;
	}

	public Map<Direction, LeavingRoomAction> getOnHeroLeave() {
        return onHeroLeave;
    }

    public void setOnHeroLeave(Map<Direction, LeavingRoomAction> onHeroLeave) {
        this.onHeroLeave = onHeroLeave;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }
    
    public Dungeon getDungeon () {
    	return dungeon;
    }
    
    public void setDungeon (Dungeon dungeon) {
    	this.dungeon = dungeon;
    }
    
    public String getOnCombatStart () {
    	return onCombatStart;
    }
    
    public String getOnCombatEnd () {
    	return onCombatEnd;
    }
    
    public void setOnCombatStart (String onCombatStart) {
    	this.onCombatStart = onCombatStart;
    }
    
    public void setOnCombatEnd (String onCombatEnd) {
    	this.onCombatEnd = onCombatEnd;
    }
    
    public Map<String, String> getOnSpellCast () {
    	return onSpellCast;
    }
    
    public void setOnSpellCast (Map<String, String> onSpellCast) {
    	this.onSpellCast = onSpellCast;
    }
    
    public Map<String, String> getOnSearch () {
    	return onSearch;
    }
    
    public void setOnSearch (Map<String, String> onSearch) {
    	this.onSearch = onSearch;
    }
    
    public Map<String, String> getOnItemUse () {
    	return onItemUse;
    }
    
    public void setOnItemUse (Map<String, String> onItemUse) {
    	this.onItemUse = onItemUse;
    }

	public EnteringRoomAction getOnHeroEnter() {
		return onHeroEnter;
	}

	public void setOnHeroEnter(EnteringRoomAction onHeroEnter) {
		this.onHeroEnter = onHeroEnter;
	}
	
	public Map<String, String> getOnHeroAction() {
		return onHeroAction;
	}

	public void setOnHeroAction(Map<String, String> onHeroAction) {
		this.onHeroAction = onHeroAction;
	}

	public Map<String, Map<String, String>> getMetaMap () {
		if (metaMap == null) {
			initMetaMap();
		}
		return metaMap;
	}
}
