package paul.TextQuest.parsing;

/**
 * Created by pauldennis on 8/19/17.
 */
public enum SpellType {
    COMBAT_ONLY, NONCOMBAT_ONLY, BOTH;

    public static SpellType getTypeFromFileAnnotation (String annotation) {
        switch (annotation) {
            case "@CombatOnly":
                return COMBAT_ONLY;
            case "@NonCombatOnly":
                return NONCOMBAT_ONLY;
            case "@Both":
                return BOTH;
            default:
                throw new AssertionError();
        }
    }
}
