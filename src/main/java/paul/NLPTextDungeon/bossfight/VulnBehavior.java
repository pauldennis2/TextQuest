package paul.NLPTextDungeon.bossfight;

import paul.NLPTextDungeon.entities.Hero;
import paul.NLPTextDungeon.enums.NumberActionType;
import paul.NLPTextDungeon.enums.NumberRuleType;
import paul.NLPTextDungeon.utils.SafeNumScanner;

import java.util.*;

/**
 * Created by Paul Dennis on 8/13/2017.
 */
public class VulnBehavior {

    private String name;
    private String params;
    private NumberActionType actionType;
    private NumberRuleType rule;
    private int failureDamage;

    private String numberChoosingBehaviorDescription;
    private String solutionBehaviorIndication;

    private transient Random random;

    public VulnBehavior () {
        random = new Random();
    }

    public VulnBehavior(String name, String params, NumberActionType actionType, NumberRuleType rule, int failureDamage) {
        random = new Random();

        this.name = name;
        this.params = params;
        this.actionType = actionType;
        this.rule = rule;
        this.failureDamage = failureDamage;
    }

    public void demoBehavior () {
        int firstRandom = random.nextInt(10);
        int secondRandom = random.nextInt(10);

        //Demonstrate the rule
        System.out.println(numberChoosingBehaviorDescription + " " + firstRandom);
        System.out.println(numberChoosingBehaviorDescription + " " + secondRandom);

        int solution = getSolution(firstRandom, secondRandom);
        System.out.println(solutionBehaviorIndication + " : " + solution);
    }

    public void doBehavior (Hero hero) {
        //if (params.equals("2random")
        int firstRandom = random.nextInt(10);
        int secondRandom = random.nextInt(10);

        System.out.println(numberChoosingBehaviorDescription + " " + firstRandom);
        System.out.println(numberChoosingBehaviorDescription + " " + secondRandom);

        System.out.println("Jump on which platform? Hint: sum mod 5");
        SafeNumScanner scanner = new SafeNumScanner(System.in);
        int userNumber = scanner.getSafeNum(0, 9);
        if (checkRule(firstRandom,secondRandom, userNumber)) {
            System.out.println("Cool, you got it right.");
            System.out.println("Boss nominally takes 10 damage.");
        } else {
            System.out.println("WRONG!");
            hero.takeDamage(failureDamage);
        }

    }

    public static void main(String[] args) {
        VulnBehavior hop = new VulnBehavior("hop", "2random", NumberActionType.SUM, NumberRuleType.MOD5, 10);
        hop.numberChoosingBehaviorDescription = "Pihop-pi jumps on platform ";
        hop.solutionBehaviorIndication = "One of the platforms starts glowing";

        System.out.println("First demo");
        hop.demoBehavior();

        System.out.println("Second demo");
        hop.demoBehavior();

        Hero hero = new Hero();

        hop.doBehavior(hero);
        hop.doBehavior(hero);
        hop.doBehavior(hero);
    }

    private boolean checkRule (int firstInput, int secondInput, int solution) {
        return getSolution(firstInput, secondInput) == solution;
    }

    private int getSolution (int firstInput, int secondInput) {
        int result;
        switch (actionType) {
            case SUM:
                result = firstInput + secondInput;
                break;
            case PRODUCT:
                result = firstInput * secondInput;
                break;
            case DIFFERENCE:
                result = Math.abs(firstInput - secondInput);
                break;
            default:
                throw new AssertionError();
        }
        switch (rule) {
            case MOD5:
                return result % 5;
            default:
                throw new AssertionError();
        }
    }

}
