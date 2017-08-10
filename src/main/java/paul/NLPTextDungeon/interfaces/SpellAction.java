package paul.NLPTextDungeon.interfaces;


import paul.NLPTextDungeon.entities.Hero;

/**
 * Created by Paul Dennis on 8/8/2017.
 */
public interface SpellAction<T> {
    void doAction(Hero hero);
}
