package eu.linkedtv.keywords.v1.rest;

import javax.ws.rs.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import eu.linkedtv.keywords.v1.dao.FilesDao;
import eu.linkedtv.keywords.v1.dao.KeywordsDao;
import eu.linkedtv.keywords.v1.dao.KeywordsOccurrencesDao;
import eu.linkedtv.keywords.v1.indexers.KeywordsIndexer;
import eu.linkedtv.keywords.v1.models.DutchKeyword;
import eu.linkedtv.keywords.v1.models.DutchKeywordsOccurrence;
import eu.linkedtv.keywords.v1.models.DutchTextFile;

@Component
@Path("/v1/dutch")
public class DutchRestService extends RestService<DutchKeywordsOccurrence, DutchTextFile, DutchKeyword> {
    @Autowired
    @Qualifier("dutchKeywordsIndexer")
    protected KeywordsIndexer<DutchTextFile> indexer;

    @Autowired
    @Qualifier("dutchFilesDao")
    protected FilesDao<DutchTextFile> filesDao;
    
    @Autowired
    @Qualifier("dutchKeywordsDao")
    protected KeywordsDao<DutchKeyword> keywordsDao;
    
    @Autowired
    @Qualifier("dutchKeywordsOccurrencesDao")
    protected KeywordsOccurrencesDao<DutchKeywordsOccurrence, DutchKeyword, DutchTextFile> keywordsOccurrencesDao;    

    @Override
    protected KeywordsIndexer<DutchTextFile> getIndexer() {
        return indexer;
    }

    @Override
    protected FilesDao<DutchTextFile> getFilesDao() {
        return filesDao;
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
