package paul.NLPTextDungeon.entities;

import paul.NLPTextDungeon.enums.LightingLevel;

import java.util.Map;

/**
 * Created by Paul Dennis on 9/5/2017.
 */
public class LightingChangeAction {

    private Map<LightingLevel, String> actionMap;

    public LightingChangeAction () {

    }

    public Map<LightingLevel, String> getActionMap() {
        return actionMap;
    }

    public void setActionMap(Map<LightingLevel, String> actionMap) {
        this.actionMap = actionMap;
    }
}
