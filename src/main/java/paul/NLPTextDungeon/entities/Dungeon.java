package paul.NLPTextDungeon.entities;



import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    public static final String GOAL_INTRO = "You must venture into the ";


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

    public static void main(String[] args) {
        String dungeonJson = readDungeonFromFile("content_files/dungeons/practice_dungeon.json");
        System.out.println(dungeonJson);
        Dungeon restored = jsonRestore(dungeonJson);
        System.out.println(restored);
    }

}
