package paul.NLPTextDungeon.enums;

/**
 * Created by pauldennis on 8/22/17.
 */
public enum LevelUpCategory {
    INC_STATS, NEW_SKILL, NEW_SPELL;

    public static String getPrettyName (LevelUpCategory category) {
        switch (category) {
            case INC_STATS:
                return "Increase one of your stats (might, defense, or health)";
            case NEW_SKILL:
                return "Learn a new skill (only sneak available right now)";
            case NEW_SPELL:
                return "Learn a new spell";
            default:
                throw new AssertionError();
        }
    }

    public static String getPrompt (LevelUpCategory category) {
        switch (category) {
            case INC_STATS:
                return "Which stat to increase?";
            case NEW_SKILL:
                return "Which skill to learn (only available skill is sneak)";
            case NEW_SPELL:
                return "What type of spell to learn?";
            default:
                throw new AssertionError();
        }
    }
}
