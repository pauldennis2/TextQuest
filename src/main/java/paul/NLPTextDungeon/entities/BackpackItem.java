package paul.NLPTextDungeon.entities;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Created by Paul Dennis on 8/8/2017.
 */



@JsonTypeInfo(defaultImpl=BackpackItem.class,
        use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Note.class, name = "note")
})
public class BackpackItem extends DungeonRoomEntity {

    private String name;
    private boolean isQuestItem;
    private int value;
    private String pickupAction;
    private boolean darklight; //Item can only be seen in the dark

    public BackpackItem () {

    }

    public BackpackItem (String name) {
        this.name = name;
        isQuestItem = false;
    }

    public BackpackItem(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isQuestItem() {
        return isQuestItem;
    }

    public void setQuestItem(boolean questItem) {
        isQuestItem = questItem;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getPickupAction() {
        return pickupAction;
    }

    public void setPickupAction(String pickupAction) {
        this.pickupAction = pickupAction;
    }

    public boolean hasPickupAction () {
        return pickupAction != null;
    }

    @Override
    public String toString () {
        return name;
    }

    public static final double DEFAULT_VISIBILITY_THRESHHOLD = 0.6;
    public boolean isVisible (double lighting) {
        if (darklight) {
            return lighting == 0.0;
        }
        return lighting >= DEFAULT_VISIBILITY_THRESHHOLD;
    }

    public boolean isDarklight() {
        return darklight;
    }

    public void setDarklight(boolean darklight) {
        this.darklight = darklight;
    }
}
