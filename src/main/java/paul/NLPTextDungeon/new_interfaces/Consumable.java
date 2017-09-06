package paul.NLPTextDungeon.new_interfaces;

import paul.NLPTextDungeon.entities.Hero;

/**
 * Created by Paul Dennis on 9/5/2017.
 */
public interface Consumable {

    public void consume (Hero hero);
    public void onConsume (Hero hero);
}
