package paul.NLPTextDungeon.new_interfaces;

import paul.NLPTextDungeon.entities.BackpackItem;
import paul.NLPTextDungeon.entities.Hero;

/**
 * Created by Paul Dennis on 9/5/2017.
 */
public class ConsumableFlash extends BackpackItem {

    String onConsume;

    public void consume (Hero hero) {
        hero.removeItem(this.getName());

    }

    public void onConsume (Hero hero) {
        //consumeActions.do(onConsume)
    }
}
