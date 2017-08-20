package paul.NLPTextDungeon.bossfight;

import paul.NLPTextDungeon.interfaces.UserInterfaceClass;
import paul.NLPTextDungeon.utils.InputType;
import paul.NLPTextDungeon.utils.TextInterface;
import paul.NLPTextDungeon.entities.Hero;
import paul.NLPTextDungeon.enums.NumberActionType;
import paul.NLPTextDungeon.enums.NumberRuleType;

import java.util.*;

/**
 * Created by Paul Dennis on 8/13/2017.
 */
public class VulnerableBehavior extends UserInterfaceClass {

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
    private transient Hero hero;
    private transient BossFight bossFight;

    public VulnerableBehavior() {
        random = new Random();
    }

    public void setHero (Hero hero) {
        this.hero = hero;
    }

    public void setBossFight (BossFight bossFight) {
        this.bossFight = bossFight;
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

    @Override
    public void start (TextInterface textOut) {
        this.textOut = textOut;
        children = new ArrayList<>();
    }

    @Override
    public InputType show () {
        int firstRandom = random.nextInt(10);
        int secondRandom = random.nextInt(10);

        textOut.println(numberChoosingBehaviorDescription + " " + firstRandom);
        textOut.println(numberChoosingBehaviorDescription + " " + secondRandom);
        solutionNumber = getSolution(firstRandom, secondRandom);
        textOut.println(playerNumberChoicePrompt);
        return InputType.NUMBER;
    }

    @Override
    public InputType handleResponse (String guess) {
        int solution;
        try {
            solution = Integer.parseInt(guess);
        } catch (NumberFormatException ex) {
            textOut.debug("Expected a number. Could not parse from: " + guess);
            return InputType.NUMBER;
        }
        if (solution == solutionNumber) {
            textOut.println(correctChoiceMessage);
            textOut.println("Boss takes " + BOSS_DAMAGE_TAKEN + " damage.");
            bossFight.setHealth(bossFight.getHealth() - BOSS_DAMAGE_TAKEN);
            if (bossFight.getHealth() <= 0) {
                return InputType.FINISHED;
            }
        } else {
            textOut.println(wrongChoiceMessage);
            hero.takeDamage(failureDamage);
            demoBehavior();
        }
        return InputType.NONE;
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
