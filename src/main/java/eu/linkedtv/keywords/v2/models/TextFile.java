package eu.linkedtv.keywords.v2.models;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import eu.linkedtv.keywords.v1.models.IKeywordsOccurrence;
import eu.linkedtv.keywords.v1.models.ITextFile;
import eu.linkedtv.keywords.v2.languages.SupportedLanguages;

@Entity
@Table(name = "files")
public class TextFile implements Serializable, ITextFile {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private int fileId;
    
    // bi-directional many-to-one association to IKeywordsOccurrence
    @OneToMany(mappedBy = "file")
    private List<KeywordsOccurrence> keywordsOccurrences;
    
    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    protected User user;    

    private String name;
    
    @Column(name = "original_text")
    private String originalText;
    
    @Column(name = "language_id")
    private Integer languageId;

    public Integer getLanguageId() {
        return languageId;
    }

    public void setLanguageId(SupportedLanguages language) {
        this.languageId = language.getLanguageId();
    }

    public int getFileId() {
        return this.fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public List<KeywordsOccurrence> getKeywordsOccurrences() {
        return this.keywordsOccurrences;
    }

    public void setKeywordsOccurrences(List<KeywordsOccurrence> keywordsOccurrences) {
        this.keywordsOccurrences = keywordsOccurrences;
    }    
    
    public void addKeywordOccurrence(IKeywordsOccurrence kwo) {
        keywordsOccurrences.add((KeywordsOccurrence) kwo);
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }
    
    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}