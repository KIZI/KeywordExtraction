package eu.linkedtv.keywords.indexers;

import java.lang.reflect.Field;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.linkedtv.keywords.v1.dao.DaoException;
import eu.linkedtv.keywords.v1.dao.KeywordsDao;
import eu.linkedtv.keywords.v1.dao.KeywordsOccurrencesDao;
import eu.linkedtv.keywords.v1.indexers.GermanTTKeywordsIndexer;
import eu.linkedtv.keywords.v1.indexers.IndexingException;
import eu.linkedtv.keywords.v1.models.GermanKeyword;
import eu.linkedtv.keywords.v1.models.GermanKeywordsOccurrence;
import eu.linkedtv.keywords.v1.models.GermanTextFile;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/dispatcher-servlet-test.xml"})
@Ignore
public class GermanTTKeyowordsIndexerTest {

    @Autowired
    @Qualifier("germanKeywordsIndexer")
    private GermanTTKeywordsIndexer indexer;
    
    private KeywordsDao<GermanKeyword> keywordsDao;
    
    protected KeywordsOccurrencesDao<GermanKeywordsOccurrence, GermanKeyword, GermanTextFile> keywordsOccurrencesDao;
    
    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, DaoException {
        Field keywordsDaoField = indexer.getClass().getDeclaredField("keywordsDao");
        keywordsDaoField.setAccessible(true);
        keywordsDao = Mockito.mock(KeywordsDao.class);
        keywordsDaoField.set(indexer, keywordsDao);
        
        Field keywordsOccurrencesDaoField = indexer.getClass().getDeclaredField("keywordsOccurrencesDao");
        keywordsOccurrencesDaoField.setAccessible(true);
        keywordsOccurrencesDao = Mockito.mock(KeywordsOccurrencesDao.class);
        keywordsOccurrencesDaoField.set(indexer, keywordsOccurrencesDao);      

        Mockito.when(keywordsDao.getKeyword(Mockito.anyString())).thenAnswer(new Answer<GermanKeyword>() {

            @Override
            public GermanKeyword answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                
                GermanKeyword keyword = Mockito.mock(GermanKeyword.class);
                Mockito.when(keyword.getWord()).thenReturn((String) args[0]);
                
                return keyword;
            }
        });
    }
    
    @Test
    public void testIndexTextFileGermanTextFileString() throws IndexingException, DaoException {
        GermanTextFile textFile = new GermanTextFile();
        textFile.setFileId(1);
        textFile.setName("testFile");
        textFile.setKeywordsOccurrences(new LinkedList<GermanKeywordsOccurrence>());
        indexer.indexTextFile(textFile, "Knapp eine Woche nach einer Geiselnahme in Zehlendorf ist erneut ein " +
        		"Spezialeinsatzkommando der Berliner Polizei zu einem Einsatz in einer Bank ausgerückt.");
        Mockito.verify(keywordsDao).getKeyword("Woche");
        Mockito.verify(keywordsDao).getKeyword("Geiselnahme");
        Mockito.verify(keywordsDao).getKeyword("Zehlendorf");
        Mockito.verify(keywordsDao).getKeyword("Spezialeinsatzkommando");
        Mockito.verify(keywordsDao).getKeyword("Einsatz");
        Mockito.verify(keywordsDao).getKeyword("Bank");
        Mockito.verify(keywordsDao).updateIdf();
        Mockito.verify(keywordsOccurrencesDao, Mockito.times(7)).setOccurrenceCount(Mockito.any(GermanKeyword.class), Mockito.eq(textFile), Mockito.eq(1));
        Mockito.verify(keywordsOccurrencesDao).updateTf();
    }

}
