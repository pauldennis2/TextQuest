package paul.TextQuest.enums;

import java.util.List;

/**
 * Created by Paul Dennis on 8/8/2017.
 */
public enum Direction {
    NORTH, EAST, SOUTH, WEST, UP, DOWN, PORTAL,
    ALL, //Represents ALL directions (i.e. an obstacle that blocks travel in all directions)
    NONE;//Represents no direction (i.e. an obstacle that blocks travel in no direction)

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
    
    /*
     * Quick method to tell if this direction is one of the cardinal directions
     * (N, E, S, W).
     */
    public boolean isCardinal () {
    	switch (this) {
        case NORTH:
            return true;
        case EAST:
            return true;
        case SOUTH:
            return true;
        case WEST:
            return true;
        case UP:
            return false;
        case DOWN:
            return false;
        case PORTAL:
            return false;
        default:
            return false;
    	}
    }
    
    public static boolean containsCardinal (List<Direction> directions) {
    	for (Direction direction : directions) {
    		if (direction.isCardinal()) {
    			return true;
    		}
    	}
		return false;
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
            case ALL:
                return "All";
            case NONE:
                return "None";
            default:
                return null;
        }
    }
    
    public static Direction getDirectionFromString (String input) {
    	input = input.toLowerCase();
    	for (Direction direction : Direction.values()) {
    		if (direction.toString().toLowerCase().equals(input)) {
    			return direction;
    		}
    	}
    	return null;
    }

}
