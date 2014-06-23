package eu.linkedtv.keywords.v1.models;

public class CIWord implements Comparable<CIWord>{
    private final String word;
    
    public CIWord(String word) {
        this.word = word;
    }
    
    @Override
    public int hashCode() {
        return word.toLowerCase().hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CIWord) {
            CIWord ciWord = (CIWord) obj;
            return ciWord.word.equalsIgnoreCase(word);
        }
        else
            return false;
    }

    @Override
    public int compareTo(CIWord o) {
        return o.word.compareToIgnoreCase(word);
    }

}
