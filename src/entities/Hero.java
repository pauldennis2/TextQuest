package entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul Dennis on 8/8/2017.
 */
public class Hero {

    private int health;
    private int might;
    private int magic;
    private int sneak;

    private int level;
    private int exp;

    private List<BackpackItem> backpack;
    private DungeonRoom location;

    public Hero () {
        health = 50;
        might = 10;
        magic = 2;
        sneak = 0;

        level = 1;
        exp = 0;

        backpack = new ArrayList<>();
        backpack.add(new BackpackItem("Torch"));
        backpack.add(new BackpackItem("Sword"));
        backpack.add(new BackpackItem("Bow & Arrows"));
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getMight() {
        return might;
    }

    public void setMight(int might) {
        this.might = might;
    }

    public int getMagic() {
        return magic;
    }

    public void setMagic(int magic) {
        this.magic = magic;
    }

    public int getSneak() {
        return sneak;
    }

    public void setSneak(int sneak) {
        this.sneak = sneak;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public List<BackpackItem> getBackpack() {
        return backpack;
    }

    public void setBackpack(List<BackpackItem> backpack) {
        this.backpack = backpack;
    }

    public DungeonRoom getLocation() {
        return location;
    }

    public void setLocation(DungeonRoom location) {
        this.location = location;
    }
}
