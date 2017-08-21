package paul.NLPTextDungeon.entities;

/**
 * Created by Paul Dennis on 8/8/2017.
 */
public class BackpackItem extends DungeonRoomEntity {

    private String name;
    private boolean isQuestItem;
    private int value;
    private String pickupAction;

    public BackpackItem () {

    }

    public BackpackItem (String name) {
        this.name = name;
        isQuestItem = false;
    }

    public BackpackItem (String name, boolean quest) {
        isQuestItem = quest;
        this.name = name;
    }

    public BackpackItem (String name, int value) {
        this.name = name;
        this.value = value;
        isQuestItem = false;
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
        return name + " (" + value + ")";
    }
}
