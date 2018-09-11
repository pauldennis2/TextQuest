package paul.TextQuest.bossfight;

import paul.TextQuest.enums.ActionResponseTiming;

/**
 * Created by Paul Dennis on 8/14/2017.
 */
@Deprecated
public class BehaviorResponse {

    private String causingAction; //The action that causes this response
    private String responseAction; //The action we are going to take in response to the causing action
    private ActionResponseTiming timing; //The timing of when we will respond

    public BehaviorResponse() {

    }

    public BehaviorResponse(String causingAction, String responseAction, ActionResponseTiming timing) {
        this.causingAction = causingAction;
        this.responseAction = responseAction;
        this.timing = timing;
    }

    @Override
    public String toString () {
        return responseAction + " " + timing.toString().toLowerCase() + " " + causingAction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BehaviorResponse that = (BehaviorResponse) o;

        if (!causingAction.equals(that.causingAction)) return false;
        if (!responseAction.equals(that.responseAction)) return false;
        return timing == that.timing;
    }

    @Override
    public int hashCode() {
        int result = causingAction.hashCode();
        result = 31 * result + responseAction.hashCode();
        result = 31 * result + timing.hashCode();
        return result;
    }

    public String getCausingAction() {
        return causingAction;
    }

    public void setCausingAction(String causingAction) {
        this.causingAction = causingAction;
    }

    public String getResponseAction() {
        return responseAction;
    }

    public void setResponseAction(String responseAction) {
        this.responseAction = responseAction;
    }

    public ActionResponseTiming getTiming() {
        return timing;
    }

    public void setTiming(ActionResponseTiming timing) {
        this.timing = timing;
    }
}
