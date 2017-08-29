package paul.NLPTextDungeon.enums;

/**
 * Created by Paul Dennis on 8/8/2017.
 */
public enum Direction {
    NORTH, EAST, SOUTH, WEST, UP, DOWN, PORTAL;

    public Direction getOpposite () {
        switch (this) {
            case NORTH:
                return SOUTH;
            case EAST:
                return WEST;
            case SOUTH:
                return NORTH;
            case WEST:
                return EAST;
            case UP:
                return DOWN;
            case DOWN:
                return UP;
            case PORTAL:
                return PORTAL;
            default:
                return null;
        }
    }

    @Override
    public String toString () {
        switch (this) {
            case NORTH:
                return "North";
            case EAST:
                return "East";
            case SOUTH:
                return "South";
            case WEST:
                return "West";
            case UP:
                return "Upstairs";
            case DOWN:
                return "Downstairs";
                //And all through the house
            case PORTAL:
                return "Portal";
            default:
                return null;
        }
    }

}
