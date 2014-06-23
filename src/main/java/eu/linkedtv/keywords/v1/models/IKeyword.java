package eu.linkedtv.keywords.v1.models;

public interface IKeyword {

    public abstract int getKeywordId();

    public abstract void setKeywordId(int keywordId);

    public abstract double getIdf();

    public abstract void setIdf(double idf);

    public abstract String getWord();

    public abstract void setWord(String word);

    public abstract void addKeywordOccurrence(IKeywordsOccurrence kwo);

}