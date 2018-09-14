/**
 * @author Paul Dennis
 * Sep 14, 2018
 */
package paul.TextQuest.entities;

import java.util.ArrayList;
import java.util.List;

public class DungeonInfo {
	
	private String fileLocation;
	private List<String> prereqs;
	
	public DungeonInfo () {
		prereqs = new ArrayList<>();
	}

	public DungeonInfo(String name, String fileLocation) {
		super();
		this.fileLocation = fileLocation;
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
}
