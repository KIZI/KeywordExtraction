package eu.linkedtv.keywords.v2.models;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import eu.linkedtv.keywords.v1.models.IKeyword;
import eu.linkedtv.keywords.v1.models.IKeywordsOccurrence;
import eu.linkedtv.keywords.v1.models.ITextFile;

/**
 * The persistent class for the keywords_occurrences database table.
 * 
 */
@Entity
@Table(name="keywords_occurrences")
public class KeywordsOccurrence implements Serializable, IKeywordsOccurrence {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	protected KeywordsOccurrencePK id;
	
    //bi-directional many-to-one association to Keyword
    @ManyToOne
    @JoinColumn(name="keyword_id", nullable=false, insertable=false, updatable=false)
    private Keyword keyword;
    
    //bi-directional many-to-one association to TextFile
    @ManyToOne
    @JoinColumn(name="file_id", nullable=false, insertable=false, updatable=false)
    protected TextFile file;	

	protected int count;
	
	protected double tf;

    public double getTf() {
        return tf;
    }

    public void setTf(double tf) {
        this.tf = tf;
    }
    
	public int getCount() {
		return this.count;
	}
	
	public void incrementCount() {
	    count++;
	}

	public void setCount(int count) {
		this.count = count;
	}

    public void setKeyword(IKeyword keyword) {
        this.keyword = (Keyword) keyword;
        
        if (id == null)
            id = new KeywordsOccurrencePK();
        id.setKeywordId(keyword.getKeywordId());
    }  

    public IKeyword getKeyword() {
        return keyword;
    }
    
    public ITextFile getFile() {
        return file;
    }

    public void setFile(ITextFile file) {
        this.file = (TextFile) file;
        
        if (id == null)
            id = new KeywordsOccurrencePK();
        id.setFileId(file.getFileId());
    }
}