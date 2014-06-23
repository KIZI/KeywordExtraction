package eu.linkedtv.keywords.v1.models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "de_files")
public class GermanTextFile extends TextFile {
    private static final long serialVersionUID = 1L;

    // bi-directional many-to-one association to KeywordsOccurrence
    @OneToMany(mappedBy = "file")
    private List<GermanKeywordsOccurrence> keywordsOccurrences;


    public List<GermanKeywordsOccurrence> getKeywordsOccurrences() {
        return this.keywordsOccurrences;
    }

    public void setKeywordsOccurrences(List<GermanKeywordsOccurrence> keywordsOccurrences) {
        this.keywordsOccurrences = keywordsOccurrences;
    }

//    public void addKeywordOccurrence(GermanKeywordsOccurrence kwo) {
//        keywordsOccurrences.add(kwo);
//    }
    @Override
    public void addKeywordOccurrence(IKeywordsOccurrence kwo) {
        keywordsOccurrences.add((GermanKeywordsOccurrence) kwo);
    }

}