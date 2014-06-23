package eu.linkedtv.keywords.v1.rest.model;

import javax.xml.bind.annotation.XmlRootElement;

import eu.linkedtv.keywords.v1.models.ITextFile;

@XmlRootElement
public class ResponseTextFile {
    private final int fileId;
    private final String name;
    
    public ResponseTextFile(ITextFile textFile) {
        fileId = textFile.getFileId();
        name = textFile.getName();
    }
    
    public int getFileId() {
        return fileId;
    }

    public String getName() {
        return name;
    }
}
