package eu.linkedtv.keywords.v1.models;

public interface ITextFile {

    public abstract int getFileId();

    public abstract void setFileId(int fileId);

    public abstract String getName();

    public abstract void setName(String name);

    public abstract String getOriginalText();

    public abstract void setOriginalText(String originalText);
    
    public abstract void addKeywordOccurrence(IKeywordsOccurrence kwo);

}