package paul.TextQuest.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import paul.TextQuest.enums.EquipSlot;

/**
 * Created by Paul Dennis on 9/5/2017.
 */

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "_type", visible = true)
@JsonSubTypes({ @Type(value = EquippableItem.class, name = EquippableItem._TYPE) })
public class EquippableItem extends BackpackItem {
	
	public static final String _TYPE = "equipment";

    private EquipSlot slot;
    
    private int mightMod;
    private int magicMod;
    private int sneakMod;
    private int defenseMod;

    private String onEquip;
    private String onUnequip;
    
    public EquippableItem () {
    	
    }
    
    public EquippableItem (String name, EquipSlot slot) {
    	super(name);
    	this.slot = slot;
    }

	public EquipSlot getSlot() {
		return slot;
	}

	@JsonProperty(required = true)
	public void setSlot(EquipSlot slot) {
		this.slot = slot;
	}

	public int getMightMod() {
		return mightMod;
	}

	@JsonInclude(Include.NON_DEFAULT)
	public void setMightMod(int mightMod) {
		this.mightMod = mightMod;
	}

	public int getMagicMod() {
		return magicMod;
	}

	@JsonInclude(Include.NON_DEFAULT)
	public void setMagicMod(int magicMod) {
		this.magicMod = magicMod;
	}

	public int getSneakMod() {
		return sneakMod;
	}

	@JsonInclude(Include.NON_DEFAULT)
	public void setSneakMod(int sneakMod) {
		this.sneakMod = sneakMod;
	}

	public int getDefenseMod() {
		return defenseMod;
	}

	@JsonInclude(Include.NON_DEFAULT)
	public void setDefenseMod(int defenseMod) {
		this.defenseMod = defenseMod;
	}

	public String getOnEquip() {
		return onEquip;
	}

	public void setOnEquip(String onEquip) {
		this.onEquip = onEquip;
	}

	public String getOnUnequip() {
		return onUnequip;
	}

	public void setOnUnequip(String onUnequip) {
		this.onUnequip = onUnequip;
	}
}
