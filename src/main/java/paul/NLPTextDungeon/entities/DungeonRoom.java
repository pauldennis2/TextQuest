package paul.NLPTextDungeon.entities;

import paul.NLPTextDungeon.LeavingRoomAction;
import paul.NLPTextDungeon.entities.obstacles.Chasm;
import paul.NLPTextDungeon.interfaces.ParamAction;
import paul.NLPTextDungeon.interfaces.VoidAction;
import paul.NLPTextDungeon.parsing.UserInterfaceClass;
import paul.NLPTextDungeon.parsing.InputType;
import paul.NLPTextDungeon.parsing.TextInterface;
import paul.NLPTextDungeon.bossfight.BossFight;
import paul.NLPTextDungeon.entities.obstacles.Obstacle;
import paul.NLPTextDungeon.entities.obstacles.RiddleObstacle;
import paul.NLPTextDungeon.enums.Direction;
import paul.NLPTextDungeon.enums.LightingLevel;
import paul.NLPTextDungeon.enums.SpeakingVolume;
import paul.NLPTextDungeon.interfaces.listeners.SpeechListener;
import paul.NLPTextDungeon.utils.VictoryException;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Paul Dennis on 8/8/2017.
 */
public class DungeonRoom extends UserInterfaceClass {

    private String name;
    private String description;
    private int id;
    private String tutorial;

    private double lighting;
    private List<Monster> monsters;
    private List<BackpackItem> items;
    //The "Key" for hidden items is a word location in the room. By convention the word should appear in the description
    //Of the room. For example if the description references a "fountain" than an item would be hidden by "fountain"
    private Map<String, List<BackpackItem>> hiddenItems;
    private List<Obstacle> obstacles;
    private Chest chest;
    private String bossFightFileLocation;
    private List<Feature> features;

    private Map<String, String> specialRoomActions;
    private LightingChangeAction onLightingChange;
    private Map<Direction, LeavingRoomAction> onHeroLeave;

    //Temporary variables for JSONification
    private Map<Direction, Integer> connectedRoomIds;

    private transient List<SpeechListener> speechListeners;
    private transient TextInterface textOut;


    private transient Map<Direction, DungeonRoom> connectedRooms;
    private transient BossFight bossFight;
    private transient Hero hero;
    private transient boolean described;

    public DungeonRoom () {
        hiddenItems = new HashMap<>();
        monsters = new ArrayList<>();
        items = new ArrayList<>();
        connectedRoomIds = new HashMap<>();
        connectedRooms = new HashMap<>();
        obstacles = new ArrayList<>();
        hiddenItems = new HashMap<>();
        speechListeners = new ArrayList<>();
        specialRoomActions = new HashMap<>();
        initUniversalSpeechListeners();
    }

    private static Map<String, VoidAction> voidActionMap;
    private static Map<String, ParamAction> paramActionMap;

    private static void initActionMaps () {
        voidActionMap = new HashMap<>();
        paramActionMap = new HashMap<>();

        paramActionMap.put("createMonster", (room, param) -> {
            if (param.equals("Skeleton")) {
                room.addMonster(new Monster(2, 1, "Skeleton"));
            } else {
                room.getHero().getTextOut().debug("Only type supported is skeleton. Param was = " + param);
            }
        });
        paramActionMap.put("explode", (room, param) -> {
            int damageAmt = Integer.parseInt(param);
            room.getHero().getTextOut().println("BOOM!! Explosions!");
            room.getHero().takeNonMitigatedDamage(damageAmt);
        });
        paramActionMap.put("giveExp", (room, param) -> room.getHero().addExp(Integer.parseInt(param)));
        voidActionMap.put("douse", room -> room.setLighting(0.0));
        voidActionMap.put("light", room -> room.setLighting(1.0));
        voidActionMap.put("makeMinibossWeak", room -> {
            room.getMonsters().stream()
                .filter(Monster::isMiniboss)
                .forEach(miniboss -> {
                    miniboss.setMight(2);
                    miniboss.setDefense(1);
                    miniboss.disable(1);
                });
        });
        voidActionMap.put("makeMinibossStrong", room -> {
            room.getMonsters().stream()
                .filter(Monster::isMiniboss)
                .forEach(miniboss -> {
                    miniboss.setMight(5);
                    miniboss.setDefense(12);
                });
        });
        voidActionMap.put("startFight", room -> room.getHero().takeAction("fight"));
        voidActionMap.put("victory", room -> {
            throw new VictoryException("You win!");
        });
        voidActionMap.put("crackFloor", room -> {
            room.textOut.println("CRAAACK!!!! The floor of the room splits and a giant chasm appears.");
            Chasm chasm = new Chasm();
            chasm.addBlockedDirection(Direction.ALL);
            room.addObstacle(new Chasm());
            room.hero.setPreviousLocation(null); //Prevent retreating
        });
        paramActionMap.put("heal", (room, param) -> {
            int amt = Integer.parseInt(param);
            room.getHero().restoreHealth(amt);
        });
    }

