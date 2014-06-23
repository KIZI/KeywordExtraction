package eu.linkedtv.keywords.v1.models;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * The persistent class for the keywords_occurrences database table.
 * 
 */
@Entity
@Table(name="nl_keywords_occurrences")
public class DutchKeywordsOccurrence extends KeywordsOccurrence {

    private static final long serialVersionUID = 1L;
    
    //bi-directional many-to-one association to Keyword
    @ManyToOne
    @JoinColumn(name="keyword_id", nullable=false, insertable=false, updatable=false)
    private DutchKeyword keyword;
    
    //bi-directional many-to-one association to TextFile
    @ManyToOne
    @JoinColumn(name="file_id", nullable=false, insertable=false, updatable=false)
    protected DutchTextFile file;
    
    public DutchKeyword getKeyword() {
        return this.keyword;
    }

    public void setKeyword(DutchKeyword keyword) {
        this.keyword = keyword;
        
        if (id == null)
            id = new KeywordsOccurrencePK();
        id.setKeywordId(keyword.getKeywordId());
    } 
    
    @Override
    public void setKeyword(IKeyword keyword) {
        setKeyword((DutchKeyword) keyword);
    }
    
    public DutchTextFile getFile() {
        return file;
    }

    public void setFile(DutchTextFile file) {
        this.file = file;
        
        if (id == null)
            id = new KeywordsOccurrencePK();
        id.setFileId(file.getFileId());
    }

    @Override
    public void setFile(ITextFile file) {
        this.file = (DutchTextFile) file;
        
        if (id == null)
            id = new KeywordsOccurrencePK();
        id.setFileId(file.getFileId());
    }
}