package paul.NLPTextDungeon.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Paul Dennis on 8/9/2017.
 */
public class WhatAmIRiddle {
    List<String> clues;
    List<String> answers;

    public WhatAmIRiddle(List<String> clues, List<String> answers) {
        this.clues = clues;
        this.answers = answers;
    }

    static List<WhatAmIRiddle> riddles;
    public static void buildRiddles () {
        riddles = new ArrayList<>();
        List<String> clues = new ArrayList<>();
        clues.add("The man who made me, doesn't want me for himself.");
        clues.add("The man who bought me, doesn't need me for himself.");
        clues.add("The man who needs me, doesn't know he needs me.");
        clues.add("I am often found in graveyards.");

        List<String> answers = new ArrayList<>();
        answers.add("coffin");
        answers.add("grave");
        WhatAmIRiddle coffin = new WhatAmIRiddle(clues, answers);
        riddles.add(coffin);

        clues = new ArrayList<>();
        clues.add("I am alive without breath.");
        clues.add("I am as cold as death.");
        clues.add("I am never thirsty, ever drinking.");
        clues.add("I am dressed in mail, but never clinking");

        answers = new ArrayList<>();
        answers.add("fish");

        WhatAmIRiddle fish = new WhatAmIRiddle(clues, answers);
        riddles.add(fish);
    }

    public int doRiddle () {
        Scanner scanner = new Scanner(System.in);
        int score = clues.size() + 1;
        for (String clue : clues) {
            System.out.println(clue);
            score--;

            System.out.println("Guess? (Hit enter to see next clue). Available points = " + score);
            String response = scanner.nextLine().toLowerCase();
            if (response.equals("")) {
                continue;
            }
            if (answers.contains(response)) {
                System.out.println("You got it! Scored " + score + " points.");
                return score;
            } else {
                System.out.println("You guessed wrong.");
                score--;
            }
        }
        return 0;
    }

    public static void main(String[] args) {
        System.out.println("Let's play a riddle game. You get one point per riddle solved.");
        System.out.println("Clues will be revealed in order and you get 1 point per clue you don't look at.");
        System.out.println("Just hit enter to see the next clue. Otherwise, type your guess.");
        System.out.println("Guess wisely! Each wrong guess costs you a point.");

        buildRiddles();
        int score = riddles.stream()
                .mapToInt(e -> e.doRiddle())
                .sum();

        System.out.println("You scored a final score of " + score);
    }
}
