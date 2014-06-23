package eu.linkedtv.keywords.v1.rest.model;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class ResponseKeyword {
    
    private final double confidence;
    private final String word;
    
    public ResponseKeyword(String word, double confidence) {
        this.word = word;
        this.confidence = confidence;
    }
    
    public double getConfidence() {
        return confidence;
    }

    public String getWord() {
        return word;
    }
}