/**
 * @author Paul Dennis
 * Aug 28, 2018
 */
package paul.TextQuest.interfaces;

import paul.TextQuest.entities.DungeonRoom;

public interface MultiParamAction {
	void doAction(DungeonRoom room, String[] args);
}
