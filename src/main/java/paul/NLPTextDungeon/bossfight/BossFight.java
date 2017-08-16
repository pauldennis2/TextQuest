package paul.NLPTextDungeon.bossfight;

import com.fasterxml.jackson.databind.ObjectMapper;
import paul.NLPTextDungeon.entities.Dungeon;
import paul.NLPTextDungeon.entities.Hero;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by Paul Dennis on 8/13/2017.
 */
public class BossFight {

    private String name;
    private int health;
    private int maxAttacks;
    private String bossDescription;
    private String roomDescription;
    private List<AttackBehavior> attackBehaviors;
    private VulnerableBehavior vulnerableBehavior;

    private transient Random random;
    private transient Hero hero;
    private transient boolean conquered;

    public BossFight () {
        attackBehaviors = new ArrayList<>();
        random = new Random();
    }

    public BossFight(String name, int health, List<AttackBehavior> attackBehaviors, VulnerableBehavior vulnerableBehavior) {
        this.name = name;
        this.health = health;
        this.attackBehaviors = attackBehaviors;
        this.vulnerableBehavior = vulnerableBehavior;
        random = new Random();
    }

    public void doFight () {
        int numTimesAttackedWithoutVuln = 0;

        System.out.println("Welcome to Boss Fight");
        System.out.println("Boss: " + name);
        System.out.println("Description: " + bossDescription);
        System.out.println("Room Description: " + roomDescription);
        System.out.println("-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.\n\n\n\n");
        vulnerableBehavior.demoBehavior();
        while (true) {
            int chosenAttack = 0;
            if (attackBehaviors.size() > 1) {
                chosenAttack = random.nextInt(attackBehaviors.size() - 1);
            }
            attackBehaviors.get(chosenAttack).doBehavior(hero);
            numTimesAttackedWithoutVuln++;
            if (numTimesAttackedWithoutVuln >= maxAttacks) {
                int damage = vulnerableBehavior.doBehavior(hero);
                health -= damage;
                if (health <= 0) {
                    System.out.println("You beat the boss!");
                    conquered = true;
                    break;
                }
                numTimesAttackedWithoutVuln = 0;
            } else {
                //50% chance to "skip" an attack
                boolean coinToss = random.nextBoolean();
                if (coinToss) {
                    numTimesAttackedWithoutVuln++;
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {

        BossFight fight = buildBossFightFromFile("first_boss.json");
        Hero hero = new Hero();
        fight.setHero(hero);
        System.out.println("Are you not entertained?");
        fight.doFight();
    }

    public static final String ENCOUNTER_FILE_PATH = "content_files/encounters/";
    public static BossFight buildBossFightFromFile (String fileName) throws IOException {
        return getBossFightFromJson(getJsonFromFile(ENCOUNTER_FILE_PATH + fileName));
    }

    private static String getJsonFromFile (String fileName) {
        try (Scanner fileScanner = new Scanner(new File(fileName))) {
            StringBuilder stringBuilder = new StringBuilder(fileScanner.nextLine());
            while (fileScanner.hasNext()) {
                stringBuilder.append(fileScanner.nextLine());
            }
            return stringBuilder.toString();
        } catch (FileNotFoundException ex) {
            System.out.println("Could not find file.");
            throw new AssertionError();
        }
    }

    private static BossFight getBossFightFromJson (String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, BossFight.class);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getMaxAttacks() {
        return maxAttacks;
    }

    public void setMaxAttacks(int maxAttacks) {
        this.maxAttacks = maxAttacks;
    }

    public List<AttackBehavior> getAttackBehaviors() {
        return attackBehaviors;
    }

    public void setAttackBehaviors(List<AttackBehavior> attackBehaviors) {
        this.attackBehaviors = attackBehaviors;
    }

    public VulnerableBehavior getVulnerableBehavior() {
        return vulnerableBehavior;
    }

    public void setVulnerableBehavior(VulnerableBehavior vulnerableBehavior) {
        this.vulnerableBehavior = vulnerableBehavior;
    }

    public void setHero (Hero hero) {
        this.hero = hero;
    }

    public String getBossDescription() {
        return bossDescription;
    }

    public void setBossDescription(String bossDescription) {
        this.bossDescription = bossDescription;
    }

    public String getRoomDescription() {
        return roomDescription;
    }

    public void setRoomDescription(String roomDescription) {
        this.roomDescription = roomDescription;
    }

    public boolean isConquered() {
        return conquered;
    }
}
