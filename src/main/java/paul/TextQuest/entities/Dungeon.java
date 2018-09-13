package paul.TextQuest.entities;

import com.fasterxml.jackson.databind.ObjectMapper;

import paul.TextQuest.enums.Direction;
import paul.TextQuest.parsing.TextInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by Paul Dennis on 8/8/2017.
 */
public class Dungeon extends MetaLocation {

    private List<DungeonRoom> rooms;
    private String description;
    private String dungeonName;
    
    private List<String> beastiaries;
    
    private Map<String, BackpackItem> itemLibrary;
    private Map<String, Monster> monsterLibrary;
    
    //private String onVariableSet;
    
    private Map<String, String> onVariableSet;
    
    private Integer entranceRoomId;

    private transient boolean cleared;
    private transient DungeonRoom entrance;
    private Map<String, Integer> levels;
    
    private transient Map<Integer, DungeonRoom> roomsById;
    private transient Map<String, DungeonRoom> roomsByName;
    
    private transient Map<String, String> dungeonVariables;
    private transient Map<String, Integer> dungeonValues;
    
    private DungeonRoom template;

    public Dungeon () {
        rooms = new ArrayList<>();
        dungeonVariables = new HashMap<>();
        dungeonValues = new HashMap<>();
        onVariableSet = new HashMap<>();
    }

    public DungeonRoom getRoom (String name) {
        List<DungeonRoom> matches = rooms.stream()
                .filter(room -> room.getName().equals(name))
                .collect(Collectors.toList());

        if (matches.size() == 0) {
            return null;
        }
        return matches.get(0);
    }
    
    public DungeonRoom getRoom (int id) {
    	return roomsById.get(id);
    }
    
    /**Note: this method is a bit weird, in that it gets called BY the room,
     * then calls all the individual rooms. The calling room is responsible
     * for triggering the dungeon's tick, if present.
     */
    public void doTick () {
    	Map<DungeonRoom, List<TickTock>> tickTockMap = new HashMap<>();
    	
    	for (DungeonRoom room : rooms) {
    		tickTockMap.put(room, new ArrayList<TickTock>(room.getTickTocks()));
    	}
    	tickTockMap.keySet().forEach(room -> {
    		List<TickTock> tickTocks = tickTockMap.get(room);
    		tickTocks.forEach(tickTock -> {
    			if (tickTock.getOnTick() != null) {
    				room.doAction(tickTock.getOnTick());
    			}
    		});
    	});
    }
    
    public void doTock () {
    	Map<DungeonRoom, List<TickTock>> tickTockMap = new HashMap<>();
    	
    	for (DungeonRoom room : rooms) {
    		tickTockMap.put(room, new ArrayList<TickTock>(room.getTickTocks()));
    	}
    	tickTockMap.keySet().forEach(room -> {
    		List<TickTock> tickTocks = tickTockMap.get(room);
    		tickTocks.forEach(tickTock -> {
    			if (tickTock.getOnTock() != null) {
    				room.doAction(tickTock.getOnTock());
    			}
    		});
    	});
    }

    public static Dungeon buildDungeonFromFile (String fileName) throws IOException {
        Dungeon restored = jsonRestore(readFromFile(fileName));
        restored.connectRooms();
        return restored;
    }

