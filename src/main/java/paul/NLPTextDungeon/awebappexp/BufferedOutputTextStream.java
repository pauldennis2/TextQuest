package paul.NLPTextDungeon.awebappexp;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul Dennis on 8/16/2017.
 */
public class BufferedOutputTextStream {

    PrintStream printStream;
    List<String> buffer;

    String currentLine;

    boolean usingConsole;

    public BufferedOutputTextStream () {
        buffer = new ArrayList<>();
    }

    public BufferedOutputTextStream (PrintStream printStream) {
        this.printStream = printStream;
        buffer = new ArrayList<>();
        usingConsole = true;
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
