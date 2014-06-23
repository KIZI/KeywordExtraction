package eu.linkedtv.keywords.srt;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import eu.linkedtv.keywords.v1.srt.SrtReader;

@Ignore
public class SrtReaderTest {

    private SrtReader srtReader = new SrtReader();
    
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSrtToText() throws IOException {
        InputStream isSrt = getClass().getResourceAsStream("2007-01-03-20,15-1_2007-01-03-20,15-1_ONBEKEND.srt");
        byte[] buffer = new byte[512];
        StringBuilder srt = new StringBuilder();
        while (isSrt.read(buffer) > 0) {
            srt.append(new String(buffer));
        }
        isSrt.close();
        
        InputStream isTxt = getClass().getResourceAsStream("2007-01-03-20,15-1_2007-01-03-20,15-1_ONBEKEND.txt");
        StringBuilder txt = new StringBuilder();
        buffer = new byte[512];
        while (isTxt.read(buffer) > 0) {
            txt.append(new String(buffer));
        }
        isTxt.close();
        
        assertEquals(txt.toString().trim(), srtReader.srtToText(srt.toString()).trim());
    }

}
