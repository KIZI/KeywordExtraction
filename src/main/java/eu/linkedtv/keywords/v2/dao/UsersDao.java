package eu.linkedtv.keywords.v2.dao;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.linkedtv.keywords.v2.models.User;

@Repository
@Transactional
public class UsersDao {
    
    private static final String KEY_SALT = "Ggjhsdgkjhat\234567l.dsjhmas%00}sdkj";
    private static final String PASSWORD_SALT = "asdfdsaghht.:d\234567l.dsjhmas%00}sdkj";
    
    @PersistenceContext
    private EntityManager em;
    
    public UsersDao() {}
    
    public User getUserByKey(String key) {
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.key = :key", User.class);
        query.setParameter("key", key);
        
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    public User getUser(String userName, String password) {
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.name = :userName AND u.password = :password", User.class);
        query.setParameter("userName", userName);
        query.setParameter("password", passwordDigest(password));
        
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    } 
    
    public static String passwordDigest(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            byte [] digest = md.digest((password + PASSWORD_SALT).getBytes());
            
            return digestToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA digest algorithm not found", e);
        }
    }

    public String addUser(String userName, String password) {
        User user = new User();
        user.setName(userName);
        user.setPassword(passwordDigest(password));
        
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA");
            byte[] key = md.digest((userName + KEY_SALT).getBytes());
            
            Random random = new Random(System.currentTimeMillis());
            
            
            for (int i = 0; i < random.nextInt(); i++) {
                key = md.digest(key);
            }
            
            String keyStr = digestToString(key);
            user.setKey(keyStr);
            save(user);
            
            return keyStr;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA digest algorithm not found", e);
        }
    }
    
    private static String digestToString(byte[] digest) {
        BigInteger bigInt = new BigInteger(1, digest);
        String hashText = bigInt.toString(16);
        while (hashText.length() < 32) {
            hashText = "0" + hashText;
        }
        
        return hashText;
    }
    
    private void save(User user) {
        em.persist(user);
    }
    
    public void delete(User user) {
        User toBeRemoved = em.merge(user);
        em.remove(toBeRemoved);
    }
}
