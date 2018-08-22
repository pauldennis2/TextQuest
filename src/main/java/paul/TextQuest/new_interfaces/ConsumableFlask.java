package paul.TextQuest.new_interfaces;

import paul.TextQuest.entities.BackpackItem;
import paul.TextQuest.entities.Hero;

/**
 * Created by Paul Dennis on 9/5/2017.
 */
public class ConsumableFlask extends BackpackItem {

    String onConsume;

    public void consume (Hero hero) {
        hero.removeItem(this.getName());

    }

    public void onConsume (Hero hero) {
        //consumeActions.do(onConsume)
    }
}
