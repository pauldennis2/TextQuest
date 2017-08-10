package paul.NLPTextDungeon.entities;

import java.util.Random;

/**
 * Created by Paul Dennis on 8/8/2017.
 */
public class Monster {

    private int health;
    private int might;
    private int exp;
    private String name;
    private boolean isBoss;

    public static final int MAX_RANDOM_HEALTH = 10;
    public static final int MAX_RANDOM_STR = 4;

    public static final int MAX_BOSS_HEALTH = 30;
    public static final int MAX_BOSS_STR = 8;

    public static final int BOSS_XP = 50;
    public static final int MONSTER_XP = 3;

    public Monster (boolean isBoss, Random random) {
        this.isBoss = isBoss;

        if (!isBoss) {
            might = random.nextInt(MAX_RANDOM_STR);
            health = random.nextInt(MAX_RANDOM_HEALTH);
            if (health == 0) {
                health = 1;
            }
            exp = health + 2 * might + MONSTER_XP;
            name = getRandomMonsterName();
        } else {
            might = random.nextInt(MAX_BOSS_STR);
            health = random.nextInt(MAX_BOSS_HEALTH);
            exp = health + 2 * might + BOSS_XP;
            name = getRandomBossName();
        }
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
        return exp;
    }

    public String getName() {
        return name;
    }

    public boolean isBoss() {
        return isBoss;
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
