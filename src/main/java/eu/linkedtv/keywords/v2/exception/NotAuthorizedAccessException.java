package eu.linkedtv.keywords.v2.exception;

public class NotAuthorizedAccessException extends Exception {
    
    private static final long serialVersionUID = 1L;

    public NotAuthorizedAccessException(String message) {
        super(message);
    }
}
