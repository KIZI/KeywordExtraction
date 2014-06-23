package eu.linkedtv.keywords.v1.indexers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import eu.linkedtv.keywords.v1.dao.KeywordsDao;
import eu.linkedtv.keywords.v1.dao.KeywordsOccurrencesDao;
import eu.linkedtv.keywords.v1.models.GermanKeyword;
import eu.linkedtv.keywords.v1.models.GermanKeywordsOccurrence;
import eu.linkedtv.keywords.v1.models.GermanTextFile;

public class GermanTTKeywordsIndexer extends TTKeywordsIndexer<GermanTextFile, GermanKeyword, GermanKeywordsOccurrence> {

    @Autowired
    @Qualifier("germanKeywordsDao")
    protected KeywordsDao<GermanKeyword> keywordsDao;
    
    @Autowired
    @Qualifier("germanKeywordsOccurrencesDao")
    protected KeywordsOccurrencesDao<GermanKeywordsOccurrence, GermanKeyword, GermanTextFile> keywordsOccurrencesDao;

    public GermanTTKeywordsIndexer(String anniePluginPath, String taggerFrameworkPluginPath, String corpusName,
            String treeTaggerPath, String japeGrammarResource) throws IndexingException {
        super(anniePluginPath, taggerFrameworkPluginPath, corpusName, treeTaggerPath, japeGrammarResource);
    }
    
    @Override
    protected KeywordsDao<GermanKeyword> getKeywordsDao() {
        return keywordsDao;
    }

    @Override
    protected KeywordsOccurrencesDao<GermanKeywordsOccurrence, GermanKeyword, GermanTextFile> getKeywordsOccurrencesDao() {
        return keywordsOccurrencesDao;
    }

}