    public DungeonRoom (String name, String description) {
        this.name = name;
        this.description = description;
        hiddenItems = new HashMap<>();
        monsters = new ArrayList<>();
        items = new ArrayList<>();
        connectedRoomIds = new HashMap<>();
        connectedRooms = new HashMap<>();
        obstacles = new ArrayList<>();
        hiddenItems = new HashMap<>();
        speechListeners = new ArrayList<>();
        specialRoomActions = new HashMap<>();
        initUniversalSpeechListeners();
    }

    public List<BackpackItem> searchForHiddenItems (String location) {
        List<BackpackItem> hiddenItemList = hiddenItems.get(location);
        if (hiddenItemList != null) {
            hiddenItems.put(location, new ArrayList<>());
            return hiddenItemList;
        } else {
            return new ArrayList<>();
        }
    }

    public void addHiddenItem (String locationName, BackpackItem item) {
        if (hiddenItems.get(locationName) == null) {
            List<BackpackItem> singleItemList = new ArrayList<>();
            singleItemList.add(item);
            hiddenItems.put(locationName, singleItemList);
        }
    }

    @Override
    public void start (TextInterface textOut) {
        this.textOut = textOut;
        if (bossFight != null) {
            children = Collections.singletonList(bossFight);
            bossFight.start(textOut);
        } else {
            children = new ArrayList<>();
        }
    }

    public void vocalize (String message, SpeakingVolume volume) {
        textOut.println("Player " + volume.toString().toLowerCase() + "s: " + message);
        speechListeners.forEach(e -> e.notify(message, volume));
    }

    private void initUniversalSpeechListeners () {
        SpeechListener riddleAnswerListener = (message, volume) -> {
            if (volume == SpeakingVolume.SAY) {
                List<RiddleObstacle> riddles = obstacles.stream()
                        .filter(e -> e.getClass() == RiddleObstacle.class)
                        .map(e -> (RiddleObstacle)e)
                        .collect(Collectors.toList());

                boolean oneCorrect = false;
                for (RiddleObstacle riddle : riddles) {
                    boolean response = riddle.attempt(message, hero);
                    if (response) {
                        textOut.println("You got it right!");
                        oneCorrect = true;
                    }
                }
                if (riddles.size() > 0 && !oneCorrect) {
                    textOut.println("Wrong. Feel the retribution of the sphinx.");
                    hero.takeDamage(5);
                }
            }
        };
        SpeechListener shoutAggroListener = (message, volume) -> {
            if (volume == SpeakingVolume.SHOUT) {
                List<DungeonRoom> adjacentRooms = new ArrayList<>(connectedRooms.values());
                int monstersNow = monsters.size();
                adjacentRooms.forEach(adjRoom -> addMonsters(adjRoom.removeMonsters()));
                if (monstersNow < monsters.size()) {
                    textOut.println("Looks like your shouting got some attention.");
                }
                hero.takeAction("fight");
                updateMonsters();
            }
        };
        speechListeners.add(riddleAnswerListener);
        speechListeners.add(shoutAggroListener);
    }

    public List<Monster> removeMonsters () {
        List<Monster> removed = monsters;
        monsters = new ArrayList<>();
        return removed;
    }


    public void addMonster (Monster monster) {
        monsters.add(monster);
    }

    public void addMonsters (List<Monster> monsters) {
        monsters.forEach(this::addMonster);
    }

    public void addContainer (Chest chest) {
        this.chest = chest;
    }

    public void addItem (BackpackItem item) {
        items.add(item);
    }

    public void connectTo (Direction direction, DungeonRoom other) {
        if (connectedRooms.get(direction) != null) {
            if (connectedRooms.get(direction) == other) {
            } else {
                throw new AssertionError("Already connected to a different room in that direction.");
            }
        }
        if (other == null) {
            throw new AssertionError("Cannot connect to a null room. This = " + this.getName());
        }

        connectedRooms.put(direction, other);
        other.connectedRooms.put(direction.getOpposite(), this);
    }

    public Set<Direction> getTravelDirections () {
        return connectedRooms.keySet();
    }

    @Override
    public InputType show () {
        if (bossFight != null && !bossFight.isConquered()) {
            InputType type = bossFight.show();
            if (type != InputType.NONE) {
                requester = bossFight;
                return type;
            }
            return bossFight.show();
        } else {
            describe();
        }
        return InputType.NONE;
    }

    public void describe () {
        LightingLevel lightingLevel = LightingLevel.getLightingLevel(lighting);
        if (!described) {
            textOut.println(description);
            described = true;
        }
        List<Obstacle> obstaclesForDisplay = obstacles.stream()
                .filter(obstacle -> {
                    if (obstacle.isCleared()) {
                        return obstacle.isDisplayIfCleared();
                    }
                    return true;
                })
                .collect(Collectors.toList());
        if (obstaclesForDisplay.size() > 0) {
            textOut.println("The room has the following obstacles:");
            obstaclesForDisplay.forEach(e -> textOut.println(e));
        }

        //Print riddles
        obstacles.stream()
                .filter(e -> e.getClass() == RiddleObstacle.class)
                .filter(e -> !e.isCleared())
                .forEach(e -> textOut.println(((RiddleObstacle) e).getRiddle()));

        switch (lightingLevel) {
            case WELL_LIT:
                textOut.println("The room is well-lit.");
                monsters.forEach(textOut::println);
                break;
            case DIM:
                textOut.println("The room is dimly lit.");
                if (monsters.size() > 1) {
                    textOut.println("You can see " + monsters.size() + " figures moving around.");
                } else if (monsters.size() == 1) {
                    textOut.println("You can see one figure moving around.");
                }
                break;
            case PITCH_BLACK:
                textOut.println("The room is pitch black.");
                break;
        }

        items.stream()
                .filter(item -> item.isVisible(lighting))
                .forEach(textOut::println);

        textOut.println("There are passages leading:");
        connectedRooms.keySet().forEach(e -> textOut.println(e));
    }

