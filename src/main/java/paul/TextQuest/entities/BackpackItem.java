package paul.TextQuest.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import paul.TextQuest.interfaces.Detailable;

/**
 * Created by Paul Dennis on 8/8/2017.
 */

@JsonTypeInfo(
		defaultImpl = BackpackItem.class,
        use = JsonTypeInfo.Id.NAME, 
        include = JsonTypeInfo.As.PROPERTY,
        property = "_type")//,
        //visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Note.class, name = Note._TYPE),
        @JsonSubTypes.Type(value = EquippableItem.class, name = EquippableItem._TYPE)
})

@JsonInclude(Include.NON_NULL)
public class BackpackItem extends DungeonEntity implements Detailable{
    
    private Boolean isQuestItem;
    private int value;
    
    private String onPickup;
    private String onDrop;
    private boolean undroppable;
    
    private int numCharges;
    private String onUse;
    
    public static final String CONSUMES = "!CONSUMES"; //Pronounced like consommés 

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
    	
    	this.numCharges = other.numCharges;
    	this.onUse = other.onUse;
    	this.description = other.description;
    }
    
    public BackpackItem copy () {
    	return new BackpackItem(this);
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
	
	public String toDetailedString () {
		String response = name;
		//description, consume, quest item, value
		if (description != null) {
			response += " - " + description;
		}
		if (onUse != null && onUse.contains(CONSUMES)) {
			response += ", (Consumable)";
		}
		if (numCharges > 0) {
			response += ", Charges: " + numCharges;
		}
		if (isQuestItem != null && isQuestItem) {
			response += ", Quest Item";
		}
		if (value != 0) {
			response += ", Value - " + value;
		}
		return response;
	}

	@Override
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

	public int getNumCharges() {
		return numCharges;
	}

	@JsonInclude(Include.NON_DEFAULT)
	public void setNumCharges(int numCharges) {
		this.numCharges = numCharges;
	}

	public String getOnUse() {
		return onUse;
	}

	public void setOnUse(String onUse) {
		this.onUse = onUse;
	}    
}
