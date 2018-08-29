package paul.TextQuest.parsing;


import java.util.*;

/**
 * Created by Paul Dennis on 8/10/2017.
 */
public class StatementAnalysis {

    private final String originalStatement;
    private boolean hasAnd;
    private String[] tokens;
    private String[] secondTokens;

    private List<String> comments;

    private Map<WordType, List<String>> tokenMatchMap;
    private Map<WordType, List<String>> secondTokenMatchMap;

    private String actionWord;
    private String actionParam;
    private boolean actionable;

    private String secondActionWord;
    private String secondActionParam;
    private boolean secondActionable;

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

    public StatementAnalysis (String actionWord, String actionParam) {
        originalStatement = "second half of an AND";
        this.actionable = true;
        this.actionWord = actionWord;
        this.actionParam = actionParam;

        tokenMatchMap = new HashMap<>();
        secondTokenMatchMap = new HashMap<>();
        comments = new ArrayList<>();
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
        System.out.println("Action Word: " + actionWord + ", Param Word: " + actionParam + ", Actionable: " + actionable);
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

    public void setAnalysis (String actionWord, String actionParam, boolean actionable) {
        this.actionWord = actionWord;
        this.actionParam = actionParam;
        this.actionable = actionable;
    }

    public void setSecondAnalysis (String secondActionWord, String secondActionParam, boolean secondActionable) {
        this.secondActionWord = secondActionWord;
        this.secondActionParam = secondActionParam;
        this.secondActionable = secondActionable;
    }

    public void addTokenMatch (String match, Enum<?> type) {
        tokenMatchMap.get(type).add(match);
    }

    public void addSecondTokenMatch (String match, Enum<?> type) {
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

    public boolean isActionable () {
        return actionable;
    }

    public String getActionWord() {
        return actionWord;
    }

    public String getActionParam() {
        return actionParam;
    }

    public boolean isSecondActionable () {
        return secondActionable;
    }

    public String getSecondActionWord() {
        return secondActionWord;
    }

    public String getSecondActionParam() {
        return secondActionParam;
    }

    @Override
    public String toString() {
        return "StatementAnalysis{" +
                "originalStatement='" + originalStatement + '\'' +
                ", hasAnd=" + hasAnd +
                ", tokens=" + Arrays.toString(tokens) +
                ", secondTokens=" + Arrays.toString(secondTokens) +
                ", comments=" + comments +
                ", tokenMatchMap=" + tokenMatchMap +
                ", secondTokenMatchMap=" + secondTokenMatchMap +
                ", actionWord='" + actionWord + '\'' +
                ", actionParam='" + actionParam + '\'' +
                ", actionable=" + actionable +
                ", secondActionWord='" + secondActionWord + '\'' +
                ", secondActionParam='" + secondActionParam + '\'' +
                ", secondActionable=" + secondActionable +
                '}';
    }

    public void setActionParam(String actionParam) {
        this.actionParam = actionParam;
    }

    public void setActionable (boolean actionable) {
        this.actionable = actionable;
    }

    public void setActionWord(String actionWord) {
        this.actionWord = actionWord;
    }
}