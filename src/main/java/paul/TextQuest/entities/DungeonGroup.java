/**
 * @author Paul Dennis
 * Sep 11, 2018
 */
package paul.TextQuest.entities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DungeonGroup {
	
	private String name;
	private Map<String, DungeonInfo> dungeonInfo;
	
	public DungeonGroup () {
		//Order matters (want to present the dungeons consistently first to last)
		dungeonInfo = new LinkedHashMap<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, DungeonInfo> getDungeonInfo() {
		return dungeonInfo;
	}

	public void setDungeonInfo(Map<String, DungeonInfo> dungeonInfo) {
		this.dungeonInfo = dungeonInfo;
	}

	public static DungeonGroup buildGroupFromFile (String fileName) throws IOException {
		return jsonRestore(readDungeonGroupFromFile(fileName));
	}
	
	private static DungeonGroup jsonRestore(String dungeonGroupJson) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(dungeonGroupJson, DungeonGroup.class);
    }
	
	private static String readDungeonGroupFromFile (String fileName) {
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
	
	public static void main(String[] args) throws Exception {
		DungeonGroup group = buildGroupFromFile("content_files/first_dungeon_group.json");
		System.out.println(group);
	}
	
}
