package paul.TextQuest.entities.obstacles;

import java.util.List;

import paul.TextQuest.entities.Feature;
import paul.TextQuest.entities.Hero;
import paul.TextQuest.parsing.TextInterface;

/**
 * Created by Paul Dennis on 9/5/2017.
 */
public class ShinePuzzle extends Obstacle {

    @Override
    public boolean attempt(String solution, Hero hero) {
    	List<Feature> features = hero.getLocation().getFeatures();
    	int totalMirrors = 0;
    	int uncleanMirrors = 0;
    	for (Feature feature : features) {
    		if (feature.getName().contains("Mirror")) {
    			totalMirrors++;
    			if (!feature.getStatus().equals("clean")) {
    				uncleanMirrors++;
    			}
    		}
    	}
    	if (uncleanMirrors != 0) {
    		TextInterface textOut = hero.getTextOut();
    		double percent = (double) uncleanMirrors / (double) totalMirrors;
    		if (percent > 0.75) {
    			textOut.println("The reflected light nearly reaches the crystal.");
    		} else if (percent > 0.5) {
    			textOut.println("The reflected light comes close to the crystal.");
    		} else {
    			textOut.println("The reflected light comes nowhere near the crystal.");
    		}
    		return false;
    	} else {
    		return true;
    	}
    }
}
