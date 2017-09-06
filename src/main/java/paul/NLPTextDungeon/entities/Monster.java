package paul.NLPTextDungeon.entities;

import paul.NLPTextDungeon.enums.BehaviorTiming;
import paul.NLPTextDungeon.interfaces.VoidAction;

import java.util.Map;

/**
 * Created by Paul Dennis on 8/8/2017.
 */
public class Monster extends DungeonRoomEntity {

    private int health;
    private int might;
    private int defense;
    private String name;

    private transient int disabledForRounds = 0;

    private static Map<String, VoidAction> actionMap;

    private Map<String, CombatBehavior> abilities;
    private Map<BehaviorTiming, String> behavior;

    public Monster () {
        name = "Biff the Understudy";
    }

    public Monster(int health, int might, String name) {
        this.health = health;
        this.might = might;
        this.name = name;
    }

    public void disable (int rounds) {
        disabledForRounds = rounds;
    }

    public boolean isDisabled () {
        return disabledForRounds > 0;
    }

    public void nextRound () {
        if (disabledForRounds > 0) {
            disabledForRounds--;
        }
    }

    public int takeDamage (int damageAmt) {
        damageAmt -= (defense / 5) * 2;
        health -= damageAmt;
        if (health < 0) {
            health = 0;
        }
        return damageAmt;
    }

    @Override
    public String toString () {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public int getMight() {
        return might;
    }

    public void setMight (int might) {
        this.might = might;
    }

    public int getExp() {
        return health + 2 * might + 3;
    }

    public String getName() {
        return name;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public Map<BehaviorTiming, String> getBehavior() {
        return behavior;
    }

    public void setBehavior(Map<BehaviorTiming, String> behavior) {
        this.behavior = behavior;
    }

    public Map<String, CombatBehavior> getAbilities() {
        return abilities;
    }

    public void setAbilities(Map<String, CombatBehavior> abilities) {
        this.abilities = abilities;
    }
}
