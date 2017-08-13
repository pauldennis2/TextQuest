package paul.NLPTextDungeon.entities;

import java.util.Random;

/**
 * Created by Paul Dennis on 8/8/2017.
 */
public class Monster {

    private int health;
    private int might;
    private String name;

    public Monster () {
        name = "Biff the Understudy";
    }

    public Monster(int health, int might, String name) {
        this.health = health;
        this.might = might;
        this.name = name;
    }

    public void takeDamage (int damageAmt) {
        health -= damageAmt;
        if (health < 0) {
            health = 0;
        }
    }

    @Override
    public String toString () {
        String response = name;
        if (might < 2) {
            response += ", weak of claw";
        } else {
            response += ", strong of claw";
        }
        if (health < 5) {
            response += ", weak of shell";
        } else {
            response += ", strong of shell";
        }
        return response;
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
}
