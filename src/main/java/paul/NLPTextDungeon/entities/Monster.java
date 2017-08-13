package paul.NLPTextDungeon.entities;

import java.util.Random;

/**
 * Created by Paul Dennis on 8/8/2017.
 */
public class Monster {

    private int health;
    private int might;
    private String name;

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


    public static final String[] NAME_COLORS = {"Red", "Blue", "Yellow", "Green"};
    public static final String[] NAME_DESCRIPTION = {"Knob", "Blob", "Hob", "Meanie", "Under", "Uber", "Backwards"};
    public static final String[] MONSTER_TYPE = {"Goblin", "Orcar", "Git", "Ronot", "Fovok"};
    public static String getRandomMonsterName () {
        Random random = new Random();
        boolean color = random.nextBoolean();
        String response = "";
        if (color) {
            response += NAME_COLORS[random.nextInt(NAME_COLORS.length)];
        }
        response += NAME_DESCRIPTION[random.nextInt(NAME_DESCRIPTION.length)];
        response += MONSTER_TYPE[random.nextInt(MONSTER_TYPE.length)];
        return response;
    }

    public static final String[] BOSS_NAMES = {"Tim", "Bob", "Joe", "Abe", "Ada", "Amy", "Dan", "Ned", "Sal"};
    public static String getRandomBossName () {
        Random random = new Random();
        return BOSS_NAMES[random.nextInt(BOSS_NAMES.length)];
    }
}
