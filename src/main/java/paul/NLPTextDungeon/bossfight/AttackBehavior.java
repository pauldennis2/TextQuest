package paul.NLPTextDungeon.bossfight;

import paul.NLPTextDungeon.entities.Hero;

/**
 * Created by Paul Dennis on 8/13/2017.
 */
public class AttackBehavior {

    private String name;
    private String solution;
    private int damage;

    private transient int numTimesDone;
    private transient boolean solved;

    private String actionDescription;
    private String avoidDescription;

    public AttackBehavior () {
        numTimesDone = 0;
    }

    public AttackBehavior(String name, String solution, int damage, String actionDescription, String avoidDescription) {
        numTimesDone = 0;
        this.name = name;
        this.solution = solution;
        this.damage = damage;
        this.actionDescription = actionDescription;
        this.avoidDescription = avoidDescription;
    }

    public void doBehavior (Hero hero) {
        System.out.println(actionDescription);
        if (solved) {
            System.out.println(avoidDescription);
        } else {
            hero.takeDamage(damage);
            System.out.println("You took " + damage + " damage.");
            if (numTimesDone >= 1) {
                System.out.println("What would you like to do next time?");
                if (new java.util.Scanner(System.in).nextLine().equals(solution)) {
                    solved = true;
                }
            }
        }
        numTimesDone++;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public String getActionDescription() {
        return actionDescription;
    }

    public void setActionDescription(String actionDescription) {
        this.actionDescription = actionDescription;
    }

    public String getAvoidDescription() {
        return avoidDescription;
    }

    public void setAvoidDescription(String avoidDescription) {
        this.avoidDescription = avoidDescription;
    }

    /*
    public void doBehavior (Hero hero) {
        actionMap.get(name).doAttack(hero, damage, solved);
        numTimesDone++;
        if (numTimesDone > 1 && !solved) {
            System.out.println("What would you like to do next time?");
            Scanner scanner = new Scanner(System.in);
            if (scanner.nextLine().equals(solution)) {
                solved = true;
            }
        }
    }


    private static Map<String, AttackBehaviorAction> actionMap;
    private static boolean init = false;

    public static void initActionMap () {
        actionMap = new HashMap<>();
        actionMap.put("pee", (hero, damage, solved) -> {
            System.out.println("Pihop-pi just peed all over the floor.");
            if (solved) {
                System.out.println("But you jumped to avoid it.");
            } else {
                System.out.println("You take " + damage + " acid damage.");
                hero.takeDamage(damage);
            }
        });
    }*/
}
