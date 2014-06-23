package eu.linkedtv.keywords.v1.srt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SrtReader {
    protected static final String nl = "\\r?\\n";
    protected static final String sp = "[ \\t]*";
    
    protected static final Pattern srtPattern = Pattern.compile("(?s)(\\d+)" + sp + nl + "(\\d{1,2}):(\\d\\d):(\\d\\d),(\\d\\d\\d)" + sp + "-->"+ sp + "(\\d\\d):(\\d\\d):(\\d\\d),(\\d\\d\\d)" + sp + "(X1:\\d.*?)??" + nl + "(.*?)" + nl + nl); 

    public String srtToText(String srt) {
        Matcher matcher = srtPattern.matcher(srt);
        StringBuilder text = new StringBuilder();
        
        while (matcher.find()) {
            text.append(matcher.group(11) + "\n");
        }
        
        return text.toString();
    }
}
