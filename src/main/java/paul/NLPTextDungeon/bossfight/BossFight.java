package paul.NLPTextDungeon.bossfight;

import paul.NLPTextDungeon.entities.Hero;

/**
 * Created by Paul Dennis on 8/13/2017.
 */
public class BossFight {

    String name;
    int health;
    AttackBehavior attackBehavior;
    VulnBehavior vulnBehavior;

    public BossFight(String name, int health, AttackBehavior attackBehavior, VulnBehavior vulnBehavior) {
        this.name = name;
        this.health = health;
        this.attackBehavior = attackBehavior;
        this.vulnBehavior = vulnBehavior;
    }

    public static void main(String[] args) {
        //AttackBehavior pee = new AttackBehavior("pee", "jump", 1);

        BossFight fight = new BossFight("Pihop-pi", 30, null, null);
        Hero hero = new Hero();

    }
}
