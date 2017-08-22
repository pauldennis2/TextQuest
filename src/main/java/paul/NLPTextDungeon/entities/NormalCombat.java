package paul.NLPTextDungeon.entities;

import paul.NLPTextDungeon.parsing.*;

import java.util.List;
import java.util.Random;

/**
 * Created by pauldennis on 8/21/17.
 */
public class NormalCombat extends UserInterfaceClass {
    //Working on combat-feature branch

    TextInterface textOut;
    DungeonRoom room;
    Random random;
    StatementAnalyzer analyzer;

    public NormalCombat (DungeonRoom room) {
        this.room = room;
        random = new Random();
        analyzer = StatementAnalyzer.getInstance();
    }

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
            return InputType.FINISHED;
        }
    }

    public InputType show () {
        //Todo: add initiative. For now the hero always gets it
        List<Monster> monsters = room.getMonsters();
        Hero hero = room.getHero();
        if (monsters.size() > 0) {
            //Hero attack
            int might = hero.getMight();
            int damageRoll = random.nextInt(might + 1) + might;
            Monster monster = monsters.get(0);
            double chance = calcAccuracy(might, monster.getDefence());
            double roll = Math.random();
            if (chance > roll) {
                int taken = monster.takeDamage(damageRoll);
                textOut.println("You hit " + monster.getName() + " for " + taken + " damage.");
                room.updateMonsters();
            } else {
                textOut.println("You missed " + monster.getName() + ".");
            }
            monsters = room.getMonsters();

            if (monsters.size() == 0) {
                return InputType.FINISHED;
            }
            monsters.forEach(m -> {
                if (monster.isDisabled()) {
                    textOut.println(monster.getName() + " misses its turn (disabled).");
                    monster.nextRound();
                } else {
                    int monsterMight = m.getMight();
                    int monsterDamageRoll = random.nextInt(monsterMight + 1) + monsterMight;
                    double mChance = calcAccuracy(monsterMight, hero.getDefence());
                    double mRoll = Math.random();
                    if (mChance > mRoll) {
                        hero.takeDamage(monsterDamageRoll);
                    } else {
                        textOut.println(monster.getName() + " missed you.");
                    }
                }
            });
        }
        textOut.println("End of combat round. Take an action? Enter to proceed with no special action.");
        return InputType.COMBAT;
    }

    public static final double BASE_ACCURACY = 0.8;
    public static double calcAccuracy (int might, int defense) {
        //Base chance 0.5
        //Every 2 pts of defense = -0.05
        //Every 4 pts of might = +0.05

        return BASE_ACCURACY + 0.05 * (might/4 - defense/2);
    }
}
