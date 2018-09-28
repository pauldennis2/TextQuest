/**
 * @author Paul Dennis (pd236m)
 * Sep 28, 2018
 */
package paul.TextQuest.entities;

public class CombatEntity extends DungeonEntity {

	protected int health;
    protected int might;
    protected int defense;
	
	protected transient int disabledForRounds;
	
	public void disable (int rounds) {
        disabledForRounds += rounds;
    }

    public boolean isDisabled () {
        return disabledForRounds > 0;
    }

    public void nextRound () {
        if (disabledForRounds > 0) {
            disabledForRounds--;
        }
    }
    
    public void unDisable () {
    	disabledForRounds = 0;
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

	public int getDefense() {
		return defense;
	}

	public void setDefense(int defense) {
		this.defense = defense;
	}
}
