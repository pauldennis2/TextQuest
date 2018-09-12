/**
 * @author Paul Dennis (pd236m)
 * Aug 31, 2018
 */
package paul.TextQuest.entities.obstacles;

import paul.TextQuest.entities.Hero;

/**
 * Represents a "generic barrier" that is probably removed through a script.
 */
public class GenericBarrier extends Obstacle {

	@Override
	public boolean attempt(String solution, Hero hero) {
		return false;
	}

}
