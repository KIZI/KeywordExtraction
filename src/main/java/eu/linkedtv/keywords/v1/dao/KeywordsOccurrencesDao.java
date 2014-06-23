package eu.linkedtv.keywords.v1.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.linkedtv.keywords.v1.models.IKeyword;
import eu.linkedtv.keywords.v1.models.IKeywordsOccurrence;
import eu.linkedtv.keywords.v1.models.ITextFile;
import eu.linkedtv.keywords.v1.models.KeywordsOccurrencePK;

@Repository
@Transactional
public class KeywordsOccurrencesDao
        <O extends IKeywordsOccurrence, K extends IKeyword, F extends ITextFile> {

    private Class<O> kwoClass;

    @PersistenceContext
    private EntityManager em;

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    public KeywordsOccurrencesDao() {}
    
    public KeywordsOccurrencesDao(Class<O> kwoClass) {
        this.kwoClass = kwoClass;
    }

    public O getOccurrence(K keyword, F file) throws DaoException {
        KeywordsOccurrencePK pk = new KeywordsOccurrencePK();
        pk.setFileId(file.getFileId());
        pk.setKeywordId(keyword.getKeywordId());

        logger.info("Find Keyword Occurrence " + kwoClass.getName());
        O kwo = em.find(kwoClass, pk);

        if (kwo == null) {
            try {
                kwo = kwoClass.newInstance();
                kwo.setCount(0);
                kwo.setKeyword(keyword);
                kwo.setFile(file);
                file.addKeywordOccurrence(kwo);
                keyword.addKeywordOccurrence(kwo);
                em.persist(kwo);
            } catch (IllegalAccessException e) {
                throw new DaoException("Problems initializing class "
                        + kwoClass.getName(), e);
            } catch (InstantiationException e) {
                throw new DaoException("Problems initializing class "
                        + kwoClass.getName(), e);
            }
        } else {
            kwo.setKeyword(keyword);
            kwo.setFile(file);
            file.addKeywordOccurrence(kwo);
            keyword.addKeywordOccurrence(kwo);
        }

        return kwo;
    }

    public void incrementOccurrencCount(K keyword, F file) throws DaoException {
        O kwo = getOccurrence(keyword, file);
        kwo.incrementCount();
        em.flush();
        em.clear();
    }
    
    public int deleteOccurrencesForFile(F file) {
        Query query = em.createQuery("DELETE FROM " + kwoClass.getSimpleName() + " ko WHERE ko.file.fileId = :fileId");
        logger.info("Query " + query);
        query.setParameter("fileId", file.getFileId());
        
        return query.executeUpdate();
    }
    
    public int deleteOccurrencesForFile(long fileId) {
        Query query = em.createQuery("DELETE FROM " + kwoClass.getSimpleName() + " ko WHERE ko.file.fileId = :fileId");
        logger.info("Query " + query);
        query.setParameter("fileId", fileId);
        
        return query.executeUpdate();
    }    

    public void setOccurrenceCount(K keyword, F file, int count)
            throws DaoException {
        IKeywordsOccurrence kwo = getOccurrence(keyword, file);
        kwo.setCount(count);
        em.flush();
        em.clear();
    }
    
    public int updateTf() {
        String tableName = "de_keywords_occurrences";
        if (kwoClass.getSimpleName().equals("DutchKeywordsOccurrence"))
            tableName = "nl_keywords_occurrences";
        
        logger.info("Updating TF " + tableName);
        
        Query query = em.createNativeQuery("UPDATE " + tableName + " koo JOIN (" +
                "SELECT file_id, SUM( count ) AS sum_count " +
                "FROM " + tableName + " ko " +
                "GROUP BY file_id" +
                ")ko ON ( ko.file_id = koo.file_id ) " +
                "SET koo.tf=koo.count / ko.sum_count");
        
        return query.executeUpdate();
    }
}
