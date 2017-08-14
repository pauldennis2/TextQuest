package paul.NLPTextDungeon.parsing;


import java.util.Set;

/**
 * Created by Paul Dennis on 8/10/2017.
 */
public class WordGroup {

    private String coreWord;
    private Set<String> relatedWords;
    private WordType type;

    public WordGroup(String coreWord, Set<String> relatedWords, WordType type) {
        if (coreWord == null || relatedWords == null || type == null) {
            throw new AssertionError();
        }
        this.coreWord = coreWord;
        this.relatedWords = relatedWords;
        this.type = type;
    }

    public void addRelatedWord (String relatedWord) {
        relatedWords.add(relatedWord);
    }

    public String getCoreWord() {
        return coreWord;
    }

    public void setCoreWord(String coreWord) {
        this.coreWord = coreWord;
    }

    public Set<String> getRelatedWords() {
        return relatedWords;
    }

    public void setRelatedWords(Set<String> relatedWords) {
        this.relatedWords = relatedWords;
    }

    public WordType getType() {
        return type;
    }

    public void setType(WordType type) {
        this.type = type;
    }

    @Override
    public String toString () {
        String response = coreWord + " - ";
        for (String word : relatedWords) {
            response += word + ", ";
        }
        response += "Type = " + type.toString();
        return response;
    }
}
