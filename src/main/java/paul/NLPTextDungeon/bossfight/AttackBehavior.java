package paul.NLPTextDungeon.bossfight;

import paul.NLPTextDungeon.entities.Hero;
import paul.NLPTextDungeon.enums.ActionResponseTiming;
import paul.NLPTextDungeon.parsing.StatementAnalysis;
import paul.NLPTextDungeon.parsing.StatementAnalyzer;
import paul.NLPTextDungeon.parsing.WordType;

import java.util.List;
import java.util.Scanner;

/**
 * Created by Paul Dennis on 8/13/2017.
 */
public class AttackBehavior {

    private String name;
    private BehaviorResponse solution; //Expresses not just "a" response but "THE" response (solution) to the attack

    private String actionDescription;
    private String avoidDescription;

    private int damage;

    private transient int numTimesDone;
    private transient boolean solved;
    private transient BehaviorResponse attemptedSolution;

    public AttackBehavior () {
        numTimesDone = 0;
    }

    public AttackBehavior(String name, BehaviorResponse solution, String actionDescription, String avoidDescription, int damage) {
        this.name = name;
        this.solution = solution;
        this.actionDescription = actionDescription;
        this.avoidDescription = avoidDescription;
        this.damage = damage;
    }

    public void doBehavior (Hero hero) {
        System.out.println(actionDescription);
        if (solved) {
            System.out.println(avoidDescription);
        } else if (attemptedSolution != null) {
            System.out.println("Attempting: " + attemptedSolution);
            if (solution.equals(attemptedSolution)) {
                System.out.println(avoidDescription);
                solved = true;
            } else {
                System.out.println("It doesn't seem to have worked.");
                attemptedSolution = null;
                doBehavior(hero);
            }
        } else {
            hero.takeDamage(damage);
            System.out.println("You took " + damage + " damage.");
            if (numTimesDone >= 1) {
                System.out.println("What would you like to do next time?");
                String response = new Scanner(System.in).nextLine();
                StatementAnalyzer statementAnalyzer = new StatementAnalyzer();
                StatementAnalysis analysis = statementAnalyzer.analyzeStatement(response);
                List<String> timingWords = analysis.getTokenMatchMap().get(WordType.TIMING);
                List<String> actionWords = analysis.getTokenMatchMap().get(WordType.VOID_ACTION);
                if (timingWords.size() > 0 && actionWords.size() > 0) {
                    //Build a solution object
                    attemptedSolution = new BehaviorResponse(this.name,
                            actionWords.get(0), ActionResponseTiming.getFromString(timingWords.get(0)));
                } else {
                    System.out.println("Could not find an actionable solution. Requires a timing and an action word.");
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

    public BehaviorResponse getSolution() {
        return solution;
    }

    public void setSolution(BehaviorResponse solution) {
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
