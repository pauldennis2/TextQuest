package paul.NLPTextDungeon.utils;

import paul.NLPTextDungeon.DungeonRunner;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul Dennis on 8/16/2017.
 */

public class TextInterface {

    private PrintStream printStream;
    private List<String> buffer;
    private List<String> debug;
    private List<String> tutorial;

    private String currentLine;

    private boolean usingConsole;

    private DungeonRunner runner;

    private InputType requestedInputType;


    public TextInterface() throws IOException {
        buffer = new ArrayList<>();
        debug = new ArrayList<>();
        tutorial = new ArrayList<>();
        runner = new DungeonRunner();
    }

    public TextInterface(PrintStream printStream) {
        this.printStream = printStream;
        buffer = new ArrayList<>();
        debug = new ArrayList<>();
        tutorial = new ArrayList<>();
        usingConsole = true;
    }

    public InputType getGameOutput () {
        return requestedInputType = runner.describeRoom();
    }

    public void returnResponse (String userResponse) {
        switch (requestedInputType) {
            case STD:
                runner.analyzeAndExecuteStatement(userResponse);

                break;
            case NUMBER:
                int x = Integer.parseInt(userResponse);
                break;
        }
    }

    public void requestInput (InputType type) {
        requestedInputType = type;
    }

    public void debug (String s) {
        debug.add(s);
    }

    public void debug (Object o) {
        debug.add(o.toString());
    }



    public int getNumberFromUser () {
        throw new AssertionError("Write");
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

    public static void main(String[] args) {
        TextInterface textOut = new TextInterface(System.out);

        textOut.println("Hello.");
        textOut.println("Do you like pizza?");
        textOut.flush();
        System.out.println("----------");
        textOut.print("Many ");
        textOut.print("words ");
        System.out.println("_-------------_");
        textOut.print("on one ");
        textOut.println("line.");
        textOut.flush();
    }

    public DungeonRunner getRunner() {
        return runner;
    }
}
