package eu.linkedtv.keywords.v1.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class TextFile implements Serializable, ITextFile {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private int fileId;

    private String name;
    
    @Column(name = "original_text")
    private String originalText;

    /* (non-Javadoc)
     * @see eu.linkedtv.keywords.v1.models.ITextFile#getFileId()
     */
    @Override
    public int getFileId() {
        return this.fileId;
    }

    /* (non-Javadoc)
     * @see eu.linkedtv.keywords.v1.models.ITextFile#setFileId(int)
     */
    @Override
    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    /* (non-Javadoc)
     * @see eu.linkedtv.keywords.v1.models.ITextFile#getName()
     */
    @Override
    public String getName() {
        return this.name;
    }

    /* (non-Javadoc)
     * @see eu.linkedtv.keywords.v1.models.ITextFile#setName(java.lang.String)
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }
    
    public abstract void addKeywordOccurrence(IKeywordsOccurrence kwo);

    /* (non-Javadoc)
     * @see eu.linkedtv.keywords.v1.models.ITextFile#getOriginalText()
     */
    @Override
    public String getOriginalText() {
        return originalText;
    }

    /* (non-Javadoc)
     * @see eu.linkedtv.keywords.v1.models.ITextFile#setOriginalText(java.lang.String)
     */
    @Override
    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }
}