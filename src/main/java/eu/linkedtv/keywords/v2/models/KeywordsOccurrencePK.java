package eu.linkedtv.keywords.v2.models;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the keywords_occurrences database table.
 * 
 */
@Embeddable
public class KeywordsOccurrencePK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="keyword_id")
	private int keywordId;

	@Column(name="file_id")
	private int fileId;

    public KeywordsOccurrencePK() {
    }
	public int getKeywordId() {
		return this.keywordId;
	}
	public void setKeywordId(int keywordId) {
		this.keywordId = keywordId;
	}
	public int getFileId() {
		return this.fileId;
	}
	public void setFileId(int fileId) {
		this.fileId = fileId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof KeywordsOccurrencePK)) {
			return false;
		}
		KeywordsOccurrencePK castOther = (KeywordsOccurrencePK)other;
		return 
			(this.keywordId == castOther.keywordId)
			&& (this.fileId == castOther.fileId);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.keywordId;
		hash = hash * prime + this.fileId;
		
		return hash;
    }
}