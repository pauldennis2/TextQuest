package paul.NLPTextDungeon.entities;

import paul.NLPTextDungeon.interfaces.UserInterfaceClass;
import paul.NLPTextDungeon.utils.InputType;
import paul.NLPTextDungeon.utils.TextInterface;

import java.util.List;

/**
 * Created by pauldennis on 8/21/17.
 */
public class NormalCombat extends UserInterfaceClass {


    TextInterface textOut;

    public NormalCombat () {

    }

    public void start (TextInterface textOut) {
        this.textOut = textOut;
    }

    public InputType show () {
        return null;
    }

    public InputType handleResponse (String response) {
        return null;
    }

    /*
    public void doCombatRound () {
        //Todo: add initiative. For now the hero always gets it
        List<Monster> monsters = location.getMonsters();
        if (monsters.size() > 0) {
            while (true) {
                //Hero attack
                int damageRoll = random.nextInt(might + 1) + might;
                Monster monster = monsters.get(0);
                double chance = calcAccuracy(might, monster.getDefence());
                double roll = Math.random();
                if (chance > roll) {
                    int taken = monster.takeDamage(damageRoll);
                    textOut.println("You hit " + monster.getName() + " for " + taken + " damage.");
                    location.updateMonsters();
                } else {
                    textOut.println("You missed " + monster.getName() + ".");
                }
                monsters = location.getMonsters();
                monsters.forEach(m -> {
                    int monsterMight = m.getMight();
                    int monsterDamageRoll = random.nextInt(monsterMight + 1) + monsterMight;
                    double mChance = calcAccuracy(monsterMight, defence);
                    double mRoll = Math.random();
                    if (mChance > mRoll) {
                        takeDamage(monsterDamageRoll);
                    } else {
                        textOut.println(monster.getName() + " missed you.");
                    }
                });
            }
        }
    }*/
}
