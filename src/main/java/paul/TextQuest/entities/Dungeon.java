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
    
    private Map<String, BackpackItem> itemLibrary;
    private Map<String, Monster> monsterLibrary;
    
    private String onVariableSet;

    private transient boolean cleared;
    private transient DungeonRoom entrance;
    private Map<String, Integer> levels;
    
    private transient Map<Integer, DungeonRoom> roomsById;
    
    private transient Map<String, String> dungeonVariables;
    private transient Map<String, Integer> dungeonValues;
    
    private DungeonRoom template;

    public Dungeon () {
        rooms = new ArrayList<>();
        dungeonVariables = new HashMap<>();
        dungeonValues = new HashMap<>();
    }

    public DungeonRoom getRoomByName (String name) {
        List<DungeonRoom> matches = rooms.stream()
                .filter(room -> room.getName().equals(name))
                .collect(Collectors.toList());

        if (matches.size() == 0) {
            return null;
        }
        return matches.get(0);
    }
    
    public DungeonRoom getRoomById (int id) {
    	return roomsById.get(id);
    }

    public static Dungeon buildDungeonFromFile (String fileName) throws IOException {
        Dungeon restored = jsonRestore(readDungeonFromFile(fileName));
        restored.connectRooms();
        return restored;
    }

    private static Dungeon jsonRestore(String dungeonJson) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(dungeonJson, Dungeon.class);
    }

    private static String readDungeonFromFile (String fileName) {
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
        rooms.forEach(e -> roomsById.put(e.getId(), e));

        entrance = roomsById.get(1);

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
    	System.out.println("Completed evaluation of dungeon:" + dungeonName + " with " + rooms.size() + " rooms.");
    	
    	System.out.println("One way warnings (" + oneWayWarnings.size() + "):");
    	oneWayWarnings.forEach(System.out::println);
    	
    	System.out.println("Trigger warnings (" + triggerWarnings.size() + "): (no, not that kind)");
    	triggerWarnings.forEach(System.out::println);
    	
    	System.out.println("Miscellaneous warnings (" + miscWarnings.size() + ")");
    	miscWarnings.forEach(System.out::println);
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
	
	public void setOnVariableSet (String onVariableSet) {
		this.onVariableSet = onVariableSet;
	}
	
	public String getOnVariableSet () {
		return onVariableSet;
	}
    
}
