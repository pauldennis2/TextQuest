package paul.TextQuest.parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import paul.TextQuest.DungeonRunner;
import paul.TextQuest.entities.Hero;

/**
 * Created by Paul Dennis on 8/16/2017.
 */

public class TextInterface {

    private List<String> buffer;
    private List<String> debug;
    private List<String> tutorial;

    private DungeonRunner runner;
    
    private static TextInterface instance;
    
    
    public static TextInterface getInstance () {
    	if (instance == null) {
    		instance = new TextInterface();
    	}
    	return instance;
    }
    
    private TextInterface () {
    	buffer = new ArrayList<>();
        debug = new ArrayList<>();
        tutorial = new ArrayList<>();
    }
    
    private TextInterface (Hero hero, String fileName) {
    	this();
    	try {
    		runner = new DungeonRunner(hero, fileName);
    	} catch (IOException ex) {
    		throw new AssertionError(ex);
    	}
    }
    
    public void newDungeon (Hero hero, String fileName) throws IOException {
    	runner = new DungeonRunner(hero, fileName);
    	
    	runner.start(this);
    }

    /**
     * Adds the given string to the debug buffer.
     * It will be printed in the debug window next time the buffer is flushed.
     * @param s
     */
    public void debug (String s) {
        debug.add(s);
    }

    public void debug (Object o) {
        debug.add(o.toString());
    }

    public List<String> flushDebug () {
        List<String> response = debug;
        debug = new ArrayList<>();
        return response;
    }
    
    public void printDebug () {
    	debug.forEach(System.out::println);
    }

    public void println (String s) {
        buffer.add(s);
    }

    public void println (Object o) {
        println(o.toString());
    }

    public List<String> flush () {
        List<String> response = buffer;
        buffer = new ArrayList<>();
        return response;
    }

    public List<String> flushTutorial () {
        List<String> response = tutorial;
        tutorial = new ArrayList<>();
        return response;
    }

    public void tutorial (String tutorialString) {
        tutorial.add(tutorialString);
    }

    public DungeonRunner getRunner() {
        return runner;
    }
}
