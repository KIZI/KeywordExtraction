package eu.linkedtv.keywords.v1.models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the files database table.
 * 
 */
@Entity
@Table(name = "nl_files")
public class DutchTextFile extends TextFile {
    private static final long serialVersionUID = 1L;

    // bi-directional many-to-one association to KeywordsOccurrence
    @OneToMany(mappedBy = "file")
    private List<DutchKeywordsOccurrence> keywordsOccurrences;


    public List<DutchKeywordsOccurrence> getKeywordsOccurrences() {
        return this.keywordsOccurrences;
    }

    public void setKeywordsOccurrences(List<DutchKeywordsOccurrence> keywordsOccurrences) {
        this.keywordsOccurrences = keywordsOccurrences;
    }

//    public void addKeywordOccurrence(DutchKeywordsOccurrence kwo) {
//        keywordsOccurrences.add(kwo);
//    }
    
    @Override
    public void addKeywordOccurrence(IKeywordsOccurrence kwo) {
        keywordsOccurrences.add((DutchKeywordsOccurrence) kwo);
    }

}