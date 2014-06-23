package eu.linkedtv.keywords.v1.rest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class MessageWebApplicationException extends WebApplicationException {
    
    private static final long serialVersionUID = 1L;

    public MessageWebApplicationException(Status status, String message) {
        super(Response.status(status).entity(message).type(MediaType.TEXT_PLAIN).build());
    }
}
