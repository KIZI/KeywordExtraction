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

import eu.linkedtv.keywords.v1.models.IKeyword;
import eu.linkedtv.keywords.v1.models.IKeywordsOccurrence;

/**
 * The persistent class for the keywords database table.
 *
 */
@Entity
@Table(name = "keywords")
public class Keyword implements Serializable, IKeyword {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "keyword_id")
    private int keywordId;

    private int d;
    private double idf;

    private String word;

    @Column(name = "language_id")
    private int languageId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    protected User user;

    //bi-directional many-to-one association to KeywordsOccurrence
    @OneToMany(mappedBy = "keyword")
    private List<KeywordsOccurrence> keywordsOccurrences;

    @Override
    public int getKeywordId() {
        return this.keywordId;
    }

    @Override
    public void setKeywordId(int keywordId) {
        this.keywordId = keywordId;
    }

    public int getLanguageId() {
        return languageId;
    }

    public void setLanguageId(int languageId) {
        this.languageId = languageId;
    }

    public int getD() {
        return d;
    }

    public void setD(int d) {
        this.d = d;
    }

    @Override
    public String getWord() {
        return this.word;
    }

    @Override
    public void setWord(String word) {
        this.word = word;
    }

    public List<KeywordsOccurrence> getKeywordsOccurrences() {
        return this.keywordsOccurrences;
    }

    public void setKeywordsOccurrences(List<KeywordsOccurrence> keywordsOccurrences) {
        this.keywordsOccurrences = keywordsOccurrences;
    }

    @Override
    public void addKeywordOccurrence(IKeywordsOccurrence kwo) {
        keywordsOccurrences.add((KeywordsOccurrence) kwo);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public double getIdf() {
        return idf;
    }

    @Override
    public void setIdf(double idf) {
        this.idf = idf;
    }

}
