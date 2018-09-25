package paul.TextQuest.entities;

/**
 * Created by pauldennis on 8/21/17.
 */
public class Note extends BackpackItem {

	public static final String _TYPE = "note";
	
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
