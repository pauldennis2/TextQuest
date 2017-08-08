import java.io.InputStream;
import java.util.Scanner;

/**
 * Created by Paul Dennis on 8/8/2017.
 */
public class SafeNumScanner {

    Scanner scanner;

    public SafeNumScanner (InputStream source) {
        scanner = new Scanner(source);
    }

    public int getSafeNum () {
        try {
            System.out.println("Please enter a number");
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException ex) {
            System.out.println("Not a number");
            getSafeNum();
        }
        return -1;
    }

    public int getSafeNum (int min, int max) {
        if (min >= max) {
            throw new AssertionError();
        }
        try {
            System.out.println("Please enter a number between " + min + " and " + max + " (inclusive).");
            int number = Integer.parseInt(scanner.nextLine());
            if (number <= max && number >= min) {
                return number;
            } else {
                getSafeNum(min, max);
            }
        } catch (NumberFormatException ex) {
            getSafeNum(min, max);
        }
        return -1;
    }
}
