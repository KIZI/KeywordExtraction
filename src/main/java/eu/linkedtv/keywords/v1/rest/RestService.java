package eu.linkedtv.keywords.v1.rest;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.linkedtv.keywords.v1.dao.DaoException;
import eu.linkedtv.keywords.v1.dao.FilesDao;
import eu.linkedtv.keywords.v1.dao.KeywordsDao;
import eu.linkedtv.keywords.v1.dao.KeywordsOccurrencesDao;
import eu.linkedtv.keywords.v1.indexers.IndexingException;
import eu.linkedtv.keywords.v1.indexers.KeywordsIndexer;
import eu.linkedtv.keywords.v1.models.IKeyword;
import eu.linkedtv.keywords.v1.models.IKeywordsOccurrence;
import eu.linkedtv.keywords.v1.models.ITextFile;
import eu.linkedtv.keywords.v1.rest.model.ResponseKeyword;
import eu.linkedtv.keywords.v1.rest.model.ResponseTextFile;

public abstract class RestService<O extends IKeywordsOccurrence, TF extends ITextFile, K extends IKeyword> {
    private static Logger logger = LoggerFactory.getLogger(RestService.class);
    
    @POST
    @Consumes("application/x-www-form-urlencoded")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Map<String, Object> submit(MultivaluedMap<String, String> formParams) {
        String fileName = formParams.getFirst("fileName");
        String fileText = formParams.getFirst("fileText");
        
        try {
            TF textFile = getFilesDao().getFile(fileName, fileText);
            try {
                getIndexer().indexTextFile(textFile, fileText.toString());

                return simpleResponse("fileId", textFile.getFileId());
            } catch (IndexingException e) {
                logger.error("Problem by indexing", e);
                throw new MessageWebApplicationException(Response.Status.BAD_REQUEST, "Problem by indexing");
            }
        } catch (DaoException e) {
            logger.error("Problem getting text file from database - text file: " + fileName, e);
            throw new MessageWebApplicationException(Response.Status.BAD_REQUEST, "Problem getting text file from database - text file: " + fileName);
        }

    }
    
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public List<ResponseTextFile> listAllFiles() {
        List<ResponseTextFile> files = new LinkedList<ResponseTextFile>();
        for (TF textFile : getFilesDao().getAllFiles()) {
            files.add(new ResponseTextFile(textFile));
        }
        
        return files;
    }    
    
    @DELETE
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Path("/{fileId}")    
    public Map<String, Object> delete(@PathParam("fileId") int fileId) {
        getFilesDao().deleteFile(fileId);
        getKeywordsOccurrencesDao().deleteOccurrencesForFile(fileId);
        getIndexer().updateIdf();
        
        return simpleResponse("deleted", true);
    }
 
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Path("/{fileId}")    
    @SuppressWarnings("unchecked")
    public List<ResponseKeyword> get(@PathParam("fileId") int fileId) {
        List<ResponseKeyword> responseKeywords = new LinkedList<ResponseKeyword>();
        Double maxTfIdf = getKeywordsDao().getMaxTfIdf();
        logger.debug("Max TF-IDF: " + maxTfIdf);
        
        for (Object[] res : getKeywordsDao().getTopKeywordsWithScores(20, fileId)) {
            K keyword = (K) res[0];
            Double score = ((Double) res[1]) / maxTfIdf;
            responseKeywords.add(new ResponseKeyword(keyword.getWord(), score));
        }
        
        return responseKeywords;
    }
    
    private static Map<String, Object> simpleResponse(String name, Object value) {
        Map<String, Object> response = new HashMap<String, Object>();
        response.put(name, value);
        
        return response;
    }

    protected abstract KeywordsIndexer<TF> getIndexer();

    protected abstract FilesDao<TF> getFilesDao();

    protected abstract KeywordsDao<K> getKeywordsDao();
    
    protected abstract KeywordsOccurrencesDao<O, K, TF> getKeywordsOccurrencesDao();
}
