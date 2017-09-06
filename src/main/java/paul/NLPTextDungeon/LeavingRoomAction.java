package paul.NLPTextDungeon;

import paul.NLPTextDungeon.enums.Direction;

import java.util.Map;

/**
 * Created by Paul Dennis on 9/5/2017.
 */
public class LeavingRoomAction {

    private String action;
    private boolean stops;

    public LeavingRoomAction () {

    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean isStops() {
        return stops;
    }

    public void setStops(boolean stops) {
        this.stops = stops;
    }
}
