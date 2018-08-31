package paul.TextQuest.enums;

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
