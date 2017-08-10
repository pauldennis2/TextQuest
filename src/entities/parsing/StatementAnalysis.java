package entities.parsing;

import enums.WordType;

import java.util.*;

/**
 * Created by Paul Dennis on 8/10/2017.
 */
public class StatementAnalysis {

    private final String originalStatement;
    private boolean hasAnd;
    private String[] tokens;
    private String[] secondTokens;

    private String statementAnalysis;
    private String secondStatementAnalysis;

    private List<String> comments;

    private Map<WordType, List<String>> tokenMatchMap;
    private Map<WordType, List<String>> secondTokenMatchMap;

    public StatementAnalysis(String originalStatement, String[] tokens) {
        this.originalStatement = originalStatement;
        this.tokens = tokens;

        hasAnd = false;
        tokenMatchMap = new HashMap<>();
        comments = new ArrayList<>();
        Arrays.stream(WordType.values()).forEach(e -> tokenMatchMap.put(e, new ArrayList<>()));
    }

    public StatementAnalysis(String originalStatement, boolean hasAnd, String[] tokens, String[] secondTokens) {
        this.originalStatement = originalStatement;
        this.hasAnd = hasAnd;
        this.tokens = tokens;
        this.secondTokens = secondTokens;

        tokenMatchMap = new HashMap<>();
        secondTokenMatchMap = new HashMap<>();
        comments = new ArrayList<>();
        Arrays.stream(WordType.values()).forEach(e -> tokenMatchMap.put(e, new ArrayList<>()));
        Arrays.stream(WordType.values()).forEach(e -> secondTokenMatchMap.put(e, new ArrayList<>()));
    }

    public void printAnalysis () {
        System.out.println("Analysis of \"" + originalStatement + "\":");
        System.out.print("Matches: ");

        tokenMatchMap.keySet()
                .forEach(wordType -> {
                    List<String> tokenMatches = tokenMatchMap.get(wordType);
                    if (tokenMatches.size() > 0) {
                        System.out.print("\n" + wordType + ":");
                        tokenMatches.forEach(word -> System.out.print(word + ", "));
                        System.out.println();
                    }
                });
        if (hasAnd) {
            System.out.print("2nd part matches: ");
            secondTokenMatchMap.keySet()
                    .forEach(wordType -> {
                        List<String> tokenMatches = secondTokenMatchMap.get(wordType);
                        if (tokenMatches.size() > 0) {
                            System.out.print("\n" + wordType + ":");
                            tokenMatches.forEach(word -> System.out.print(word + ", "));
                            System.out.println();
                        }
                    });
        }
        System.out.println();
    }

    public void printFinalAnalysis () {
        System.out.println("Final analysis of \"" + originalStatement + "\":");
        System.out.println(statementAnalysis);
        if (comments.size() > 0) {
            System.out.println("Comments:");
            comments.forEach(e -> System.out.println("\t" + e));
        }
    }

    public String getOriginalStatement() {
        return originalStatement;
    }

    public boolean hasAnd() {
        return hasAnd;
    }

    public String[] getTokens() {
        return tokens;
    }

    public String[] getSecondTokens() {
        return secondTokens;
    }

    public String getStatementAnalysis() {
        return statementAnalysis;
    }

    public void setStatementAnalysis(String statementAnalysis) {
        this.statementAnalysis = statementAnalysis;
    }

    public String getSecondStatementAnalysis() {
        return secondStatementAnalysis;
    }

    public void setSecondStatementAnalysis(String secondStatementAnalysis) {
        this.secondStatementAnalysis = secondStatementAnalysis;
    }

    public void addTokenMatch (String match, WordType type) {
        tokenMatchMap.get(type).add(match);
    }

    public void addSecondTokenMatch (String match, WordType type) {
        secondTokenMatchMap.get(type).add(match);
    }

    public Map<WordType, List<String>> getTokenMatchMap() {
        return tokenMatchMap;
    }

    public Map<WordType, List<String>> getSecondTokenMatchMap() {
        return secondTokenMatchMap;
    }

    public List<String> getComments() {
        return comments;
    }

    public void addComment(String comment) {
        comments.add(comment);
    }
}