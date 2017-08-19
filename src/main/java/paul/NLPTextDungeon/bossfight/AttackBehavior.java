package paul.NLPTextDungeon.bossfight;

import paul.NLPTextDungeon.interfaces.TextOuter;
import paul.NLPTextDungeon.utils.TextInterface;
import paul.NLPTextDungeon.entities.Hero;

/**
 * Created by Paul Dennis on 8/13/2017.
 */
public class AttackBehavior implements TextOuter {

    private String name;
    private BehaviorResponse solution; //Expresses not just "a" response but "THE" response (solution) to the attack

    private String actionDescription;
    private String avoidDescription;

    private int damage;

    private transient boolean solved;
    private transient BehaviorResponse attemptedSolution;

    private transient TextInterface textOut;

    public AttackBehavior () {

    }

    public void setTextOut(TextInterface textOut) {
        this.textOut = textOut;
    }

    public AttackBehavior(String name, BehaviorResponse solution, String actionDescription, String avoidDescription, int damage) {
        this.name = name;
        this.solution = solution;
        this.actionDescription = actionDescription;
        this.avoidDescription = avoidDescription;
        this.damage = damage;
    }

    public void doBehavior (Hero hero) {
        textOut.println(actionDescription);
        if (solved) {
            textOut.println(avoidDescription);
        } else if (attemptedSolution != null) {
            textOut.println("Attempting: " + attemptedSolution);
            if (solution.equals(attemptedSolution)) {
                textOut.println(avoidDescription);
                solved = true;
            } else {
                textOut.println("It doesn't seem to have worked.");
                attemptedSolution = null;
                doBehavior(hero);
            }
        } else {
            hero.takeDamage(damage);
            //Todo fix so people can avoid attacks
            /*
            if (numTimesDone >= 1) {
                textOut.println("What would you like to do next time?");
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
                    textOut.println("Could not find an actionable solution. Requires a timing and an action word.");
                }
            }*/
        }
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
