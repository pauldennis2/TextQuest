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

    private List<String> buffer;
    private List<String> debug;
    private List<String> tutorial;

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
        List<String> response = debug;
        debug = new ArrayList<>();
        return response;
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
