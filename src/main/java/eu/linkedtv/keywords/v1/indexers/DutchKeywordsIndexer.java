package eu.linkedtv.keywords.v1.indexers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import eu.linkedtv.keywords.v1.dao.DaoException;
import eu.linkedtv.keywords.v1.dao.KeywordsDao;
import eu.linkedtv.keywords.v1.dao.KeywordsOccurrencesDao;
import eu.linkedtv.keywords.v1.models.DutchKeyword;
import eu.linkedtv.keywords.v1.models.DutchKeywordsOccurrence;
import eu.linkedtv.keywords.v1.models.DutchTextFile;

@Component
public class DutchKeywordsIndexer extends KeywordsIndexer<DutchTextFile> {
    
    @Autowired
    @Qualifier("dutchKeywordsDao")
    protected KeywordsDao<DutchKeyword> keywordsDao;
    
    @Autowired
    @Qualifier("dutchKeywordsOccurrencesDao")
    protected KeywordsOccurrencesDao<DutchKeywordsOccurrence, DutchKeyword, DutchTextFile> keywordsOccurrencesDao;    

    private static final Set<String> DUTCH_ARTICLES;
    
    static {
        DUTCH_ARTICLES = new HashSet<String>();
        DUTCH_ARTICLES.add("de");
        DUTCH_ARTICLES.add("het");
        DUTCH_ARTICLES.add("een");
    }    
    
    @Override
    public void indexTextFile(DutchTextFile textFile, String text) throws IndexingException {
        String[] words = splitPat.split(text);
        Map<String, Integer> wordOccurrences = new HashMap<String, Integer>();
        
        boolean prevArticle = false;
        for (String word : words) {
            word = word.trim();
            if ((!isStopWord(word)) && (!word.equals("sil")) &&
                    (word.length() > 0)) {
                if (prevArticle) {
                    Integer wordCount = wordOccurrences.get(word);
                    if (wordCount == null) {
                        wordOccurrences.put(word, 1);
                    } else {
                        wordOccurrences.put(word, wordCount + 1);
                    }
                }
            }
            
            if (DUTCH_ARTICLES.contains(word.toLowerCase()))
                prevArticle = true;
            else
                prevArticle = false;
        }
        
        for (Entry<String, Integer> entry : wordOccurrences.entrySet()) {
            try {
                DutchKeyword keyword = keywordsDao.getKeyword(entry.getKey());
                logger.info("Setting Occurrence Count " + keyword.getWord() + ", " +
                		textFile.getName() + ", " + entry.getValue());
                keywordsOccurrencesDao.setOccurrenceCount(keyword, textFile, entry.getValue());
            } catch (DaoException e) {
                throw new IndexingException("Unable to index keyword " + entry.getKey(), e);
            }
        }
        
        keywordsDao.updateIdf();
        keywordsOccurrencesDao.updateTf();
        logger.info("File " + textFile.getName() + " indexed");
    }

    public int updateIdf() {
        return keywordsDao.updateIdf();
    }
}
