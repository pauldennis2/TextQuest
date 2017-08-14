package paul.NLPTextDungeon.parsing;

import java.util.Set;

/**
 * Created by Paul Dennis on 8/13/2017.
 */
@Deprecated
public class ItemWordGroup extends WordGroup {

    String itemName;

    public ItemWordGroup (String coreWord, Set<String> relatedWords, String itemName) {
        super(coreWord, relatedWords, WordType.ITEM);
        this.itemName = itemName;
    }
}
