package paul.TextQuest;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Paul Dennis on 8/16/2017.
 */

public class TextInterface {

    private List<String> buffer;
    private List<String> debug;
    private List<String> tutorial;
    
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
}
