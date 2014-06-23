package eu.linkedtv.keywords.v1.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.linkedtv.keywords.v1.models.ITextFile;

@Repository
@Transactional
public class FilesDao<F extends ITextFile> {
    
    @PersistenceContext
    private EntityManager em;
    
    private Class<F> textFileClass;
    
    public FilesDao() {}
    
    public FilesDao(Class<F> textFileClass) {
        this.textFileClass = textFileClass;
    }
    
    public F getFile(String fileName, String originalText) throws DaoException {
        TypedQuery<F> query = em.createQuery("SELECT t FROM " + textFileClass.getSimpleName() + " t WHERE t.name = :name", textFileClass);
        query.setParameter("name", fileName);

        List<F> files = query.getResultList();
        
        if (files.size() > 0) {
            F file = files.get(0);
            if (!StringUtils.isEmpty(originalText)) {
                file.setOriginalText(originalText);
            }
            
            return file;
        }
        
        try {
            F file = textFileClass.newInstance();
            file.setName(fileName);
            file.setOriginalText(originalText);
            em.persist(file);
            
            return file;
        } catch (InstantiationException e) {
            throw new DaoException("Problems initializing class " + textFileClass.getName(), e);
        } catch (IllegalAccessException e) {
            throw new DaoException("Problems initializing class " + textFileClass.getName(), e);
        }
    }
    
    public void deleteFile(int fileId) {
        F textFile = em.find(textFileClass, fileId);
        em.remove(textFile);
    }
    
    public F getFile(int fileId) {
        return em.find(textFileClass, fileId);
    }
    
    public List<F> getAllFiles() {
        TypedQuery<F> query = em.createQuery("SELECT t FROM " + textFileClass.getSimpleName() + " t", textFileClass);
        
        return query.getResultList();
    }

    public void save(F file) {
        em.persist(file);
    }
}
