package paul.NLPTextDungeon.entities;

/**
 * Created by Paul Dennis on 9/5/2017.
 */
public class LightingChangeAction {

    private double requiredLevel;
    private String action;

    public LightingChangeAction () {

    }

    public double getRequiredLevel() {
        return requiredLevel;
    }

    public void setRequiredLevel(double requiredLevel) {
        this.requiredLevel = requiredLevel;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
