package paul.NLPTextDungeon.entities.parsing;

import java.util.Scanner;

/**
 * Created by Paul Dennis on 8/11/2017.
 */
public class QuestionAnalyzer {

    String question;

    public QuestionAnalyzer (String question) {
        if (!question.contains("?")) {
            System.out.println("There's no question!");
            throw new AssertionError();
        }
        this.question = question;
    }

    public static void main(String[] args) {
        System.out.println("Welcome to question analyzer practice.");
        System.out.println("Questions must contain a question mark, duh. Empty string to exit.");
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Enter question to analyze:");
            String question = scanner.nextLine();
            if (question.equals("")) {
                break;
            }
            QuestionAnalyzer analyzer = new QuestionAnalyzer(question);
            System.out.println("analyzing " + question + "...");
        }
    }
}
