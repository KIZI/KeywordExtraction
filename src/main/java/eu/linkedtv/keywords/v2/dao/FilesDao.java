package eu.linkedtv.keywords.v2.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.linkedtv.keywords.v2.exception.NotAuthorizedAccessException;
import eu.linkedtv.keywords.v2.languages.SupportedLanguages;
import eu.linkedtv.keywords.v2.models.TextFile;
import eu.linkedtv.keywords.v2.models.User;

@Repository
@Transactional
public class FilesDao {

    @PersistenceContext
    private EntityManager em;

    public FilesDao() {
    }

    public TextFile getFile(String fileName, String originalText, User user, SupportedLanguages language) {
        TypedQuery<TextFile> query = em.createQuery("SELECT t FROM TextFile t WHERE t.name = :name AND t.user = :user AND t.languageId = :languageId", TextFile.class);
        query.setParameter("name", fileName);
        query.setParameter("user", user);
        query.setParameter("languageId", language.getLanguageId());

        final TextFile file;
        List<TextFile> files = query.getResultList();

        if (files.size() > 0) {
            file = files.get(0);
            if (!StringUtils.isEmpty(originalText)) {
                file.setOriginalText(originalText);
            }
        } else {
            file = new TextFile();
            file.setName(fileName);
            file.setOriginalText(originalText);
            user.addFile(file);
            file.setUser(user);
            file.setLanguageId(language);
            em.persist(file);
        }

        return file;
    }

    public void deleteFile(int fileId, User user) throws NotAuthorizedAccessException {
        TextFile textFile = em.find(TextFile.class, fileId);
        if (textFile.getUser().equals(user)) {
            em.remove(textFile);
        } else {
            throw new NotAuthorizedAccessException("User " + user.getName() + " is not allowed to access file " + fileId);
        }
    }

    public TextFile getFile(int fileId, User user) throws NotAuthorizedAccessException {
        TextFile textFile = em.find(TextFile.class, fileId);
        if (textFile.getUser().equals(user)) {
            return textFile;
        } else {
            throw new NotAuthorizedAccessException("User " + user.getName() + " is not allowed to access file " + fileId);
        }
    }

    public List<TextFile> getAllFiles(User user) {
        TypedQuery<TextFile> query = em.createQuery("SELECT t FROM TextFile t WHERE t.user = :user", TextFile.class);
        query.setParameter("user", user);

        return query.getResultList();
    }

    public void save(TextFile file) {
        em.persist(file);
    }
}
