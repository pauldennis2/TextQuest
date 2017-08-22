package paul.NLPTextDungeon.entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import paul.NLPTextDungeon.enums.Direction;

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

    private transient DungeonRoom entrance;

    public Dungeon () {
        rooms = new ArrayList<>();
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
        Map<Integer, DungeonRoom> roomsById = new HashMap<>();
        rooms.forEach(e -> roomsById.put(e.getId(), e));

        entrance = roomsById.get(1);

        rooms.forEach(e -> {
            Map<Direction, Integer> connectedRoomIds = e.getConnectedRoomIds();
            connectedRoomIds.keySet().forEach(f -> {
                Integer id = connectedRoomIds.get(f);
                DungeonRoom otherRoom = roomsById.get(id);
                e.connectTo(f, otherRoom);
            });
        });
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
