package paul.NLPTextDungeon.utils;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul Dennis on 8/16/2017.
 */
public class BufferedOutputTextStream {

    private PrintStream printStream;
    private List<String> buffer;
    private List<String> debug;
    private List<String> tutorial;

    private String currentLine;

    private boolean usingConsole;

    public BufferedOutputTextStream () {
        buffer = new ArrayList<>();
        debug = new ArrayList<>();
        tutorial = new ArrayList<>();
    }

    public BufferedOutputTextStream (PrintStream printStream) {
        this.printStream = printStream;
        buffer = new ArrayList<>();
        debug = new ArrayList<>();
        tutorial = new ArrayList<>();
        usingConsole = true;
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

    public static void main(String[] args) {
        BufferedOutputTextStream textOut = new BufferedOutputTextStream(System.out);

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
}
