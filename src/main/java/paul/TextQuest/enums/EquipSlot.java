package paul.TextQuest.enums;

import paul.TextQuest.utils.StringUtils;

/**
 * Created by Paul Dennis on 9/5/2017.
 */
public enum EquipSlot {
    HELM, WEAPON, OFFHAND, ARMOR, RING, AMULET, BOOTS, GLOVES;
	
	@Override
	public String toString () {
		return StringUtils.capitalize(this.name());
	}
}
