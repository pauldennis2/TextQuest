package enums;

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
}
