/**
 * @author Paul Dennis
 * Sep 11, 2018
 */
package paul.TextQuest.entities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DungeonGroup {
	
	private String name;
	private List<DungeonInfo> dungeonInfoList;
	
	public DungeonGroup () {
		dungeonInfoList = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<DungeonInfo> getDungeonInfoList() {
		return dungeonInfoList;
	}

	public void setDungeonInfoList(List<DungeonInfo> dungeonInfoList) {
		this.dungeonInfoList = dungeonInfoList;
	}
	
	@Override
	public String toString() {
		return "DungeonGroup [name=" + name + ", dungeonInfoList=" + dungeonInfoList + "]";
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

class DungeonInfo {
	
	private String name;
	private String fileLocation;
	private List<String> prereqs;
	
	public DungeonInfo () {
		prereqs = new ArrayList<>();
	}

	public DungeonInfo(String name, String fileLocation) {
		super();
		this.name = name;
		this.fileLocation = fileLocation;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFileLocation() {
		return fileLocation;
	}

	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}

	public List<String> getPrereqs() {
		return prereqs;
	}

	public void setPrereqs(List<String> prereqs) {
		this.prereqs = prereqs;
	}

	@Override
	public String toString() {
		return "DungeonInfo [name=" + name + ", fileLocation=" + fileLocation + ", prereqs=" + prereqs + "]";
	}
}
