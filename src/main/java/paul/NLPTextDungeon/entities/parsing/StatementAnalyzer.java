package paul.NLPTextDungeon.entities.parsing;


import paul.NLPTextDungeon.enums.WordType;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by Paul Dennis on 8/10/2017.
 */
public class StatementAnalyzer {

    private Map<String, WordGroup> wordMap;

    private List<WordGroup> wordGroups;
    private Scanner scanner;

    public StatementAnalyzer () {
        initializeWordGroups();
        initializeWordMap();
        scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        StatementAnalyzer analyzer = new StatementAnalyzer();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Enter String to analyze:");
            String response = scanner.nextLine();
            if (response.equals("")) {
                break;
            }
            analyzer.analyzeStatement(response);
        }
    }

    private String cleanStatement (String statement) {
        String response = statement.toLowerCase().trim()
                .replaceAll("[^a-z ]", "") //Removes everything but letters and spaces
                .replaceAll(" {2,}", " ") //Removes extra spaces (leaves one space between words)
                .trim(); //Just for the heck of it.
        return response;
    }

    private StatementAnalysis parseStatement (String statement) {
        StatementAnalysis analysis;
        if (statement.contains("and")) {
            String[] andSplit = statement.split("and");
            String[] firstTokens = andSplit[0].split(" ");
            String[] secondTokens = andSplit[1].split(" ");
            analysis = new StatementAnalysis(statement, true, firstTokens, secondTokens);
        } else {
            analysis = new StatementAnalysis(statement, statement.split(" "));
        }
        return analysis;
    }

    private StatementAnalysis finalAnalysis (StatementAnalysis analysis) {
        List<String> voidActionWords = analysis.getTokenMatchMap().get(WordType.VOID_ACTION);
        List<String> paramActionWords = analysis.getTokenMatchMap().get(WordType.PARAM_ACTION);
        if (voidActionWords.size() > 0) {
            analysis.setAnalysis(voidActionWords.get(0), null, true);
            if (paramActionWords.size() > 0) {
                analysis.addComment("Ignoring param action words.");
            }
            if (voidActionWords.size() > 1) {
                analysis.addComment("Ignoring extra void action words.");
            }
        } else if (paramActionWords.size() > 0) {
            if (paramActionWords.size() > 1) {
                analysis.addComment("Ignoring extra param action words.");
            }
            String actionWord = paramActionWords.get(0);
            if (actionWord.equals("move")) {
                List<String> directionWords = analysis.getTokenMatchMap().get(WordType.DIRECTION);
                if (directionWords.size() > 0) {
                    analysis.setAnalysis("move", directionWords.get(0), true);
                } else {
                    analysis.addComment("Received move param but no direction. Move where?");
                }
            } else {
                List<String> conceptWords = analysis.getTokenMatchMap().get(WordType.CONCEPT);
                if (conceptWords.size() > 0) {
                    analysis.setAnalysis(actionWord,conceptWords.get(0), true);
                    if (conceptWords.size() > 1) {
                        analysis.addComment("Ignoring extra concept words.");
                    }
                } else {
                    analysis.addComment("Received param action word but no concepts to act on." +
                            "\ni.e. " + actionWord + " on what?");
                }
            }
        } else {
            analysis.addComment("No action words.");
        }
        if (analysis.hasAnd()) {
            System.out.println("Detected and. analyzing");
            StatementAnalysis andAnalysis = new StatementAnalysis(analysis.getOriginalStatement() +
                    " JUST THE AND BIT", analysis.getSecondTokens());
            andAnalysis = finalAnalysis(findTokenMatches(andAnalysis));
            System.out.println(andAnalysis);
            analysis.setSecondAnalysis(andAnalysis.getActionWord(), andAnalysis.getActionParam(), true);
            System.out.println(analysis);
        }
        return analysis;
    }

    private StatementAnalysis findTokenMatches (StatementAnalysis analysis) {
        Arrays.stream(analysis.getTokens())
                .filter(word -> wordMap.get(word) != null)
                .forEach(word -> {
                    WordGroup wg = wordMap.get(word);
                    analysis.addTokenMatch(wg.getCoreWord(), wg.getType());
                });
        if (analysis.hasAnd()) {
            Arrays.stream(analysis.getSecondTokens())
                    .filter(word -> wordMap.get(word) != null)
                    .forEach(word -> {
                        WordGroup wg = wordMap.get(word);
                        analysis.addSecondTokenMatch(wg.getCoreWord(), wg.getType());
                    });
        }
        return analysis;
    }

    public StatementAnalysis analyzeStatement (String statement) {
        String cleaned = cleanStatement(statement);
        StatementAnalysis analysis = finalAnalysis(findTokenMatches(parseStatement(cleaned)));
        return analysis;
    }

    private void updateWordMap (WordGroup changedGroup) {
        Set<String> words = changedGroup.getRelatedWords();
        words.add(changedGroup.getCoreWord());

        words.forEach(e -> wordMap.put(e, changedGroup));
    }

    private void learnNewAssociation () {
        Scanner scanner = new Scanner(System.in);
        System.out.println("No hits. Would you like to teach me an association? (y/n");
        String yesNoResponse = scanner.nextLine().toLowerCase();
        if (yesNoResponse.contains("y")) {
            System.out.println("Add to an existing association group or create new?(add/new)");
            String userResponse = scanner.nextLine().toLowerCase();
            if (userResponse.contains("add")) {
                System.out.println("Enter word from an existing association group you want to add to:");
                String existingWord = scanner.nextLine().trim().toLowerCase();
                WordGroup wg = wordMap.get(existingWord);
                if (wg == null) {
                    System.out.println("Sorry, cannot find the association. Start over?");
                    yesNoResponse = scanner.nextLine().toLowerCase();
                    if (yesNoResponse.contains("y")) {
                        System.out.println("For reference here are the possibilities for existing words:");
                        wordMap.keySet().forEach(e -> System.out.print(e + " "));
                        learnNewAssociation();
                    }
                } else {
                    System.out.println("Enter word(s) to add to association with "
                            + existingWord + "(empty string \"\" to exit)");
                    String wordToAdd;
                    while (true) {
                        wordToAdd = scanner.nextLine();
                        if (wordToAdd.equals("")) {
                            break;
                        }
                        wg.addRelatedWord(wordToAdd);
                    }
                    System.out.println("Printing updated word group");
                    updateWordMap(wg);
                    System.out.println(wg);
                }
            } else if (userResponse.contains("new")) {
                System.out.println("Enter new primary word");
                String newWord = scanner.nextLine().trim().toLowerCase();
                if (wordMap.get(newWord) != null) {
                    System.out.println("That word is already mapped, sorry.");
                } else {
                    WordGroup newGroup = new WordGroup(newWord, new HashSet<>(), null);
                    //TODO fix: causes AssertionError directly
                    System.out.println("Enter word(s) to associate with " + newWord + " (empty string\"\" to exit):");
                    String wordToAdd;
                    while (true) {
                        wordToAdd = scanner.nextLine();
                        if (wordToAdd.equals("")) {
                            break;
                        }
                        newGroup.addRelatedWord(wordToAdd);
                    }
                    System.out.println("Printing new word group");
                    updateWordMap(newGroup);
                    System.out.println(newGroup);
                }
            } else {
                System.out.println("Could not parse response.");
            }
        } else if (yesNoResponse.contains("n")) {
            System.out.println("OK. No problem.");
        } else {
            System.out.println("Response did not contain a yes or no. (Defaulted to no)");
        }
    }

    private void initializeWordMap () {
        wordMap = new HashMap<>();
        wordGroups.forEach(e -> wordMap.put(e.getCoreWord(), e));

        wordGroups.forEach(wordGroup -> {
            wordGroup.getRelatedWords()
                    .forEach(word -> wordMap.put(word, wordGroup));
        });
    }

    private void initializeWordGroups () {
        wordGroups = new ArrayList<>();
        File wordAssocFile = new File("word_association.txt");
        WordType currentType = null;
        try (Scanner fileScanner = new Scanner(wordAssocFile)) {
            while (fileScanner.hasNext()) {
                String token = fileScanner.nextLine();
                if (token.startsWith("|") || token.trim().equals("")) {
                    continue;
                }
                if (token.startsWith("@")) {
                    currentType = WordType.getTypeFromFileAnnotation(token);
                    continue;
                }
                String primaryWord = token.split("-")[0].trim();
                String[] associatedWords = token.split("-")[1].split(",");
                int index = 0;
                for (String s : associatedWords) {
                    associatedWords[index] = s.trim().toLowerCase();
                    index++;
                }
                Set<String> wordSet = new HashSet<>();
                Arrays.stream(associatedWords).forEach(wordSet::add);
                WordGroup wg = new WordGroup(primaryWord, wordSet, currentType);
                wordGroups.add(wg);
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
