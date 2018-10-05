package paul.TextQuest.parsing;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

import paul.TextQuest.TextInterface;
import paul.TextQuest.entities.DungeonRoom;
import paul.TextQuest.utils.StringUtils;

/**
 * Created by Paul Dennis on 8/10/2017.
 */
public class StatementAnalyzer {

    private WordGroupMap wordMap;

    private List<WordGroup> wordGroups;

    private DungeonRoom location;

    private static StatementAnalyzer instance;
    
    public static final String WORD_ASSOCIATION_FILE = "word_associations/word_association.txt";

    private StatementAnalyzer () {
        initializeWordGroups();
        initializeWordMap();
    }

    public static StatementAnalyzer getInstance () {
        if (instance == null) {
            instance = new StatementAnalyzer();
        }
        return instance;
    }

    private String cleanStatement (String statement) {
        return statement.toLowerCase().trim()
                .replaceAll("[^a-z ]", "") //Removes everything but letters and spaces
                .replaceAll(" {2,}", " ") //Removes extra spaces (leaves one space between words)
                .trim(); //Just for the heck of it.
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
        List<String> specialRoomActionWords;
        if (location != null) {
            specialRoomActionWords = new ArrayList<>(location.getSpecialRoomActions().keySet());
        } else {
            specialRoomActionWords = new ArrayList<>();
        }


        //TODO Consider moving some of this functionality into other classes like Hero
        //i.e. let the Hero class worry about how to cast a spell, etc. Forcing this class
        //to care about those things is bloating it.
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
                    analysis.setAnalysis("move", directionWords.get(0));
                } else {
                    analysis.addComment("Received move param but no direction. Move where?");
                }
            //This part is genuinely terrible. TODO please fix
            } else if (actionWord.equals("say") || actionWord.equals("whisper") || actionWord.equals("shout")) {
                analysis.setActionable(true);
                analysis.setActionWord(actionWord);
            } else if (actionWord.equals("search")) {
                Set<String> hiddenItemLocations = location.getHiddenItems().keySet();
                String[] tokens = analysis.getTokens();
                List<String> matches = Arrays.stream(tokens)
                        .filter(hiddenItemLocations::contains)
                        .collect(Collectors.toList());
                if (matches.size() > 0) {
                    String param = matches.get(0);
                    analysis.setAnalysis(actionWord, param);
                    if (matches.size() > 1) {
                        analysis.addComment("Can only search one place at a time.");
                    }
                } else {
                    //There are no matches.
                    //We have to find what they tried to search for that isn't a match
                    List<String> nonmatches = Arrays.stream(tokens)
                            .filter(token -> {
                                if (token.equals("search")) return false;
                                if (token.equals("look")) return false;
                                if (token.equals("examine")) return false;
                                return true;
                            }).collect(Collectors.toList());
                    if (nonmatches.size() > 0) {
                        String param = nonmatches.get(0);
                        analysis.setActionable(true);
                        analysis.setActionWord(actionWord);
                        analysis.setActionParam(param);
                        analysis.setAnalysis(actionWord, param);
                        if (nonmatches.size() > 1) {
                            analysis.addComment("Can only search one place at a time.");
                        }
                    }
                }
            } else if (actionWord.equals("use")) {
            	String[] tokens = analysis.getTokens();
            	List<String> itemNames = location.getHero().getBackpack().stream()
            			.map(item -> item.getName().toLowerCase())
            			.collect(Collectors.toList());
            	List<String> matches = Arrays.stream(tokens)
            			.filter(token -> itemNames.contains(token))
            			.collect(Collectors.toList());
            	if (matches.size() > 0) {
            		String param = matches.get(0);
            		analysis.setAnalysis("use", param);
            		
            		if (matches.size() > 1) {
            			analysis.addComment("Can only use one item at a time.");
            		}
            	}
            } else if (actionWord.equals("drop") || actionWord.equals("insert") || actionWord.equals("equip")
            		|| actionWord.equals("unequip") || actionWord.equals("viewspells") || actionWord.equals("cast")
            		|| actionWord.equals("detail")) {
            	String squished = StringUtils.squishTokens(analysis.getTokens());
            	analysis.setAnalysis(actionWord, squished);
            } else {
                List<String> conceptWords = analysis.getTokenMatchMap().get(WordType.CONCEPT);
                if (conceptWords.size() > 0) {
                    analysis.setAnalysis(actionWord, conceptWords.get(0));
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
            StatementAnalysis andAnalysis = new StatementAnalysis(analysis.getOriginalStatement() +
                    " JUST THE AND BIT", analysis.getSecondTokens());
            andAnalysis = finalAnalysis(findTokenMatches(andAnalysis));
            analysis.setSecondAnalysis(andAnalysis.getActionWord(), andAnalysis.getActionParam(), true);
        }
        if (specialRoomActionWords.size() > 0) {
            if (analysis.hasAnd()) {
                throw new AssertionError("Not supported");
            }
            String[] tokens = analysis.getTokens();
            List<String> matches = Arrays.stream(tokens)
                    .filter(specialRoomActionWords::contains)
                    .collect(Collectors.toList());
            if (matches.size() > 0) {
                if (analysis.isActionable()) {
                    analysis.addComment("Already had actionable analysis. Overriding with special room action");
                    analysis.addComment(analysis.getActionWord() + " overridden.");
                }
                analysis.setActionWord(matches.get(0));
                analysis.setActionable(true);
            }
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

    public StatementAnalysis analyzeStatement (String statement, DungeonRoom location) {
        String quote = null;
        this.location = location;
        if (statement.contains("\"")) {
            int beginIndex = statement.indexOf("\"") + 1;
            int endIndex = statement.lastIndexOf("\"");
            quote = statement.substring(beginIndex, endIndex);
            statement = statement.substring(0, beginIndex - 1) + statement.substring(endIndex);
        }
        String cleaned = cleanStatement(statement);
        StatementAnalysis analysis = finalAnalysis(findTokenMatches(parseStatement(cleaned)));
        if (quote != null) {
            analysis.setActionParam(quote);
            analysis.setActionable(true);
        }
        return analysis;
    }

    //This method should only be used if location doesn't matter (no searching)
    //Will cause NPE otherwise
    public StatementAnalysis analyzeStatement (String statement) {
        return analyzeStatement(statement, null);
    }

    private void updateWordMap (WordGroup changedGroup) {
        Set<String> words = changedGroup.getRelatedWords();
        words.add(changedGroup.getCoreWord());

        words.forEach(e -> wordMap.put(e, changedGroup));
    }

    public void learnNewAssociation () {
    	TextInterface textOut = new TextInterface();
    	//PrintStream textOut = System.out;
    	Scanner scanner = new Scanner(System.in);
        textOut.println("No hits. Would you like to teach me an association? (y/n");
        String yesNoResponse = scanner.nextLine().toLowerCase();
        if (yesNoResponse.contains("y")) {
        	textOut.println("Add to an existing association group or create new?(add/new)");
            String userResponse = scanner.nextLine().toLowerCase();
            if (userResponse.contains("add")) {
            	textOut.println("Enter word from an existing association group you want to add to:");
                String existingWord = scanner.nextLine().trim().toLowerCase();
                WordGroup wg = wordMap.get(existingWord);
                if (wg == null) {
                	textOut.println("Sorry, cannot find the association. Start over?");
                    yesNoResponse = scanner.nextLine().toLowerCase();
                    if (yesNoResponse.contains("y")) {
                    	textOut.println("For reference here are the possibilities for existing words:");
                        wordMap.keySet().forEach(e -> System.out.print(e + " "));
                        learnNewAssociation();
                    }
                } else {
                	textOut.println("Enter word(s) to add to association with "
                            + existingWord + "(empty string \"\" to exit)");
                    String wordToAdd;
                    while (true) {
                        wordToAdd = scanner.nextLine();
                        if (wordToAdd.equals("")) {
                            break;
                        }
                        wg.addRelatedWord(wordToAdd);
                    }
                    textOut.println("Printing updated word group");
                    updateWordMap(wg);
                    textOut.println(wg);
                }
            } else if (userResponse.contains("new")) {
            	textOut.println("Enter new primary word");
                String newWord = scanner.nextLine().trim().toLowerCase();
                if (wordMap.get(newWord) != null) {
                    textOut.println("That word is already mapped, sorry.");
                } else {
                    WordGroup newGroup = new WordGroup(newWord, new HashSet<>(), null);
                    //TODO fix: causes AssertionError directly
                    textOut.println("Enter word(s) to associate with " + newWord + " (empty string\"\" to exit):");
                    String wordToAdd;
                    while (true) {
                        wordToAdd = scanner.nextLine();
                        if (wordToAdd.equals("")) {
                            break;
                        }
                        newGroup.addRelatedWord(wordToAdd);
                    }
                    textOut.println("Printing new word group");
                    updateWordMap(newGroup);
                    textOut.println(newGroup);
                }
            } else {
            	textOut.println("Could not parse response.");
            }
        } else if (yesNoResponse.contains("n")) {
        	textOut.println("OK. No problem.");
        } else {
        	textOut.println("Response did not contain a yes or no. (Defaulted to no)");
        }
        scanner.close();
    }

    private void initializeWordMap () {
        wordMap = new WordGroupMap();
        wordGroups.forEach(e -> wordMap.put(e.getCoreWord(), e));

        wordGroups.forEach(wordGroup -> wordGroup.getRelatedWords()
                .forEach(word -> wordMap.put(word, wordGroup)));
    }

    private void initializeWordGroups () {
        wordGroups = new ArrayList<>();
        File wordAssocFile = new File(WORD_ASSOCIATION_FILE);
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
                if (token.startsWith("$")) {
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
                WordGroup wg = new WordGroup(primaryWord, wordSet, currentType);
                wordGroups.add(wg);
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
