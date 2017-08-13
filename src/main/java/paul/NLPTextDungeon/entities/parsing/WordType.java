package paul.NLPTextDungeon.entities.parsing;

/**
 * Created by Paul Dennis on 8/10/2017.
 */
public enum WordType {
    CONCEPT, VOID_ACTION, PARAM_ACTION, DIRECTION, SPEAKING;


    public static WordType getTypeFromFileAnnotation (String annotation) {
        switch (annotation) {
            case "@VoidAction":
                return VOID_ACTION;
            case "@ParamAction":
                return PARAM_ACTION;
            case "@Concept":
                return CONCEPT;
            case "@Direction":
                return DIRECTION;
            case "@Speaking":
                return SPEAKING;
            default:
                throw new AssertionError();
        }
    }
}