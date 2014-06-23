package eu.linkedtv.keywords.v1.models;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the keywords_occurrences database table.
 * 
 */
@MappedSuperclass
public abstract class KeywordsOccurrence implements Serializable, IKeywordsOccurrence {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	protected KeywordsOccurrencePK id;

	protected int count;
	
	protected double tf;

    /* (non-Javadoc)
     * @see eu.linkedtv.keywords.v1.models.IKeywordOccurrence#getTf()
     */
    @Override
    public double getTf() {
        return tf;
    }

    /* (non-Javadoc)
     * @see eu.linkedtv.keywords.v1.models.IKeywordOccurrence#setTf(double)
     */
    @Override
    public void setTf(double tf) {
        this.tf = tf;
    }
    
	/* (non-Javadoc)
     * @see eu.linkedtv.keywords.v1.models.IKeywordOccurrence#getCount()
     */
	@Override
    public int getCount() {
		return this.count;
	}
	
	/* (non-Javadoc)
     * @see eu.linkedtv.keywords.v1.models.IKeywordOccurrence#incrementCount()
     */
	@Override
    public void incrementCount() {
	    count++;
	}

	/* (non-Javadoc)
     * @see eu.linkedtv.keywords.v1.models.IKeywordOccurrence#setCount(int)
     */
	@Override
    public void setCount(int count) {
		this.count = count;
	}

    /* (non-Javadoc)
     * @see eu.linkedtv.keywords.v1.models.IKeywordOccurrence#setFile(eu.linkedtv.keywords.v1.models.ITextFile)
     */
    @Override
    public abstract void setFile(ITextFile file);

    /* (non-Javadoc)
     * @see eu.linkedtv.keywords.v1.models.IKeywordOccurrence#setKeyword(eu.linkedtv.keywords.v1.models.Keyword)
     */
    @Override
    public abstract void setKeyword(IKeyword keyword);
}