package paul.NLPTextDungeon.entities;

/**
 * Created by pauldennis on 8/21/17.
 */
public class Note extends BackpackItem {

    private String text;

    public Note () {

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
