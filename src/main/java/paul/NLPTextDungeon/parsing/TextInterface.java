package paul.NLPTextDungeon.parsing;

import paul.NLPTextDungeon.DungeonRunner;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Paul Dennis on 8/16/2017.
 */

public class TextInterface extends UserInterfaceClass {

    private PrintStream printStream;
    private List<String> buffer;
    private List<String> debug;
    private List<String> tutorial;

    private String currentLine;

    private boolean usingConsole;

    private DungeonRunner runner;

    @Override
    public void start (TextInterface textOut) {
        children = Collections.singletonList(runner);
        children.forEach(child -> child.start(this));
    }

    public TextInterface() throws IOException {
        buffer = new ArrayList<>();
        debug = new ArrayList<>();
        tutorial = new ArrayList<>();
        runner = new DungeonRunner();
        defaultRequester = runner;
    }


    @Override
    public InputType show () {
        return runner.show();
    }

    public void debug (String s) {
        debug.add(s);
    }

    public void debug (Object o) {
        debug.add(o.toString());
    }

    public List<String> flushDebug () {
        if (usingConsole) {
            debug.forEach(e -> printStream.println("DEBUG: " + e));
            debug = new ArrayList<>();
            return null;
        } else {
            List<String> response = debug;
            debug = new ArrayList<>();
            return response;
        }
    }

    public void println (String s) {
        if (currentLine != null) {
            buffer.add(currentLine + s);
            currentLine = null;
        } else {
            buffer.add(s);
        }
    }

    public void println (Object o) {
        println(o.toString());
    }

    public void print (String s) {
        if (currentLine != null) {
            currentLine += s;
        } else {
            currentLine = s;
        }
    }

    public void print (Object o) {
        print(o.toString());
    }

    public List<String> flush () {
        if (usingConsole) {
            buffer.forEach(e -> printStream.println(e));
            buffer = new ArrayList<>();
            if (currentLine != null) {
                printStream.print(currentLine);
                currentLine = null;
            }
            return null;
        } else {
            List<String> response = buffer;
            buffer = new ArrayList<>();
            if (currentLine != null) {
                response.add(currentLine);
                currentLine = null;
            }
            return response;
        }
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