    private static Dungeon jsonRestore(String dungeonJson) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(dungeonJson, Dungeon.class);
    }
    
    public static Map<String, Monster> buildBeastiaryFromFile (String fileName) throws IOException {
    	ObjectMapper mapper = new ObjectMapper();
    	Map<String, Monster> testmap = new HashMap<>();
    	return mapper.readValue(readFromFile(fileName), testmap.getClass());
    }

    private static String readFromFile (String fileName) {
        try (Scanner fileScanner = new Scanner(new File(fileName))) {
            StringBuilder stringBuilder = new StringBuilder(fileScanner.nextLine());
            while (fileScanner.hasNext()) {
                stringBuilder.append(fileScanner.nextLine());
            }
            return stringBuilder.toString();
        } catch (FileNotFoundException ex) {
            throw new AssertionError("Could not read from file");
        }
    }

    private void connectRooms () {
        roomsById = new HashMap<>();
        roomsByName = new HashMap<>();
        
        rooms.forEach(room -> {
        	if (roomsById.containsKey(room.getId()) || roomsByName.containsKey(room.getName())) {
            	throw new AssertionError("Room names and IDs must be unique.");
            }
        	roomsById.put(room.getId(), room);
            roomsByName.put(room.getName(), room);
        });
        //These for-eaches MUST be separate because the 2nd one relies on IDs being ready.
        rooms.forEach(room -> {
        	room.setDungeon(this);
        	
            Map<Direction, Integer> connectedRoomIds = room.getConnectedRoomIds();
            connectedRoomIds.keySet().forEach(f -> {
                Integer id = connectedRoomIds.get(f);
                DungeonRoom otherRoom = roomsById.get(id);
                room.connectTo(f, otherRoom);
            });
            if (template != null) {
            	room.applyTemplate(template);
            }
        });
        if (entranceRoomId == null) {
        	entrance = roomsById.get(1);
        } else {
        	entrance = roomsById.get(entranceRoomId);
        }
    }
    
    public static void main(String[] args) throws Exception {
		System.out.println("Testing dungeon evaluater.");
		Dungeon test = buildDungeonFromFile("content_files/dungeons/evaluate_dungeon_test.json");
		test.evaluateDungeon();
	}
    
    public void evaluateDungeon () throws Exception {
    	List<String> oneWayWarnings = new ArrayList<>();
    	List<String> triggerWarnings = new ArrayList<>();
    	List<String> miscWarnings = new ArrayList<>();
    	for (DungeonRoom room : rooms) {
    		//Check connections
    		Map<Direction, DungeonRoom> connectedRooms = room.getConnectedRooms();
    		for (Direction direction : connectedRooms.keySet()) {
    			DungeonRoom other = connectedRooms.get(direction);
    			if (!other.getConnectedRooms().containsValue(room)) {
    				oneWayWarnings.add("Warning: " + room.getName() + " has a one-way connection to " + other.getName() + ".");
    			}
    		}
    		
    		//Check triggers
    		Map<String, Map<String, String>> metaMap = room.getMetaMap();
    		Hero hero = new Hero("Tester");
    		TextInterface textOut = TextInterface.getInstance(hero);
    		hero.setTextOut(textOut);
    		room.setHero(hero);
    		for (String mapName : metaMap.keySet()) {
    			Map<String, String> triggers = metaMap.get(mapName);
    			if (triggers == null) {
    				continue;
    			}
    			for (String trigger : triggers.keySet()) {
    				try {
    					room.start(textOut);
    					room.doAction(triggers.get(trigger));
    				} catch (Throwable t) {
    					triggerWarnings.add("Warning: exception on trigger " + trigger + " in " + mapName + ". " +
    							"Exception: " + t.getMessage() + ", Action was " + triggers.get(trigger));
    				}
    			}
    		}
    		
    		//Check misc stuff
    		if (room.getName().trim().equals("")) {
    			miscWarnings.add("Warning: Name is blank for room with id " + room.getId());
    		}
    	}
    	List<String> messages = new ArrayList<>();
    	messages.add("Completed evaluation of dungeon:" + dungeonName + " with " + rooms.size() + " rooms.");
    	
    	messages.add("One way warnings (" + oneWayWarnings.size() + "):");
    	oneWayWarnings.forEach(messages::add);
    	
    	messages.add("Trigger warnings (" + triggerWarnings.size() + "): (no, not that kind)");
    	triggerWarnings.forEach(messages::add);
    	
    	messages.add("Miscellaneous warnings (" + miscWarnings.size() + ")");
    	miscWarnings.forEach(messages::add);
    	
    	messages.forEach(System.out::println);
    }
    
    public void setDungeonVar (String name, String variable) {
    	try {
    		Integer val = Integer.parseInt(variable);
    		dungeonValues.put(name, val);
    	} catch (NumberFormatException ex) {
    		dungeonVariables.put(name, variable);
    	}
    }
    
    public void addToDungeonVal (String name, int amount) {
    	if (dungeonValues.containsKey(name)) {
    		dungeonValues.put(name, dungeonValues.get(name) + amount);
    	} else {
    		dungeonValues.put(name, amount);
    	}
    }
    
    public void updateRoomName (DungeonRoom room, String oldName) {
    	if (roomsByName.containsKey(room.getName())) {//If it's already mapped, we have a conflict
    		throw new AssertionError("Room names must be unique. New name " + room.getName() + " is a duplicate.");
    	}
    	roomsByName.remove(oldName);
    	roomsByName.put(room.getName(), room);
    }
    
    
    
    public List<String> getBeastiaries() {
		return beastiaries;
	}

	public void setBeastiaries(List<String> beastiaries) {
		this.beastiaries = beastiaries;
		
		for (String fileName : beastiaries) {
			try {
				Map<String, Monster> monsterMap = buildBeastiaryFromFile(fileName);
				for (String key : monsterMap.keySet()) {
					if (monsterLibrary.containsKey(key)) {
						throw new AssertionError("Namespace conflict. Can't have two monsters named " +
								key + ". Duplicate comes from " + fileName + ".");
					}
					monsterLibrary.put(key, monsterMap.get(key));
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public Map<String, Integer> getDungeonValues () {
    	return dungeonValues;
    }
    
    public Map<String, String> getDungeonVariables () {
    	return dungeonVariables;
    }
    
    public String getDungeonVariable (String name) {
    	return dungeonVariables.get(name);
    }
    
    public int getDungeonValue (String name) {
    	return dungeonValues.get(name);
    }

    public List<DungeonRoom> getRooms() {
        return rooms;
    }

    public void setRooms(List<DungeonRoom> rooms) {
        this.rooms = rooms;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDungeonName() {
        return dungeonName;
    }

    public void setDungeonName(String dungeonName) {
        this.dungeonName = dungeonName;
    }

    public DungeonRoom getEntrance() {
        return entrance;
    }

    public void setEntrance(DungeonRoom entrance) {
        this.entrance = entrance;
    }

    public Map<String, Integer> getLevels() {
        return levels;
    }

    public void setLevels(Map<String, Integer> levels) {
        this.levels = levels;
    }
    
    public boolean isCleared () {
    	return cleared;
    }
    
    public void setCleared (boolean cleared) {
    	this.cleared = cleared;
    }
    
    public void setItemLibrary (Map<String, BackpackItem> itemLibrary) {
    	this.itemLibrary = itemLibrary;
    }
    
    public Map<String, BackpackItem> getItemLibrary () {
    	return itemLibrary;
    }
    
    public void setMonsterLibrary (Map<String, Monster> monsterLibrary) {
    	this.monsterLibrary = monsterLibrary;
    }
    
    public Map<String, Monster> getMonsterLibrary () {
    	if (monsterLibrary == null) {
    		monsterLibrary = new HashMap<>();
    	}
    	return monsterLibrary;
    }

	public DungeonRoom getTemplate() {
		return template;
	}

	public void setTemplate(DungeonRoom template) {
		this.template = template;
	}
	
	public void setOnVariableSet (Map<String, String> onVariableSet) {
		this.onVariableSet = onVariableSet;
	}
	
	public Map<String, String> getOnVariableSet () {
		return onVariableSet;
	}
	
	public void setEntranceRoomId (int entranceRoomId) {
		this.entranceRoomId = entranceRoomId;
	}
    
}
