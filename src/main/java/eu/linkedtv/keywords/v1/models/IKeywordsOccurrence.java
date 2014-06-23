package eu.linkedtv.keywords.v1.models;

public interface IKeywordsOccurrence {

    public abstract double getTf();

    public abstract void setTf(double tf);

    public abstract int getCount();

    public abstract void incrementCount();

    public abstract void setCount(int count);

    public abstract void setFile(ITextFile file);

    public abstract void setKeyword(IKeyword keyword);

}