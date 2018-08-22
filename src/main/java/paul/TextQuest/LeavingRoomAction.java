package paul.TextQuest;

import java.util.Map;

import paul.TextQuest.enums.Direction;

/**
 * Created by Paul Dennis on 9/5/2017.
 */
public class LeavingRoomAction {

    private String action;
    private boolean stops; //Stops the hero from leaving the room
    private boolean doOnce; //Perform the action only once. default = true

    public LeavingRoomAction () {
        doOnce = true;
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

    public boolean isDoOnce() {
        return doOnce;
    }

    public void setDoOnce(boolean doOnce) {
        this.doOnce = doOnce;
    }
}
