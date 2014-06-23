package eu.linkedtv.keywords.v1.indexers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import eu.linkedtv.keywords.v1.dao.KeywordsDao;
import eu.linkedtv.keywords.v1.dao.KeywordsOccurrencesDao;
import eu.linkedtv.keywords.v1.models.DutchKeyword;
import eu.linkedtv.keywords.v1.models.DutchKeywordsOccurrence;
import eu.linkedtv.keywords.v1.models.DutchTextFile;

public class DutchTTKeywordsIndexer extends TTKeywordsIndexer<DutchTextFile, DutchKeyword, DutchKeywordsOccurrence>{

    @Autowired
    @Qualifier("dutchKeywordsDao")
    protected KeywordsDao<DutchKeyword> keywordsDao;
    
    @Autowired
    @Qualifier("dutchKeywordsOccurrencesDao")
    protected KeywordsOccurrencesDao<DutchKeywordsOccurrence, DutchKeyword, DutchTextFile> keywordsOccurrencesDao;
    
    public DutchTTKeywordsIndexer(String anniePluginPath, String taggerFrameworkPluginPath, String corpusName,
            String treeTaggerPath, String japeGrammarResource) throws IndexingException {
        super(anniePluginPath, taggerFrameworkPluginPath, corpusName, treeTaggerPath, japeGrammarResource);
    }

    @Override
    protected KeywordsDao<DutchKeyword> getKeywordsDao() {
        return keywordsDao;
    }

    @Override
    protected KeywordsOccurrencesDao<DutchKeywordsOccurrence, DutchKeyword, DutchTextFile> getKeywordsOccurrencesDao() {
        return keywordsOccurrencesDao;
    }

}
