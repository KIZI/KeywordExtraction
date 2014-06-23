package eu.linkedtv.keywords.v1.indexers;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import eu.linkedtv.keywords.v1.dao.DaoException;
import eu.linkedtv.keywords.v1.dao.KeywordsDao;
import eu.linkedtv.keywords.v1.dao.KeywordsOccurrencesDao;
import eu.linkedtv.keywords.v1.models.GermanKeyword;
import eu.linkedtv.keywords.v1.models.GermanKeywordsOccurrence;
import eu.linkedtv.keywords.v1.models.GermanTextFile;

@Component
public class GermanKeywordsIndexer extends KeywordsIndexer<GermanTextFile> {
    @Autowired
    @Qualifier("germanKeywordsDao")
    protected KeywordsDao<GermanKeyword> keywordsDao;
    
    @Autowired
    @Qualifier("germanKeywordsOccurrencesDao")
    protected KeywordsOccurrencesDao<GermanKeywordsOccurrence, GermanKeyword, GermanTextFile> keywordsOccurrencesDao;
    
    @Override
    public void indexTextFile(GermanTextFile textFile, String text) throws IndexingException {
        String[] words = splitPat.split(text);
        Map<String, Integer> wordOccurrences = new HashMap<String, Integer>();
        for (String word : words) {
            word = word.trim();
            if ((word.length() > 0) && (!isStopWord(word)) && (!word.equals("sil"))) {
                Integer wordCount = wordOccurrences.get(word);
                if (wordCount == null) {
                    wordOccurrences.put(word, 1);
                } else {
                    wordOccurrences.put(word, wordCount + 1);
                }
            }
        }
        
        for (Entry<String, Integer> entry : wordOccurrences.entrySet()) {
            try {
                GermanKeyword keyword = keywordsDao.getKeyword(entry.getKey());
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
