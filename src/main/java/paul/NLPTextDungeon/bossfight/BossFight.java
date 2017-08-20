package paul.NLPTextDungeon.bossfight;

import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.internal.util.xml.impl.Input;
import paul.NLPTextDungeon.interfaces.TextOuter;
import paul.NLPTextDungeon.interfaces.UserInterface;
import paul.NLPTextDungeon.utils.InputType;
import paul.NLPTextDungeon.utils.TextInterface;
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
public class BossFight implements UserInterface {

    private String name;
    private int health;
    private int maxAttacks;
    private String bossDescription;
    private String roomDescription;
    private List<AttackBehavior> attackBehaviors;
    private VulnerableBehavior vulnerableBehavior;

    private transient Random random;
    private transient boolean conquered;
    private transient TextInterface textOut;
    private UserInterface requester;

    private transient int numTimesAttackedWithoutVuln;

    public BossFight () {
        attackBehaviors = new ArrayList<>();
        random = new Random();
        conquered = false;
        numTimesAttackedWithoutVuln = 0;
    }

    public void setTextOut (TextInterface textOut) {
        this.textOut = textOut;
        vulnerableBehavior.setTextOut(textOut);
        attackBehaviors.forEach(e -> e.setTextOut(textOut));
    }

    public InputType processResponse (String response) {
        UserInterface rq = requester;
        requester = null;
        InputType type = rq.processResponse(response);
        if (type == InputType.FINISHED) {
            conquered = true;
        }
        return type;
    }

    public InputType show () {
        if (numTimesAttackedWithoutVuln >= maxAttacks) {
            InputType type = vulnerableBehavior.show();
            if (type != InputType.NONE) {
                requester = vulnerableBehavior;
                return type;
            } else {
                return show();
            }
        } else {
            int chosenAttack = 0;
            if (attackBehaviors.size() > 1) {
                chosenAttack = random.nextInt(attackBehaviors.size() - 1);
            }
            numTimesAttackedWithoutVuln++;
            InputType type = attackBehaviors.get(chosenAttack).show();
            if (type != InputType.NONE) {
                requester = attackBehaviors.get(chosenAttack);
                return type;
            } else { //InputType.NONE
                return show();
            }
        }
    }

    public void start () {
        textOut.println("Welcome to Boss Fight");
        textOut.println("Boss: " + name);
        textOut.println("Description: " + bossDescription);
        textOut.println("Room Description: " + roomDescription);
        textOut.println("-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.\n\n\n\n");

        vulnerableBehavior.demoBehavior();
        vulnerableBehavior.setBossFight(this);
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
            throw new AssertionError("Could not find file.");
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
        vulnerableBehavior.setHero(hero);
        attackBehaviors.forEach(e -> e.setHero(hero));
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
