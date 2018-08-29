package paul.TextQuest.new_interfaces;

import paul.TextQuest.entities.BackpackItem;
import paul.TextQuest.entities.Hero;

/**
 * Created by Paul Dennis on 9/5/2017.
 */
public class Consumable extends BackpackItem {

    String onConsume;
    int uses; //number of uses remaining

    public void consume (Hero hero) {
        hero.removeItem(this.getName());

    }
}
