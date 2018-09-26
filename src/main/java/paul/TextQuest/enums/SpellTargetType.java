/**
 * @author Paul Dennis
 * Sep 17, 2018
 */
package paul.TextQuest.enums;

public enum SpellTargetType {
	SELF, ALL_ENEMIES, RANDOM_ENEMY, NONE;
	
	public static SpellTargetType getType (String input) {
		for (SpellTargetType type : SpellTargetType.values()) {
			if (type.toString().equalsIgnoreCase(input)) {
				return type;
			}
		}
		return null;
	}
}
