package paul.TextQuest.parsing;

import java.util.Scanner;

/**
 * Created by Paul Dennis on 8/11/2017.
 */
public class CommandAnalyzer {

    String command;

    /*
    Commands
    /help
    /exit
    /debug
     */

    public CommandAnalyzer (String command) {
        this.command = command;
        if (!command.startsWith("/")) {
            throw new AssertionError();
        }
    }

    public static void main(String[] args) {
        System.out.println("Welcome to command analyzer. \"Commands\" begin with a slash (/).");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Please enter command to analyze. Empty string to exit.");
            String command = scanner.nextLine();
            if (command.equals("")) {
                break;
            }
            //TODO fix/continue to impl
            //CommandAnalyzer analyzer = new CommandAnalyzer(command);
            System.out.println("Analyzing " + command);
        }
        scanner.close();
    }
}
