package paul.TextQuest.bossfight;

import paul.TextQuest.entities.Hero;
import paul.TextQuest.enums.ActionResponseTiming;
import paul.TextQuest.parsing.InputType;
import paul.TextQuest.parsing.StatementAnalysis;
import paul.TextQuest.parsing.StatementAnalyzer;
import paul.TextQuest.parsing.TextInterface;
import paul.TextQuest.parsing.UserInterfaceClass;
import paul.TextQuest.parsing.WordType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul Dennis on 8/13/2017.
 */
public class AttackBehavior extends UserInterfaceClass {

    private String name;
    private BehaviorResponse solution; //Expresses not just "a" response but "THE" response (solution) to the attack

    private String actionDescription;
    private String avoidDescription;

    private int damage;

    private transient boolean solved;
    private transient BehaviorResponse attemptedSolution;
    private transient int numTimesDone;
    private transient Hero hero;


    public AttackBehavior () {
        numTimesDone = 0;
    }

    public void setHero (Hero hero) {
        this.hero = hero;
    }

    public AttackBehavior(String name, BehaviorResponse solution, String actionDescription, String avoidDescription, int damage) {
        this.name = name;
        this.solution = solution;
        this.actionDescription = actionDescription;
        this.avoidDescription = avoidDescription;
        this.damage = damage;
        numTimesDone = 0;
    }

    @Override
    public void start (TextInterface textOut) {
        this.textOut = textOut;
        children = new ArrayList<>();
    }

    @Override
    public InputType show () {
        textOut.println(actionDescription);
        numTimesDone++;
        if (solved) {
            textOut.println(avoidDescription);
            return InputType.NONE;
        } else if (attemptedSolution != null) {
            textOut.println("Attempting: " + attemptedSolution);
            if (solution.equals(attemptedSolution)) {
                textOut.println(avoidDescription);
                solved = true;
                return InputType.NONE;
            } else {
                textOut.println("It doesn't seem to have worked.");
                attemptedSolution = null;
                return InputType.NONE;
            }
        } else {
            hero.takeDamage(damage);
            if (numTimesDone >= 2) {
                textOut.println("What would you like to do next time?");
                return InputType.SOLUTION_STRING;
            } else {
                return InputType.NONE;
            }
        }
    }

    @Override
    public InputType handleResponse (String solution) {

        StatementAnalyzer statementAnalyzer = StatementAnalyzer.getInstance();
        StatementAnalysis analysis = statementAnalyzer.analyzeStatement(solution);
        List<String> timingWords = analysis.getTokenMatchMap().get(WordType.TIMING);
        List<String> actionWords = analysis.getTokenMatchMap().get(WordType.VOID_ACTION);
        if (timingWords.size() > 0 && actionWords.size() > 0) {
            //Build a solution object
            attemptedSolution = new BehaviorResponse(this.name,
                    actionWords.get(0), ActionResponseTiming.getFromString(timingWords.get(0)));
        } else {
            textOut.println("Could not find an actionable solution. Requires a timing and an action word.");
        }

        return show();
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

}
