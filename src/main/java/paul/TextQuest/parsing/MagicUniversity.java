package paul.TextQuest.parsing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by pauldennis on 8/19/17.
 */
@Deprecated
public class MagicUniversity {


    private Scanner scanner;

    private List<WordGroup> wordGroups;
    private Map<String, WordGroup> wordMap;
    private List<String> words;


    //Current primary use is for parsing

    private MagicUniversity () {
        scanner = new Scanner(System.in);
        initSpellWordGroups();
    }

    private static MagicUniversity instance;

    public static MagicUniversity getInstance () {
        if (instance == null) {
            instance = new MagicUniversity();
        }
        return instance;
    }

    public String getSpellMatch (String userInput) {
        if (wordMap.get(userInput) != null) {
            return wordMap.get(userInput).getCoreWord();
        } else {
            return getRegexMatch(userInput);
        }
    }



    private void run () {
        while (true) {
            System.out.println("Welcome to Magic University (MagicU - Go Fightin' Tomes!). What do you want to learn today?");

            String response = scanner.nextLine().toLowerCase().trim();

            System.out.println("Match was: " + getSpellMatch(response));
        }
    }

    public static void main(String[] args) {
        new MagicUniversity().run();
    }


    private String getRegexMatch (String userInput) {
        List<String> regexMatches = words.stream()
                .filter(word -> word.endsWith("-"))//Words ending with dash should be matched to regex
                .map(word -> word.substring(0, word.length() - 1))//Remove the dash
                .filter(word -> userInput.matches("^" + word + "[a-z]*"))//Match to anything starting with the word
                .collect(Collectors.toList());

        if (regexMatches.size() > 1) {
            throw new AssertionError("Only should be finding one match");
        } else if (regexMatches.size() == 1) {
            String match = regexMatches.get(0);
            return wordMap.get(match + "-").getCoreWord();
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

        wordMap = new HashMap<>();

        wordGroups.forEach(wordGroup -> {
                    List<String> words = new ArrayList<>(wordGroup.getRelatedWords());
                    words.add(wordGroup.getCoreWord());
                    words.forEach(word -> wordMap.put(word, wordGroup));
                });
    }
}
