package paul.NLPTextDungeon.bossfight;

import paul.NLPTextDungeon.utils.BufferedOutputTextStream;
import paul.NLPTextDungeon.entities.Hero;
import paul.NLPTextDungeon.enums.NumberActionType;
import paul.NLPTextDungeon.enums.NumberRuleType;

import java.util.*;

/**
 * Created by Paul Dennis on 8/13/2017.
 */
public class VulnerableBehavior {

    private String name;
    private String params;
    private NumberActionType actionType;
    private NumberRuleType ruleType;
    private int failureDamage;

    private String numberChoosingBehaviorDescription;
    private String solutionBehaviorIndication;
    private String playerNumberChoicePrompt;

    private String correctChoiceMessage;
    private String wrongChoiceMessage;

    private transient Random random;
    private transient BufferedOutputTextStream textOut;

    public VulnerableBehavior() {
        random = new Random();
    }

    public void setTextOut(BufferedOutputTextStream textOut) {
        this.textOut = textOut;
    }

    public VulnerableBehavior(String name, String params, NumberActionType actionType, NumberRuleType rule, int failureDamage) {
        random = new Random();

        this.name = name;
        this.params = params;
        this.actionType = actionType;
        this.ruleType = rule;
        this.failureDamage = failureDamage;
    }

    public void demoBehavior () {
        int firstRandom = random.nextInt(10);
        int secondRandom = random.nextInt(10);

        //Demonstrate the rule
        textOut.println(numberChoosingBehaviorDescription + " " + firstRandom);
        textOut.println(numberChoosingBehaviorDescription + " " + secondRandom);

        int solution = getSolution(firstRandom, secondRandom);
        textOut.println(solutionBehaviorIndication + " : " + solution);
    }

    //Returns damage to be taken by the boss
    public static final int BOSS_DAMAGE_TAKEN = 10;
    private int solutionNumber;

    public void startBehavior (Hero hero) {
        int firstRandom = random.nextInt(10);
        int secondRandom = random.nextInt(10);

        textOut.println(numberChoosingBehaviorDescription + " " + firstRandom);
        textOut.println(numberChoosingBehaviorDescription + " " + secondRandom);
        solutionNumber = getSolution(firstRandom, secondRandom);
        textOut.println(playerNumberChoicePrompt);
    }

    public int doResponse (Hero hero, int solution) {
        if (solution == solutionNumber) {
            textOut.println(correctChoiceMessage);
            textOut.println("Boss takes " + BOSS_DAMAGE_TAKEN + " damage.");
            return BOSS_DAMAGE_TAKEN;
        } else {
            textOut.println(wrongChoiceMessage);
            hero.takeDamage(failureDamage);
            demoBehavior();
            return 0;
        }
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
        switch (ruleType) {
            case MOD10:
                return result % 10;
            default:
                throw new AssertionError();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public NumberActionType getActionType() {
        return actionType;
    }

    public void setActionType(NumberActionType actionType) {
        this.actionType = actionType;
    }

    public NumberRuleType getRule() {
        return ruleType;
    }

    public void setRule(NumberRuleType rule) {
        this.ruleType = rule;
    }

    public int getFailureDamage() {
        return failureDamage;
    }

    public void setFailureDamage(int failureDamage) {
        this.failureDamage = failureDamage;
    }

    public String getNumberChoosingBehaviorDescription() {
        return numberChoosingBehaviorDescription;
    }

    public void setNumberChoosingBehaviorDescription(String numberChoosingBehaviorDescription) {
        this.numberChoosingBehaviorDescription = numberChoosingBehaviorDescription;
    }

    public String getSolutionBehaviorIndication() {
        return solutionBehaviorIndication;
    }

    public void setSolutionBehaviorIndication(String solutionBehaviorIndication) {
        this.solutionBehaviorIndication = solutionBehaviorIndication;
    }

    public String getPlayerNumberChoicePrompt() {
        return playerNumberChoicePrompt;
    }

    public void setPlayerNumberChoicePrompt(String playerNumberChoicePrompt) {
        this.playerNumberChoicePrompt = playerNumberChoicePrompt;
    }

    public String getCorrectChoiceMessage() {
        return correctChoiceMessage;
    }

    public void setCorrectChoiceMessage(String correctChoiceMessage) {
        this.correctChoiceMessage = correctChoiceMessage;
    }

    public String getWrongChoiceMessage() {
        return wrongChoiceMessage;
    }

    public void setWrongChoiceMessage(String wrongChoiceMessage) {
        this.wrongChoiceMessage = wrongChoiceMessage;
    }

    public NumberRuleType getRuleType() {
        return ruleType;
    }

    public void setRuleType(NumberRuleType ruleType) {
        this.ruleType = ruleType;
    }
}
