package paul.NLPTextDungeon.bossfight;

import paul.NLPTextDungeon.enums.ActionResponseTiming;

/**
 * Created by Paul Dennis on 8/14/2017.
 */
public class Solution {

    String actionWord;
    ActionResponseTiming timing;

    public Solution () {

    }

    public Solution(String actionWord, ActionResponseTiming timing) {
        this.actionWord = actionWord;
        this.timing = timing;
    }

    public String getActionWord() {
        return actionWord;
    }

    public void setActionWord(String actionWord) {
        this.actionWord = actionWord;
    }

    public ActionResponseTiming getTiming() {
        return timing;
    }

    public void setTiming(ActionResponseTiming timing) {
        this.timing = timing;
    }
}
