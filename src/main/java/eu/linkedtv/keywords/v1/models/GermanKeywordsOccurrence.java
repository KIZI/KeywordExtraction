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
@Table(name="de_keywords_occurrences")
public class GermanKeywordsOccurrence extends KeywordsOccurrence {

    private static final long serialVersionUID = 1L;
    
    //bi-directional many-to-one association to Keyword
    @ManyToOne
    @JoinColumn(name="keyword_id", nullable=false, insertable=false, updatable=false)
    private GermanKeyword keyword;
    
    //bi-directional many-to-one association to TextFile
    @ManyToOne
    @JoinColumn(name="file_id", nullable=false, insertable=false, updatable=false)
    protected GermanTextFile file;
    
    public GermanKeyword getKeyword() {
        return keyword;
    }

    public void setKeyword(GermanKeyword keyword) {
        this.keyword = keyword;
        
        if (id == null)
            id = new KeywordsOccurrencePK();
        id.setKeywordId(keyword.getKeywordId());
    }  
    
    @Override
    public void setKeyword(IKeyword keyword) {
        setKeyword((GermanKeyword) keyword);
    }    
    
    public GermanTextFile getFile() {
        return file;
    }

    public void setFile(GermanTextFile file) {
        this.file = file;
        
        if (id == null)
            id = new KeywordsOccurrencePK();
        id.setFileId(file.getFileId());
    }
    
    @Override
    public void setFile(ITextFile file) {
        this.file = (GermanTextFile) file;
        
        if (id == null)
            id = new KeywordsOccurrencePK();
        id.setFileId(file.getFileId());
    }
}