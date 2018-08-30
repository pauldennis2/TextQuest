package paul.TextQuest.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Default;

import paul.TextQuest.new_interfaces.EquipableItem;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Created by Paul Dennis on 8/8/2017.
 */

@JsonTypeInfo(
		defaultImpl = BackpackItem.class,
        use = JsonTypeInfo.Id.NAME, 
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")//,
        //visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Note.class, name = "note"),
        //@JsonSubTypes.Type(value = EquipableItem.class, name = EquipableItem._TYPE)
})
@JsonInclude(Include.NON_NULL)
public class BackpackItem extends DungeonRoomEntity {

    private String name;
    
    private Boolean isQuestItem;
    private int value;
    
    private String onPickup;
    private String onDrop;
    private boolean darklight; //Item can only be seen in the dark
    private boolean undroppable;

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
    
    public BackpackItem (BackpackItem other) {
    	this.name = other.name;
    	this.isQuestItem = other.isQuestItem;
    	this.value = other.value;
    	this.onPickup = other.onPickup;
    	this.onDrop = other.onDrop;
    	this.darklight = other.darklight;
    	this.undroppable = other.undroppable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    
    public Boolean isQuestItem() {
        return isQuestItem;
    }

    @JsonInclude(Include.NON_DEFAULT)
    public void setQuestItem(Boolean questItem) {
        isQuestItem = questItem;
    }

    public int getValue() {
        return value;
    }

    @JsonInclude(Include.NON_DEFAULT)
    public void setValue(int value) {
        this.value = value;
    }

    public String getOnPickup() {
        return onPickup;
    }

    public void setOnPickup(String onPickup) {
        this.onPickup = onPickup;
    }

    public boolean hasPickupAction () {
        return onPickup != null;
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

    public boolean isDarklight () {
        return darklight;
    }

    @JsonInclude(Include.NON_DEFAULT)
    public void setDarklight (boolean darklight) {
        this.darklight = darklight;
    }
    
    public boolean isUndroppable () {
    	return undroppable;
    }
    
    @JsonInclude(Include.NON_DEFAULT)
    public void setUndroppable (boolean undroppable) {
    	this.undroppable = undroppable;
    }
    
    public String getOnDrop () {
    	return onDrop;
    }
    
    public void setOnDrop (String onDrop) {
    	this.onDrop = onDrop;
    }
}
