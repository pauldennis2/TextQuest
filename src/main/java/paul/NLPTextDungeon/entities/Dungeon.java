package paul.NLPTextDungeon.entities;



import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import paul.NLPTextDungeon.enums.Direction;
import paul.NLPTextDungeon.enums.DungeonGoalType;
import paul.NLPTextDungeon.interfaces.listeners.OnPickup;
import paul.NLPTextDungeon.utils.VictoryException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import static paul.NLPTextDungeon.enums.Direction.*;

/**
 * Created by Paul Dennis on 8/8/2017.
 */
public class Dungeon extends MetaLocation {

    private List<DungeonRoom> rooms;
    private String description;
    private String dungeonName;

    private transient DungeonRoom entrance;

    public Dungeon () {
        rooms = new ArrayList<>();
    }


    public static Dungeon jsonRestore(String dungeonJson) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(dungeonJson, Dungeon.class);
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            throw new AssertionError();
        }
    }

    public static String readDungeonFromFile (String fileName) {
        try (Scanner fileScanner = new Scanner(new File(fileName))) {
            StringBuilder stringBuilder = new StringBuilder(fileScanner.nextLine());
            while (fileScanner.hasNext()) {
                stringBuilder.append(fileScanner.nextLine());
            }
            return stringBuilder.toString();
        } catch (FileNotFoundException ex) {
            System.out.println("Could not find file.");
            throw new AssertionError();
        }
    }

    private void connectRooms () {
        Map<Integer, DungeonRoom> roomsById = new HashMap<>();
        rooms.forEach(e -> roomsById.put(e.getId(), e));

        rooms.forEach(e -> {
            Map<Direction, Integer> connectedRoomIds = e.getConnectedRoomIds();
            connectedRoomIds.keySet().forEach(f -> {
                Integer id = connectedRoomIds.get(f);
                DungeonRoom otherRoom = roomsById.get(id);
                e.connectTo(f, otherRoom);
            });
        });
    }

    public static void main(String[] args) {
        String dungeonJson = readDungeonFromFile("content_files/dungeons/practice_dungeon.json");
        System.out.println(dungeonJson);
        Dungeon restored = jsonRestore(dungeonJson);
        restored.connectRooms();
        System.out.println(restored);
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
}
