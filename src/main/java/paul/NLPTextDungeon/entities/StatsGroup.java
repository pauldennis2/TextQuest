package paul.NLPTextDungeon.entities;

/**
 * Created by pauldennis on 8/18/17.
 */
public class StatsGroup {
    //private int health;
    private int maxHealth;

    //private int might;
    private int maxMight;

    private int magic;
    private int maxMagic;

    private int sneak;
    private int maxSneak;

    private int defense;
    private int maxDefense;

    private int numSpellsAvailable;
    private int maxSpellsPerDay;

    private BoundedStat<Integer> health;
    private BoundedStat<Integer> might;


    public StatsGroup () {
        health = new BoundedStat<>(50, 0, 50);
        might = new BoundedStat<>(5, 2, 8);
    }

    public void setHealth (int health) {
        this.health.set(health);
    }
    public void setMight (int might) {
        this.might.set(might);
    }

    public static void main(String[] args) {
        StatsGroup test = new StatsGroup();
        test.setHealth(-50);
        System.out.println("Health = " + test.health);
        test.setHealth(52);
        System.out.println("Health = " + test.health);
        test.setHealth(25);
        System.out.println("health = " + test.health);

        test.setMight(0);
        System.out.println("Might = " + test.might);
        test.setMight(10);
        System.out.println("Might = " + test.might);
        test.setMight(4);
        System.out.println("might = " + test.might);
    }
}

class BoundedStat <T extends Comparable<T>> {

    private String stat;
    private T value;
    private T min;
    private T max;

    public BoundedStat(T value, T min, T max) {
        this.value = value;
        this.min = min;
        this.max = max;
    }

    public void set (T t) {
        //If the new value is less than min, set value to min
        if (t.compareTo(min) < 0) {
            value = min;
        //If the new value is greater than max, set value to max
        } else if (t.compareTo(max) > 0) {
            value = max;
        //Otherwise the new value is in the range, so just set it.
        } else {
            value = t;
        }
    }

    public String toString () {
        return value.toString();
    }
}