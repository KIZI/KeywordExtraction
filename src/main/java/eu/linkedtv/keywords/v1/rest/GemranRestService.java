package eu.linkedtv.keywords.v1.rest;

import javax.ws.rs.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import eu.linkedtv.keywords.v1.dao.FilesDao;
import eu.linkedtv.keywords.v1.dao.KeywordsDao;
import eu.linkedtv.keywords.v1.dao.KeywordsOccurrencesDao;
import eu.linkedtv.keywords.v1.indexers.KeywordsIndexer;
import eu.linkedtv.keywords.v1.models.GermanKeyword;
import eu.linkedtv.keywords.v1.models.GermanKeywordsOccurrence;
import eu.linkedtv.keywords.v1.models.GermanTextFile;

@Component
@Path("/v1/german")
public class GemranRestService extends RestService<GermanKeywordsOccurrence, GermanTextFile, GermanKeyword> {
    @Autowired
    @Qualifier("germanKeywordsIndexer")
    protected KeywordsIndexer<GermanTextFile> indexer;

    @Autowired
    @Qualifier("germanFilesDao")
    protected FilesDao<GermanTextFile> filesDao;
    
    @Autowired
    @Qualifier("germanKeywordsDao")
    protected KeywordsDao<GermanKeyword> keywordsDao;
    
    @Autowired
    @Qualifier("germanKeywordsOccurrencesDao")
    protected KeywordsOccurrencesDao<GermanKeywordsOccurrence, GermanKeyword, GermanTextFile> keywordsOccurrencesDao;

    protected KeywordsOccurrencesDao<GermanKeywordsOccurrence, GermanKeyword, GermanTextFile> getKeywordsOccurrencesDao() {
        return keywordsOccurrencesDao;
    }

    @Override
    protected KeywordsIndexer<GermanTextFile> getIndexer() {
        return indexer;
    }

    @Override
    protected FilesDao<GermanTextFile> getFilesDao() {
        return filesDao;
    }

    @Override
    protected KeywordsDao<GermanKeyword> getKeywordsDao() {
        return keywordsDao;
    }
}
