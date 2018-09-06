package paul.TextQuest.parsing;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

import paul.TextQuest.entities.DungeonRoom;

/**
 * Created by Paul Dennis on 8/10/2017.
 */
public class StatementAnalyzer {

    private WordGroupMap wordMap;

    private List<WordGroup> wordGroups;

    private DungeonRoom location;

    private static StatementAnalyzer instance;

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
                    analysis.setAnalysis("move", directionWords.get(0), true);
                } else {
                    analysis.addComment("Received move param but no direction. Move where?");
                }
            //This part is genuinely terrible. TODO please fix
            } else if (actionWord.equals("say") || actionWord.equals("whisper") || actionWord.equals("shout")) {
                analysis.setActionable(true);
                analysis.setActionWord(actionWord);
            } else if (actionWord.equals("cast")) {
                MagicUniversity magicUniversity = MagicUniversity.getInstance();
                String[] tokens = analysis.getTokens();
                List<String> matches = Arrays.stream(tokens)
                        .map(magicUniversity::getSpellMatch)
                        .filter(e -> e != null)
                        .collect(Collectors.toList());
                if (matches.size() > 0) {
                    analysis.setActionable(true);
                    analysis.setActionParam(matches.get(0));
                    analysis.setActionWord("cast");
                } else {
                    analysis.addComment("Could not find a spell to cast");
                }
            } else if (actionWord.equals("search")) {
                Set<String> hiddenItemLocations = location.getHiddenItems().keySet();
                String[] tokens = analysis.getTokens();
                List<String> matches = Arrays.stream(tokens)
                        .filter(hiddenItemLocations::contains)
                        .collect(Collectors.toList());
                if (matches.size() > 0) {
                    String param = matches.get(0);
                    analysis.setActionable(true);
                    analysis.setActionParam(param);
                    analysis.setActionWord(actionWord);
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
                        if (nonmatches.size() > 1) {
                            analysis.addComment("Can only search one place at a time.");
                        }
                    }
                }
            } else if (actionWord.equals("use")) {
            	String[] tokens = analysis.getTokens();
            	List<String> itemNames = location.getHero().getBackpack().getItems().stream()
            			.map(item -> item.getName().toLowerCase())
            			.collect(Collectors.toList());
            	List<String> matches = Arrays.stream(tokens)
            			.filter(token -> itemNames.contains(token))
            			.collect(Collectors.toList());
            	if (matches.size() > 0) {
            		String param = matches.get(0);
            		analysis.setActionable(true);
            		analysis.setActionParam(param);
            		analysis.setActionWord("use");
            		if (matches.size() > 1) {
            			analysis.addComment("Can only use one item at a time.");
            		}
            	}
            } else if (actionWord.equals("drop")) {
            	String[] tokens = analysis.getTokens();
            	String itemName = "";
            	for (int i = 1; i < tokens.length; i++) {
            		itemName += tokens[i];
            		if (i + 1 < tokens.length) {
            			itemName += " ";
            		}
            	}
            	analysis.setActionable(true);
            	analysis.setActionWord("drop");
            	analysis.setActionParam(itemName);
            } else if (actionWord.equals("equip")) {
            	String[] tokens = analysis.getTokens();
            	String itemName = "";
            	for (int i = 1; i < tokens.length; i++) {
            		itemName += tokens[i];
            		if (i + 1 < tokens.length) {
            			itemName += " ";
            		}
            	}
            	analysis.setActionable(true);
            	analysis.setActionWord("equip");
            	analysis.setActionParam(itemName);
            } else if (actionWord.equals("unequip")) {
            	String[] tokens = analysis.getTokens();
            	String itemName = "";
            	for (int i = 1; i < tokens.length; i++) {
            		itemName += tokens[i];
            		if (i + 1 < tokens.length) {
            			itemName += " ";
            		}
            	}
            	analysis.setActionable(true);
            	analysis.setActionWord("unequip");
            	analysis.setActionParam(itemName);
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
                if (token.startsWith("$")) {
                    System.out.println("Inference = " + token);
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
