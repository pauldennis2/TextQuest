package paul.NLPTextDungeon;

import paul.NLPTextDungeon.entities.Hero;
import paul.NLPTextDungeon.entities.Monster;
import paul.NLPTextDungeon.interfaces.BossFightNumberRule;
import paul.NLPTextDungeon.utils.SafeNumScanner;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Paul Dennis on 8/12/2017.
 */
public class Encounter {

    Monster boss;
    Hero hero;

    BossFightNumberRule rule;
    static Random random;

    public Encounter(Monster boss, BossFightNumberRule rule, Hero hero) {
        this.boss = boss;
        this.rule = rule;
        this.hero = hero;
    }

    public void doFight () {

        System.out.println("There are 10 platforms labeled 0-9");

        int firstRandomNum = random.nextInt(10);
        int secondRandomNum = random.nextInt(10);
        System.out.println("Rule demonstration:");
        System.out.println("Boss hits platforms " + firstRandomNum + " and " + secondRandomNum);
        int demonstrationPlat = rule.getSolution(Arrays.asList(firstRandomNum, secondRandomNum));
        System.out.println("Platform " + demonstrationPlat + " begins to glow mysteriously.");

        SafeNumScanner safeNumScanner = new SafeNumScanner(System.in);

        //Minigame loop
        while (true) {
            firstRandomNum = random.nextInt(10);
            secondRandomNum = random.nextInt(10);
            System.out.println("Boss hits platforms " + firstRandomNum + " and " + secondRandomNum);
            System.out.println("What platform do you want to jump on?");
            int response = safeNumScanner.getSafeNum(0, 9);
            System.out.println("You jumped on platform " + response);
            if (response == rule.getSolution(Arrays.asList(firstRandomNum, secondRandomNum))) {
                System.out.println("Rumble rumble. the boss takes 10 damage");
                boss.takeDamage(10);
                if (boss.getHealth() <= 0) {
                    System.out.println("You win");
                    break;
                }
            } else {
                System.out.println("Bad buzzer sound. You take 10 damage");
                hero.takeDamage(10);
                if (hero.getHealth() <= 0) {
                    System.out.println("You died, you lose. Goodbye.");
                    break;
                }
            }
        }
    }

    public static void main(String[] args) {
        random = new Random();
        System.out.println("Rule is mod 5 of the sum of the numbers");
        BossFightNumberRule rule = new BossFightNumberRule() {
            @Override
            public boolean checkRule(List<Integer> inputs, int solution) {
                int sum = inputs.get(0) + inputs.get(1);
                return (sum % 5) == solution;
            }

            @Override
            public int getSolution(List<Integer> inputs) {
                return (inputs.get(0) + inputs.get(1)) % 5;
            }
        };


        Monster boss = new Monster(30, 5, "Bob");
        Hero hero = new Hero();

        Encounter encounter = new Encounter(boss, rule, hero);
        encounter.doFight();
    }
}
