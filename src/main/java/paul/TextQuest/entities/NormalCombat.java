package paul.TextQuest.entities;

import java.util.List;
import java.util.Map;
import java.util.Random;

import paul.TextQuest.DungeonRunner;
import paul.TextQuest.TextInterface;
import paul.TextQuest.enums.BehaviorTiming;
import paul.TextQuest.parsing.*;
import paul.TextQuest.utils.StringUtils;

/**
 * Created by pauldennis on 8/21/17.
 */
public class NormalCombat {

    private TextInterface textOut;
    private DungeonRunner dungeonRunner;
    private DungeonRoom room;
    private Random random;
    private StatementAnalyzer analyzer;

    private int expCalc;

    public static final int BASE_COMBAT_XP = 25;

    private boolean finished = false;
    
    private String onCombatEnd;
    
    private int roundNum;

    public NormalCombat (DungeonRunner dungeonRunner, DungeonRoom room) {
        this.room = room;
        random = new Random();
        analyzer = StatementAnalyzer.getInstance();
        expCalc = room.getMonsters().stream()
                .mapToInt(Monster::getExp)
                .sum();
        roundNum = 1;
        this.dungeonRunner = dungeonRunner;
        this.textOut = dungeonRunner.getTextOut();
        textOut.println("Combat started with " + StringUtils.prettyPrintList(room.getMonsters()));
    }


    public void handleResponse (String response) {
        StatementAnalysis analysis = analyzer.analyzeStatement(response);
        dungeonRunner.doActionFromAnalysis(analysis);

        //If monsters remain do another combat round
        if (room.getMonsters().size() > 0) {
            show();
        } else {
            endCombat();
        }
    }

    public void show () {
        if (finished) {
            throw new AssertionError("Fight is over");
        }
        textOut.println("Combat Round " + roundNum);
        //TODO: add initiative. For now the hero always gets it
        List<Monster> monsters = room.getMonsters();
        Hero hero = room.getHero();
        if (monsters.size() > 0) {
            //Hero attacks the first monster in the list
        	if (!hero.isDisabled()) {
	            int might = hero.getModdedMight();
	            int damageRoll = random.nextInt(might + 1) + might;
	            Monster firstMonster = monsters.get(0);
	            double chance = calcAccuracy(might, firstMonster.getDefense());
	            double roll = Math.random();
	            if (chance > roll) {
	                int taken = firstMonster.takeDamage(damageRoll);
	                textOut.println("You hit " + firstMonster.getName() + " for " + taken + " damage.");
	                if (firstMonster.getHealth() <= 0) {
	                    textOut.println("You killed " + firstMonster.getName() + ".");
	                    room.updateMonsters();
	                }
	            } else {
	                textOut.println("You missed " + firstMonster.getName() + ".");
	            }
        	} else {
        		textOut.println("You were unable to attack (stunned)");
        	}
        	hero.nextRound();
            monsters = room.getMonsters();

            if (monsters.size() == 0) {
                endCombat();
                return;
            }
            monsters.forEach(monster -> {
                if (monster.isDisabled()) {
                    textOut.println(monster.getName() + " misses its turn (disabled).");
                    monster.nextRound();
                } else {
                	//Monster attack
                    int monsterMight = monster.getMight();
                    int monsterDamageRoll = random.nextInt(monsterMight + 1) + monsterMight;
                    double mChance = calcAccuracy(monsterMight, hero.getModdedDefense());
                    double mRoll = Math.random();
                    if (mChance > mRoll) {
                        int damageTaken = hero.takeDamage(monsterDamageRoll);
                        textOut.println("You took " + damageTaken + " damage from " + monster.getName() + ".");
                        String onDealDamage = monster.getOnDealDamage();
                        if (onDealDamage != null) {
                        	room.doAction(onDealDamage);
                        }
                    } else {
                        textOut.println(monster.getName() + " missed you.");
                    }
                    //Monster behavior
                    textOut.debug("Evaluating behaviors for round " + roundNum);
                    Map<BehaviorTiming, String> behavior = monster.getBehavior();
                    for (BehaviorTiming timing : behavior.keySet()) {
                    	textOut.debug("Evaluating " + timing);
                    	if (timing.evaluate(roundNum)) {
                    		room.doAction(behavior.get(timing));
                    		textOut.debug("we took action: " + behavior.get(timing));
                    	} else {
                    		textOut.debug("we did not take action: " + behavior.get(timing));
                    	}
                    }
                }
            });
            roundNum++;
        } else {
            endCombat();
            return;
        }
        textOut.println("End of combat round. Take an action? Enter to proceed with no special action.");
    }

    private void endCombat () {
    	room.getHero().unDisable();
    	textOut.println("Combat ended.");
        if (expCalc > 0) {
            room.getHero().addExp(expCalc + BASE_COMBAT_XP);
        }
        if (onCombatEnd != null) {
        	room.doAction(onCombatEnd);
        }
        
        room.doAction("print \"Health: {hero.health}/{hero.maxHealth}\""); //This is a cute wiring workaround
        expCalc = 0;
        finished = true;
        dungeonRunner.endCombat();
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
    
    public boolean isFinished () {
    	return finished;
    }
}
