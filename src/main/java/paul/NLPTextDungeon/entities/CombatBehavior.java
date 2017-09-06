package paul.NLPTextDungeon.entities;

/**
 * Created by Paul Dennis on 9/5/2017.
 */
public abstract class CombatBehavior {

    private boolean before;
    private String action;

    protected CombatBehavior () {

    }


    public boolean isBefore() {
        return before;
    }

    public void setBefore(boolean before) {
        this.before = before;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
