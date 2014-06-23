package eu.linkedtv.keywords.v2.indexers;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.linkedtv.keywords.v1.indexers.IndexingException;
import eu.linkedtv.keywords.v1.models.CIWord;
import eu.linkedtv.keywords.v1.models.ITextFile;
import eu.linkedtv.keywords.v2.languages.SupportedLanguages;
import eu.linkedtv.keywords.v2.models.User;

public abstract class KeywordsIndexer<F extends ITextFile> {

    protected static final Pattern splitPat = Pattern.compile("[^\\p{L}\\p{N}]");
    
    
    protected Set<CIWord> stopWords = new HashSet<CIWord>();
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    public abstract void indexTextFile(F textFile, String text, User user, SupportedLanguages language) throws IndexingException;
    
    public abstract int updateIdf(User user, SupportedLanguages language);
    
    protected boolean isStopWord(String word) {
        return stopWords.contains(new CIWord(word)) || word.matches("^[0-9]+$") || (!word.matches("^[\\p{L}0-9\\s-]+$"));
    }
    
    public void loadStopWordsList(InputStream is) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            
            String line = "";
            int i = 0;
            while ((line = in.readLine()) != null) {
                stopWords.add(new CIWord(line.trim()));
                i++;
            }
            
            logger.info("Loaded " + i + " stop words");
        } catch (IOException e) {
            logger.error("Unable to load stopwords list", e);
        }        
    }
    
    public void setStopWordsList(String stopWordFile) {
        try {
            loadStopWordsList(new FileInputStream(stopWordFile));
        } catch (FileNotFoundException e) {
            logger.error("Unable to load stopwords list", e);
        }
        
    }    
}
