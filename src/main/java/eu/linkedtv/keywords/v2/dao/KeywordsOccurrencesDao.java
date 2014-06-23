package eu.linkedtv.keywords.v2.dao;

import eu.linkedtv.keywords.v1.dao.DaoException;
import eu.linkedtv.keywords.v1.models.IKeyword;
import eu.linkedtv.keywords.v1.models.IKeywordsOccurrence;
import eu.linkedtv.keywords.v1.models.ITextFile;
import eu.linkedtv.keywords.v2.models.KeywordsOccurrence;
import eu.linkedtv.keywords.v2.models.KeywordsOccurrencePK;
import eu.linkedtv.keywords.v2.models.TextFile;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class KeywordsOccurrencesDao {

    @PersistenceContext
    private EntityManager em;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public KeywordsOccurrencesDao() {
    }

    public KeywordsOccurrence getOccurrence(IKeyword keyword, ITextFile file) {
        return getOccurrence(keyword, file, false);
    }

    public KeywordsOccurrence getOccurrence(IKeyword keyword, ITextFile file, boolean onlyNew) {
        KeywordsOccurrence kwo = null;
        if (!onlyNew) {
            KeywordsOccurrencePK pk = new KeywordsOccurrencePK();
            pk.setFileId(file.getFileId());
            pk.setKeywordId(keyword.getKeywordId());

            kwo = em.find(KeywordsOccurrence.class, pk);
        }

        if (kwo == null) {
            kwo = new KeywordsOccurrence();
            kwo.setCount(0);
            kwo.setKeyword(keyword);
            kwo.setFile(file);
            file.addKeywordOccurrence(kwo);
            keyword.addKeywordOccurrence(kwo);
            em.persist(kwo);
        } else {
            kwo.setKeyword(keyword);
            kwo.setFile(file);
            file.addKeywordOccurrence(kwo);
            keyword.addKeywordOccurrence(kwo);
        }

        return kwo;
    }

    public void incrementOccurrencCount(IKeyword keyword, ITextFile file) throws DaoException {
        IKeywordsOccurrence kwo = getOccurrence(keyword, file);
        kwo.incrementCount();
        em.flush();
        em.clear();
    }

    public int deleteOccurrencesForFile(TextFile file) {
        return deleteOccurrencesForFile(file.getFileId());
    }

    public void updateOccurrencesForFile(long fileId) {
        em.createNativeQuery("UPDATE keywords k NATURAL JOIN keywords_occurrences ko SET d=d+1 WHERE ko.file_id = ?1")
                .setParameter(1, fileId)
                .executeUpdate();
    }

    public int deleteOccurrencesForFile(long fileId) {
        em.createNativeQuery("UPDATE keywords k NATURAL JOIN keywords_occurrences ko SET d=d-1 WHERE ko.file_id = ?1")
                .setParameter(1, fileId)
                .executeUpdate();
        em.createNativeQuery("DELETE FROM keywords WHERE d = 0")
                .setParameter(1, fileId)
                .executeUpdate();
        Query query = em.createQuery("DELETE FROM KeywordsOccurrence ko WHERE ko.file.fileId = :fileId");
        logger.info("Query " + query);
        query.setParameter("fileId", fileId);

        return query.executeUpdate();
    }

    public void setOccurrenceCount(Long keywordId, ITextFile file, int count, double tf)
            throws DaoException {
        em
                .createNativeQuery("INSERT INTO keywords_occurrences (keyword_id, file_id, count, tf) VALUES (?1, ?2, ?3, ?4)")
                .setParameter(1, keywordId)
                .setParameter(2, file.getFileId())
                .setParameter(3, count)
                .setParameter(4, tf)
                .executeUpdate();
    }

    public int updateTf() {
        logger.info("Updating TF...");

        Query query = em.createNativeQuery("UPDATE keywords_occurrences koo JOIN ("
                + "SELECT file_id, SUM( count ) AS sum_count "
                + "FROM keywords_occurrences ko "
                + "GROUP BY file_id"
                + ")ko ON ( ko.file_id = koo.file_id ) "
                + "SET koo.tf=koo.count / ko.sum_count");

        return query.executeUpdate();
    }
}
