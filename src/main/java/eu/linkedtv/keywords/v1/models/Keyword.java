package eu.linkedtv.keywords.v1.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;


/**
 * The persistent class for the keywords database table.
 * 
 */
@MappedSuperclass
public abstract class Keyword implements Serializable, IKeyword {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="keyword_id")
	private int keywordId;

	private double idf;

	private String word;

	/* (non-Javadoc)
     * @see eu.linkedtv.keywords.v1.models.IKeyword#getKeywordId()
     */
	@Override
    public int getKeywordId() {
		return this.keywordId;
	}

	/* (non-Javadoc)
     * @see eu.linkedtv.keywords.v1.models.IKeyword#setKeywordId(int)
     */
	@Override
    public void setKeywordId(int keywordId) {
		this.keywordId = keywordId;
	}

	/* (non-Javadoc)
     * @see eu.linkedtv.keywords.v1.models.IKeyword#getIdf()
     */
	@Override
    public double getIdf() {
		return this.idf;
	}

	/* (non-Javadoc)
     * @see eu.linkedtv.keywords.v1.models.IKeyword#setIdf(double)
     */
	@Override
    public void setIdf(double idf) {
		this.idf = idf;
	}

	/* (non-Javadoc)
     * @see eu.linkedtv.keywords.v1.models.IKeyword#getWord()
     */
	@Override
    public String getWord() {
		return this.word;
	}

	/* (non-Javadoc)
     * @see eu.linkedtv.keywords.v1.models.IKeyword#setWord(java.lang.String)
     */
	@Override
    public void setWord(String word) {
		this.word = word;
	}
	
	/* (non-Javadoc)
     * @see eu.linkedtv.keywords.v1.models.IKeyword#addKeywordOccurrence(eu.linkedtv.keywords.v1.models.IKeywordsOccurrence)
     */
	@Override
    public abstract void addKeywordOccurrence(IKeywordsOccurrence kwo);

}