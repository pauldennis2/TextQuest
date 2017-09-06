package paul.NLPTextDungeon;

import paul.NLPTextDungeon.enums.Direction;

/**
 * Created by Paul Dennis on 9/5/2017.
 */
public class LeavingRoomAction {

    private Direction direction;//not required
    private String action;

    public LeavingRoomAction () {

    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
