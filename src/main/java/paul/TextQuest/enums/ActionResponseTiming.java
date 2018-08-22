package paul.TextQuest.enums;

/**
 * Created by Paul Dennis on 8/14/2017.
 */
public enum ActionResponseTiming {
    //Represents response possibilities for boss fights.
    //Example: I want to jump BEFORE the boss uses pee
    //I want to defend DURING the boss's whirlwind
    //AFTER the boss uses smash, I want to drink a potion
    BEFORE, AFTER, DURING;

    public static ActionResponseTiming getFromString (String input) {
        switch (input.toLowerCase()) {
            case "before":
                return BEFORE;
            case "after":
                return AFTER;
            case "during":
                return DURING;
            default:
                throw new AssertionError();
        }
    }
}
