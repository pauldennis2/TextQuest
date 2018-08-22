package paul.TextQuest.enums;

/**
 * Created by Paul Dennis on 9/5/2017.
 */
public enum BehaviorTiming {

    EVERY_ROUND, //1,2,3,4,5...
    EVERY_OTHER_ROUND, //1,3,5...
    EVERY_THIRD_ROUND, //1,4,7...
    FLAT_CHANCE,
    ESCALATING_CHANCE;

    public static final double DEFAULT_CHANCE = 0.3;

    public static boolean doBehavior (BehaviorTiming timing, int roundNum) {
        switch (timing) {
            case EVERY_ROUND:
                return true;
            case EVERY_OTHER_ROUND:
                return roundNum % 2 == 1;
            case EVERY_THIRD_ROUND:
                return roundNum % 3 == 1;
            case FLAT_CHANCE:
                return Math.random() < DEFAULT_CHANCE;
            case ESCALATING_CHANCE:
                throw new AssertionError("Not yet implemented");
            default:
                throw new AssertionError("Unreachable");
        }
    }

}
