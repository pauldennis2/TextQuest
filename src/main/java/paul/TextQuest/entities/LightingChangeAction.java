package paul.TextQuest.entities;

import java.util.Map;

import paul.TextQuest.enums.LightingLevel;

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
