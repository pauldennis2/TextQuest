package paul.NLPTextDungeon.interfaces.listeners;

import paul.NLPTextDungeon.entities.DungeonRoom;
import paul.NLPTextDungeon.enums.SpeakingVolume;

/**
 * Created by Paul Dennis on 8/14/2017.
 */
public interface SpeechListener {
    void doAction (DungeonRoom room, SpeakingVolume volume, String speech);
}
