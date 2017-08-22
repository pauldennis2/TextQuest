package paul.NLPTextDungeon.entities;

import java.util.Random;

/**
 * Created by Paul Dennis on 8/8/2017.
 */
public class Monster extends DungeonRoomEntity {

    private int health;
    private int might;
    private int defence;
    private String name;
    private double accuracyAdjustment;

    private transient int disabledForRounds = 0;

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
        damageAmt -= (defence / 5) * 2;
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

    public int getDefence() {
        return defence;
    }

    public void setDefence(int defence) {
        this.defence = defence;
    }

    public double getAccuracyAdjustment() {
        return accuracyAdjustment;
    }

    public void setAccuracyAdjustment(double accuracyAdjustment) {
        this.accuracyAdjustment = accuracyAdjustment;
    }
}
