package eu.linkedtv.keywords.v1.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.linkedtv.keywords.v1.models.GermanKeyword;
import eu.linkedtv.keywords.v1.models.IKeyword;
import eu.linkedtv.keywords.v1.models.ITextFile;

@Repository
@Transactional
public class KeywordsDao <K extends IKeyword> {
    @PersistenceContext
    protected EntityManager em;

    protected Class<K> entityClass;
    protected String simpleClassName;
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public KeywordsDao() {}
    
    public KeywordsDao(Class<K> clazz) {
        this();
        entityClass = clazz;
        simpleClassName = entityClass.getSimpleName();
    }

    public K getKeyword(String word) throws DaoException {
        TypedQuery<K> query = em
                .createQuery("SELECT k FROM " + entityClass.getSimpleName() + " k WHERE k.word = :word", entityClass);
        query.setParameter("word", word);

        List<K> keywords = query.getResultList();

        if (keywords.size() > 0)
            return keywords.get(0);

        try {
            K keyword = entityClass.newInstance();
            keyword.setWord(word);
            em.persist(keyword);
            
            return keyword;
        } catch (InstantiationException e) {
            throw new DaoException("Problems initializing class " + entityClass.getName(), e);
        } catch (IllegalAccessException e) {
            throw new DaoException("Problems initializing class " + entityClass.getName(), e);
        }

    }

    public List<K> getTopKeywords(int count, ITextFile textFile) {
        TypedQuery<K> query = em
                .createQuery(
                        "SELECT k FROM " + simpleClassName + " k JOIN k.keywordsOccurrences o WHERE o.file.fileId = :fileId "
                                + "ORDER BY (o.tf * k.idf) DESC", entityClass);
        query.setParameter("fileId", textFile.getFileId());
        query.setMaxResults(count);

        return query.getResultList();
    }

    public List<K> getTopKeywords(int count, long fileId) {
        TypedQuery<K> query = em
                .createQuery(
                        "SELECT k FROM " + simpleClassName + " k JOIN k.keywordsOccurrences o WHERE o.file.fileId = :fileId "
                                + "ORDER BY (o.tf * k.idf) DESC", entityClass);
        query.setParameter("fileId", fileId);
        query.setMaxResults(count);

        return query.getResultList();
    }    

    public List<Object[]> getTopKeywordsWithScores(int count, long fileId) {
        TypedQuery<Object[]> query = em
                .createQuery(
                        "SELECT k, (o.tf * k.idf) FROM " + simpleClassName + " k JOIN k.keywordsOccurrences o WHERE o.file.fileId = :fileId "
                                + "ORDER BY (o.tf * k.idf) DESC", Object[].class);
        query.setParameter("fileId", fileId);
        query.setMaxResults(count);

        return query.getResultList();
    }    
    
    public Double getMaxTfIdf() {
        TypedQuery<Double> query = em
                .createQuery(
                        "SELECT MAX(o.tf * k.idf) FROM " + simpleClassName + " k JOIN k.keywordsOccurrences o ", 
                        Double.class);
        
        return query.getSingleResult();
    }        
    
    public List<GermanKeyword> getTopGermanKeywords(int count, ITextFile textFile) {
        TypedQuery<GermanKeyword> query = em
                .createQuery(
                        "SELECT k FROM GermanKeyword k JOIN k.keywordsOccurrences o WHERE o.file.fileId = :fileId "
                                + "AND(SUBSTRING(k.word, 1, 1) = UPPER(SUBSTRING(k.word, 1, 1))) "
                                + "AND (SUBSTRING(k.word, 2, 1) = LOWER(SUBSTRING(k.word, 2, 1))) "
                                + "ORDER BY (o.tf * k.idf) DESC", GermanKeyword.class);
        query.setParameter("fileId", textFile.getFileId());// .setMaxResults(count);
        query.setMaxResults(count);

        return query.getResultList();
    }

    public List<GermanKeyword> getTopGermanKeywords(int count, long fileId) {
        TypedQuery<GermanKeyword> query = em
                .createQuery(
                        "SELECT k FROM GermanKeyword k JOIN k.keywordsOccurrences o WHERE o.file.fileId = :fileId "
                                + "AND(SUBSTRING(k.word, 1, 1) = UPPER(SUBSTRING(k.word, 1, 1))) "
                                + "AND (SUBSTRING(k.word, 2, 1) = LOWER(SUBSTRING(k.word, 2, 1))) "
                                + "ORDER BY (o.tf * k.idf) DESC", GermanKeyword.class);
        query.setParameter("fileId", fileId);
        query.setMaxResults(count);

        return query.getResultList();
    }

    
    public void deleteKeyword(K keyword) {
        keyword = em.merge(keyword);
        em.remove(keyword);
    }
    
    public void save(K keyword) {
        em.persist(keyword);
    }
    
    public int updateIdf() {
        String kwTableName = "de_keywords";
        String kwoTableName = "de_keywords_occurrences";
        String filesTableName = "de_files";
        if (entityClass.getSimpleName().equals("DutchKeyword")) {
            kwTableName = "nl_keywords";
            kwoTableName = "nl_keywords_occurrences";
            filesTableName = "nl_files";
        }
        
        logger.info("Updating IDF " + kwTableName + ", " + kwoTableName + ", " + filesTableName);
        
        Query query = em.createNativeQuery("UPDATE " + kwTableName + " k JOIN (" +
                "SELECT keyword_id, " +
                "LOG((SELECT COUNT(*) FROM " + filesTableName + ") / COUNT(DISTINCT file_id)) idfc " +
                "FROM " + kwoTableName + " GROUP BY keyword_id) ko ON (k.keyword_id = ko.keyword_id) " +
                "SET idf = idfc");
        
        return query.executeUpdate();
    }
}
