package paul.TextQuest.entities;

import java.util.List;
import java.util.Random;

import paul.TextQuest.parsing.*;

/**
 * Created by pauldennis on 8/21/17.
 */
public class NormalCombat extends UserInterfaceClass {

    private TextInterface textOut;
    private DungeonRoom room;
    private Random random;
    private StatementAnalyzer analyzer;

    private int expCalc;

    public static final int BASE_COMBAT_XP = 25;

    private boolean finished = false;
    
    private String onCombatEnd;

    public NormalCombat (DungeonRoom room) {
        this.room = room;
        random = new Random();
        analyzer = StatementAnalyzer.getInstance();
        expCalc = room.getMonsters().stream()
                .mapToInt(Monster::getExp)
                .sum();
    }

    @Override
    public void start (TextInterface textOut) {
        this.textOut = textOut;
    }

    public InputType handleResponse (String response) {
        StatementAnalysis analysis = analyzer.analyzeStatement(response);
        textOut.getRunner().doActionFromAnalysis(analysis);

        //If monsters remain do another combat round
        if (room.getMonsters().size() > 0) {
            return show();
        } else {
            endCombat();
            return InputType.FINISHED;
        }
    }

    public InputType show () {
        if (finished) {
            throw new AssertionError("Fight is over");
        }
        //TODO: add initiative. For now the hero always gets it
        List<Monster> monsters = room.getMonsters();
        Hero hero = room.getHero();
        if (monsters.size() > 0) {
            //Hero attacks the first monster in the list
            int might = hero.getMight();
            int damageRoll = random.nextInt(might + 1) + might;
            Monster firstMonster = monsters.get(0);
            double chance = calcAccuracy(might, firstMonster.getDefense());
            double roll = Math.random();
            if (chance > roll) {
                int taken = firstMonster.takeDamage(damageRoll);
                textOut.println("You hit " + firstMonster.getName() + " for " + taken + " damage.");
                if (firstMonster.getHealth() <= 0) {
                    textOut.println("You killed " + firstMonster.getName());
                    room.updateMonsters();
                }
            } else {
                textOut.println("You missed " + firstMonster.getName() + ".");
            }
            monsters = room.getMonsters();

            if (monsters.size() == 0) {
                endCombat();
                return InputType.FINISHED;
            }
            monsters.forEach(monster -> {
                if (monster.isDisabled()) {
                    textOut.println(monster.getName() + " misses its turn (disabled).");
                    monster.nextRound();
                } else {
                    int monsterMight = monster.getMight();
                    int monsterDamageRoll = random.nextInt(monsterMight + 1) + monsterMight;
                    double mChance = calcAccuracy(monsterMight, hero.getDefense());
                    double mRoll = Math.random();
                    if (mChance > mRoll) {
                        hero.takeDamage(monsterDamageRoll);
                    } else {
                        textOut.println(monster.getName() + " missed you.");
                    }
                }
            });
        } else {
            endCombat();
            return InputType.FINISHED;
        }
        textOut.println("End of combat round. Take an action? Enter to proceed with no special action.");
        return InputType.COMBAT;
    }

    private void endCombat () {
        if (expCalc > 0) {
            room.getHero().addExp(expCalc + BASE_COMBAT_XP);
        }
        if (onCombatEnd != null) {
        	room.doAction(onCombatEnd);
        }
        room.doAction("print {hero.health}/{hero.maxHealth}");
        expCalc = 0;
        finished = true;
    }

    public static final double BASE_ACCURACY = 0.8;
    public static double calcAccuracy (int might, int defense) {
        //Base chance 0.5
        //Every 2 pts of defense = -0.05
        //Every 4 pts of might = +0.05

        return BASE_ACCURACY + 0.05 * (might/4 - defense/2);
    }
    
    public void setOnCombatEnd (String onCombatEnd) {
    	this.onCombatEnd = onCombatEnd;
    }
}
