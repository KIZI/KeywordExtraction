package eu.linkedtv.keywords.v2.rest.model;

import javax.xml.bind.annotation.XmlRootElement;

import eu.linkedtv.keywords.v2.models.TextFile;

@XmlRootElement
public class ResponseTextFile {
    private final int fileId;
    private final String name;
    private final int languageId;
    
    public ResponseTextFile(TextFile textFile) {
        fileId = textFile.getFileId();
        name = textFile.getName();
        languageId = textFile.getLanguageId();
    }
    
    public int getFileId() {
        return fileId;
    }

    public String getName() {
        return name;
    }

    public int getLanguageId() {
        return languageId;
    }
}
