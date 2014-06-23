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
import eu.linkedtv.keywords.v1.indexers.DutchTTKeywordsIndexer;
import eu.linkedtv.keywords.v1.indexers.IndexingException;
import eu.linkedtv.keywords.v1.models.DutchKeyword;
import eu.linkedtv.keywords.v1.models.DutchKeywordsOccurrence;
import eu.linkedtv.keywords.v1.models.DutchTextFile;
import eu.linkedtv.keywords.v1.models.GermanKeyword;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/dispatcher-servlet-test.xml"})
@Ignore
public class DucthTTKeyowordsIndexerTest {

    @Autowired
    @Qualifier("dutchKeywordsIndexer")
    private DutchTTKeywordsIndexer indexer;
    
    private KeywordsDao<DutchKeyword> keywordsDao;
    
    protected KeywordsOccurrencesDao<DutchKeywordsOccurrence, DutchKeyword, DutchTextFile> keywordsOccurrencesDao;
    
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
        DutchTextFile textFile = new DutchTextFile();
        textFile.setFileId(1);
        textFile.setName("testFile");
        textFile.setKeywordsOccurrences(new LinkedList<DutchKeywordsOccurrence>());
        indexer.indexTextFile(textFile, "Zij begeleidt al meer dan 20 jaar de grote winnaars van de Nationale Postcode Loterij");
        Mockito.verify(keywordsDao).getKeyword("Nationale Postcode Loterij");
        Mockito.verify(keywordsDao).updateIdf();
        Mockito.verify(keywordsOccurrencesDao, Mockito.times(2)).setOccurrenceCount(Mockito.any(DutchKeyword.class), Mockito.eq(textFile), Mockito.eq(1));
        Mockito.verify(keywordsOccurrencesDao).updateTf();
    }

}
