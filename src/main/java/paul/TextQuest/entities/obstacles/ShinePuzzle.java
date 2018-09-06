package paul.TextQuest.entities.obstacles;

import java.util.List;

import paul.TextQuest.entities.Feature;
import paul.TextQuest.entities.Hero;
import paul.TextQuest.parsing.TextInterface;

/**
 * Created by Paul Dennis on 9/5/2017.
 */
public class ShinePuzzle extends Obstacle {
	
	private int numMirrors;
	
	public ShinePuzzle (int numMirrors) {
		super();
		this.numMirrors = numMirrors;
	}

    @Override
    public boolean attempt(String solution, Hero hero) {
    	List<Feature> features = hero.getLocation().getFeatures();
    	int cleanMirrors = 0;
    	for (Feature feature : features) {
    		if (feature.getName().contains("Mirror") && feature.getStatus().equals("clean")) {
				cleanMirrors++;
    		}
    	}
    	if (cleanMirrors != numMirrors) {
    		TextInterface textOut = hero.getTextOut();
    		double percent = (double) cleanMirrors / (double) numMirrors;
    		if (percent > 0.75) {
    			textOut.println("The reflected light nearly reaches the crystal.");
    		} else if (percent > 0.5) {
    			textOut.println("The reflected light comes close to the crystal.");
    		} else {
    			textOut.println("The reflected light comes nowhere near the crystal.");
    		}
    		if (onAttempt != null) {
    			hero.getLocation().doAction(onAttempt);
    		}
    		return false;
    	} else {
    		return true;
    	}
    }
    
    public void setNumMirrors (int numMirrors) {
    	this.numMirrors = numMirrors;
    }
    
    public int getNumMirrors () {
    	return numMirrors;
    }
}
