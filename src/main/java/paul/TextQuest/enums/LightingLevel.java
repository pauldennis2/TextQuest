package paul.TextQuest.enums;

/**
 * Created by Paul Dennis on 8/9/2017.
 */
public enum LightingLevel {
    WELL_LIT, PITCH_BLACK, DIM;

    public static LightingLevel getLightingLevel (double lightingLevel) {
        if (lightingLevel > 0.8) {
            return WELL_LIT;
        } else if (lightingLevel == 0) {
            return PITCH_BLACK;
        } else {
            return DIM;
        }
    }
}
