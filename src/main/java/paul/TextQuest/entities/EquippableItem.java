package paul.TextQuest.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import paul.TextQuest.enums.EquipSlot;
import paul.TextQuest.utils.StringUtils;

/**
 * Created by Paul Dennis on 9/5/2017.
 */

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "_type", visible = true)
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
    	super();
    }
    
    public EquippableItem (EquippableItem other) {
    	super(other);
    	
    	this.slot = other.slot;
    	this.mightMod = other.mightMod;
    	this.magicMod = other.magicMod;
    	this.sneakMod = other.sneakMod;
    	this.defenseMod = other.defenseMod;
    	
    	this.onEquip = other.onEquip;
    	this.onUnequip = other.onUnequip;
    }
    
    public EquippableItem (String name, EquipSlot slot) {
    	super(name);
    	this.slot = slot;
    }
    
    @Override
    public String toDetailedString () {
    	String response = super.toDetailedString();
    	response += ", Slot: " + slot;
    	if (mightMod != 0) {
    		response += ", Might " + StringUtils.appendModifierWithSign(mightMod);
    	}
    	if (magicMod != 0) {
    		response += ", Magic " + StringUtils.appendModifierWithSign(magicMod);
    	}
    	if (sneakMod != 0) {
    		response += ", Sneak " + StringUtils.appendModifierWithSign(sneakMod);
    	}
    	if (defenseMod != 0) {
    		response += ", Defense " + StringUtils.appendModifierWithSign(defenseMod);
    	}
    	return response;
    }

	@Override
    public EquippableItem copy () {
    	return new EquippableItem(this);
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
