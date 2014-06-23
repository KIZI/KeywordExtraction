package eu.linkedtv.keywords.v2.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.linkedtv.keywords.v2.languages.SupportedLanguages;
import eu.linkedtv.keywords.v2.models.Keyword;
import eu.linkedtv.keywords.v2.models.TextFile;
import eu.linkedtv.keywords.v2.models.User;

@Repository
@Transactional
public class KeywordsDao {

    @PersistenceContext
    protected EntityManager em;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public KeywordsDao() {
    }

    public java.util.Map<String, Long> getKeywords(java.util.Set<String> words, User user, SupportedLanguages language) {
        java.util.Map<String, Long> keywords = new java.util.HashMap<String, Long>();
        String whereIn = "";
        for (String word : words) {
            whereIn += "'" + word + "',";
        }
        whereIn = whereIn.replaceAll(",$", "");
        for (Object[] objects : (List<Object[]>) em
                .createNativeQuery("SELECT keyword_id, word FROM keywords WHERE word IN (" + whereIn + ") AND user_id = " + user.getUserId() + " AND language_id = " + language.getLanguageId() + "")
                .getResultList()) {
            Long id = (Long) objects[0];
            String word = (String) objects[1];
            words.remove(word);
            keywords.put(word, id);
        }
        List<Keyword> addedKeywords = new java.util.LinkedList<Keyword>();
        for (String word : words) {
            Keyword keyword = new Keyword();
            keyword.setWord(word);
            keyword.setLanguageId(language.getLanguageId());
            keyword.setUser(user);
            addedKeywords.add(keyword);
            em.persist(keyword);
        }
        em.flush();
        for (Keyword keyword : addedKeywords) {
            keywords.put(keyword.getWord(), (long) keyword.getKeywordId());
            words.remove(keyword.getWord());
        }
        return keywords;
    }

    public Keyword getKeyword(String word, User user, SupportedLanguages language) {
        TypedQuery<Keyword> query = em
                .createQuery("SELECT k FROM Keyword k WHERE k.word = :word AND k.user = :user AND k.languageId = :languageId", Keyword.class);
        query.setParameter("word", word);
        query.setParameter("user", user);
        query.setParameter("languageId", language.getLanguageId());

        List<Keyword> keywords = query.getResultList();

        if (keywords.size() > 0) {
            return keywords.get(0);
        }

        Keyword keyword = new Keyword();
        keyword.setWord(word);
        keyword.setLanguageId(language.getLanguageId());
        keyword.setUser(user);
        em.persist(keyword);
        em.flush();

        return keyword;

    }

    public List<Keyword> getTopKeywords(int count, TextFile textFile) {
        TypedQuery<Keyword> query = em
                .createQuery(
                        "SELECT k FROM Keyword k JOIN k.keywordsOccurrences o WHERE o.file.fileId = :fileId "
                        + "ORDER BY (o.tf * k.idf) DESC", Keyword.class);
        query.setParameter("fileId", textFile.getFileId());
        query.setMaxResults(count);

        return query.getResultList();
    }

    public List<Keyword> getTopKeywords(int count, long fileId) {
        TypedQuery<Keyword> query = em
                .createQuery(
                        "SELECT k FROM Keyword k JOIN k.keywordsOccurrences o WHERE o.file.fileId = :fileId "
                        + "ORDER BY (o.tf * k.idf) DESC", Keyword.class);
        query.setParameter("fileId", fileId);
        query.setMaxResults(count);

        return query.getResultList();
    }

    public List<Object[]> getTopKeywordsWithScores(int count, long fileId, long D) {
        return em
                .createNativeQuery(
                        "SELECT k.word, (o.tf * LOG(" + D + " / (k.d * 1.0))) FROM keywords k NATURAL JOIN keywords_occurrences o WHERE o.file_id = ?1 "
                        + "ORDER BY (o.tf * LOG(" + D + " / (k.d * 1.0))) DESC")
                .setParameter(1, fileId)
                .setMaxResults(count)
                .getResultList();
    }

    public Double getMaxTfIdf() {
        TypedQuery<Double> query = em
                .createQuery(
                        "SELECT MAX(o.tf * k.idf) FROM Keyword k JOIN k.keywordsOccurrences o ",
                        Double.class);

        return query.getSingleResult();
    }

    public void deleteKeyword(Keyword keyword) {
        keyword = em.merge(keyword);
        em.remove(keyword);
    }

    public void save(Keyword keyword) {
        em.persist(keyword);
    }

    public void flush() {
        em.flush();
    }

    public Long getDocumentCount(long userId, SupportedLanguages language) {
        return (Long) em
                .createNativeQuery("SELECT COUNT(*) FROM files WHERE user_id = ?1 AND language_id = ?2")
                .setParameter(1, userId)
                .setParameter(2, language.getLanguageId())
                .getSingleResult();
    }

    public int updateIdf(long userId, SupportedLanguages language) {

        logger.info("Updating IDF...");

        Query query = em.createNativeQuery("UPDATE keywords k JOIN ("
                + "SELECT keyword_id, "
                + "LOG((SELECT COUNT(*) FROM files WHERE user_id = ?1 AND language_id = ?2) / COUNT(DISTINCT file_id)) idfc "
                + "FROM keywords_occurrences JOIN files USING(file_id) WHERE user_id = ?1 AND language_id = ?2 GROUP BY keyword_id) ko ON (k.keyword_id = ko.keyword_id) "
                + "SET idf = idfc");

        query.setParameter(1, userId);
        query.setParameter(2, language.getLanguageId());

        return query.executeUpdate();
    }
}
