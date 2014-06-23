package eu.linkedtv.keywords.v1.models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity 
@Table(name="nl_keywords")
public class DutchKeyword extends Keyword {

    private static final long serialVersionUID = 1L;
    
    //bi-directional many-to-one association to KeywordsOccurrence
    @OneToMany(mappedBy="keyword")
    private List<DutchKeywordsOccurrence> keywordsOccurrences;

    public List<DutchKeywordsOccurrence> getKeywordsOccurrences() {
        return this.keywordsOccurrences;
    }

    public void setKeywordsOccurrences(List<DutchKeywordsOccurrence> keywordsOccurrences) {
        this.keywordsOccurrences = keywordsOccurrences;
    }
    
    public void addKeywordOccurrence(DutchKeywordsOccurrence kwo) {
        keywordsOccurrences.add(kwo);
    }
    
    @Override
    public void addKeywordOccurrence(IKeywordsOccurrence kwo) {
        keywordsOccurrences.add((DutchKeywordsOccurrence) kwo);
    }
}
