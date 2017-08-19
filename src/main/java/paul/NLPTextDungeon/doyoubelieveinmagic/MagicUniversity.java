package paul.NLPTextDungeon.doyoubelieveinmagic;

import paul.NLPTextDungeon.parsing.SpellType;
import paul.NLPTextDungeon.parsing.WordGroup;
import paul.NLPTextDungeon.parsing.WordType;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by pauldennis on 8/19/17.
 */
public class MagicUniversity {


    private Scanner scanner;

    private List<WordGroup> wordGroups;
    private List<String> words;

    public MagicUniversity () {
        scanner = new Scanner(System.in);
        initSpellWordGroups();
    }



    private void run () {
        while (true) {
            System.out.println("Welcome to Magic University (MagicU - Go Fightin' Tomes!). What do you want to learn today?");

            String response = scanner.nextLine();

            System.out.println("Regex match was: " + getRegexMatch(response));
        }
    }

    public static void main(String[] args) {
        new MagicUniversity().run();
    }


    private String getRegexMatch (String userInput) {
        List<String> regexMatches = new ArrayList<>();
        /*wordGroups.forEach(wordGroup -> {
                    wordGroup.getRelatedWords().stream()
                            .filter(word -> word.endsWith("-")) //Words with a dash mean match by regex
                            .map(word -> word.substring(0, word.length() - 1)) //Remove the dash
                            .filter(word -> userInput.matches("^" + word + "[a-z]*"))
                            .forEach(regexMatches::add);
                            //Match definition: "fire-" matches to any word starting with those letters with zero or
                            //more letters (i.e. "fireblast", "firebolt", "fireball", etc)
                });*/
        regexMatches = words.stream()
                .filter(word -> word.endsWith("-"))
                .map(word -> word.substring(0, word.length() - 1))
                .filter(word -> userInput.matches("^" + word + "[a-z]*"))
                .collect(Collectors.toList());

        if (regexMatches.size() > 1) {
            throw new AssertionError("Only should be finding one match");
        } else if (regexMatches.size() == 1) {
            return regexMatches.get(0);
        } else {
            return null;
        }
    }

    private void initSpellWordGroups () {
        wordGroups = new ArrayList<>();
        words = new ArrayList<>();
        File wordAssocFile = new File("spell_wordgroups.txt");
        SpellType currentType = null;
        try (Scanner fileScanner = new Scanner(wordAssocFile)) {
            while (fileScanner.hasNext()) {
                String token = fileScanner.nextLine();
                if (token.startsWith("|") || token.trim().equals("")) {
                    continue;
                }
                if (token.startsWith("@")) {
                    currentType = SpellType.getTypeFromFileAnnotation(token);
                    continue;
                }
                String primaryWord = token.split(":")[0].trim();
                String[] associatedWords = token.split(":")[1].split(",");
                int index = 0;
                for (String s : associatedWords) {
                    associatedWords[index] = s.trim().toLowerCase();
                    index++;
                }
                Set<String> wordSet = new HashSet<>();
                wordSet.addAll(Arrays.asList(associatedWords));
                words.addAll(wordSet);
                words.add(primaryWord);
                WordGroup wg = new WordGroup(primaryWord, wordSet, currentType);
                wordGroups.add(wg);
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