    public List<BackpackItem> lootRoom () {
        List<BackpackItem> visibleItems = items.stream()
                .filter(item -> item.isVisible(lighting))
                .collect(Collectors.toList());
        items.removeAll(visibleItems);
        return visibleItems;
    }

    public void updateMonsters () {
        monsters = monsters.stream()
                .filter(e -> e.getHealth() > 0)
                .collect(Collectors.toList());
    }

    public double getLighting () {
        return lighting;
    }

    public void setLighting (double lighting) {
        if (lighting != this.lighting) {
            if (onLightingChange != null) {
                String action = onLightingChange.getActionMap().get(LightingLevel.getLightingLevel(lighting));
                if (action != null) {
                    doAction(action);
                }
            }
        }
        if (lighting > 1.0) {
            this.lighting = 1.0;
        } else if (lighting < 0.0) {
            this.lighting = 0.0;
        } else {
            this.lighting = lighting;
        }
    }

    public void doAction (String action) {
        if (action.contains(" ")) {
            String[] tokens = action.split(" ");
            paramActionMap.get(tokens[0]).doAction(this, tokens[1]);
        } else {
            voidActionMap.get(action).doAction(this);
        }
    }

    public boolean isCleared () {
        return monsters.size() == 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Monster> getMonsters() {
        return monsters;
    }

    public void setMonsters(List<Monster> monsters) {
        this.monsters = monsters;
    }

    public List<BackpackItem> getItems() {
        return items;
    }

    public void setItems(List<BackpackItem> items) {
        this.items = items;
    }

    public Chest getChest() {
        return chest;
    }

    public void setChest(Chest chest) {
        this.chest = chest;
    }

    public Map<Direction, Integer> getConnectedRoomIds() {
        return connectedRoomIds;
    }

    public void setConnectedRoomIds(Map<Direction, Integer> connectedRoomIds) {
        this.connectedRoomIds = connectedRoomIds;
    }

    public Hero getHero() {
        return hero;
    }

    private transient int numVisits = 0;

    public void setHero(Hero hero) {
        if (hero == null) {
            throw new AssertionError("Cannot be used to remove hero. Use removeHero() instead.");
        }
        this.hero = hero;
        numVisits++;
        if (numVisits == 1) {
            textOut.tutorial(tutorial);
        } else if (numVisits == 2) {
            textOut.tutorial("Repeating tutorial just in case.");
            textOut.tutorial(tutorial);
        }
        if (bossFight != null) {
            bossFight.setHero(hero);
        }
    }

    public void removeHero () {
        hero = null;
    }

    public Map<Direction, DungeonRoom> getConnectedRooms() {
        return connectedRooms;
    }

    public void setConnectedRooms(Map<Direction, DungeonRoom> connectedRooms) {
        this.connectedRooms = connectedRooms;
    }

    public Map<String, List<BackpackItem>> getHiddenItems() {
        return hiddenItems;
    }

    public void setHiddenItems(Map<String, List<BackpackItem>> hiddenItems) {
        this.hiddenItems = hiddenItems;
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    public void setObstacles(List<Obstacle> obstacles) {
        this.obstacles = obstacles;
    }

    public void addObstacle (Obstacle obstacle) {
        obstacles.add(obstacle);
    }

    public String getBossFightFileLocation() {
        return bossFightFileLocation;
    }

    public void setBossFightFileLocation(String bossFightFileLocation) throws IOException {
        this.bossFightFileLocation = bossFightFileLocation;

        this.bossFight = BossFight.buildBossFightFromFile(bossFightFileLocation);
    }

    public String getTutorial() {
        return tutorial;
    }

    public void setTutorial(String tutorial) {
        this.tutorial = tutorial;
    }

    public Map<String, String> getSpecialRoomActions() {
        return specialRoomActions;
    }

    public void setSpecialRoomActions(Map<String, String> specialRoomActions) {
        this.specialRoomActions = specialRoomActions;
    }

    public LightingChangeAction getOnLightingChange() {
        return onLightingChange;
    }

    public void setOnLightingChange(LightingChangeAction onLightingChange) {
        this.onLightingChange = onLightingChange;
    }

    public Map<Direction, LeavingRoomAction> getOnHeroLeave() {
        return onHeroLeave;
    }

    public void setOnHeroLeave(Map<Direction, LeavingRoomAction> onHeroLeave) {
        this.onHeroLeave = onHeroLeave;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }
}
