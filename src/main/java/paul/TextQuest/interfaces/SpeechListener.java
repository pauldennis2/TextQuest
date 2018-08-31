package paul.TextQuest.interfaces;

import paul.TextQuest.enums.SpeakingVolume;

/**
 * Created by Paul Dennis on 8/14/2017.
 */
public interface SpeechListener {
    void notify (String message, SpeakingVolume volume);
}
