package eu.linkedtv.keywords.v2.rest;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionSystemException;

import eu.linkedtv.keywords.v1.indexers.IndexingException;
import eu.linkedtv.keywords.v1.rest.MessageWebApplicationException;
import eu.linkedtv.keywords.v1.rest.model.ResponseKeyword;
import eu.linkedtv.keywords.v2.dao.FilesDao;
import eu.linkedtv.keywords.v2.dao.KeywordsDao;
import eu.linkedtv.keywords.v2.dao.KeywordsOccurrencesDao;
import eu.linkedtv.keywords.v2.dao.UsersDao;
import eu.linkedtv.keywords.v2.exception.NotAuthorizedAccessException;
import eu.linkedtv.keywords.v2.indexers.KeywordsIndexer;
import eu.linkedtv.keywords.v2.indexers.TTKeywordsIndexer;
import eu.linkedtv.keywords.v2.languages.SupportedLanguages;
import eu.linkedtv.keywords.v2.models.TextFile;
import eu.linkedtv.keywords.v2.models.User;
import eu.linkedtv.keywords.v2.rest.model.ResponseTextFile;
import java.util.logging.Level;
import org.springframework.transaction.annotation.Transactional;

@Component
@Path("/v2")
public class RestService {

    private static Logger logger = LoggerFactory.getLogger(RestService.class);

    @Autowired
    @Qualifier("germanV2KeywordsIndexer")
    protected TTKeywordsIndexer germanIndexer;

    @Autowired
    @Qualifier("dutchV2KeywordsIndexer")
    protected TTKeywordsIndexer dutchIndexer;

    @Autowired
    @Qualifier("englishV2KeywordsIndexer")
    protected TTKeywordsIndexer englishIndexer;

    @Autowired
    @Qualifier("filesDao")
    protected FilesDao filesDao;

    @Autowired
    @Qualifier("keywordsDao")
    protected KeywordsDao keywordsDao;

    @Autowired
    @Qualifier("keywordsOccurrencesDao")
    protected KeywordsOccurrencesDao keywordsOccurrencesDao;

    @Autowired
    protected UsersDao usersDao;

    @GET
    @Path("/ping")
    public String test() {
        return "ping SUCCESS";
    }

    /**
     *
     * @param formParams
     * @return User key for authentication.
     */
    @POST
    @Consumes("application/x-www-form-urlencoded")
    @Produces({MediaType.TEXT_PLAIN})
    @Path("/user")
    public String addUser(MultivaluedMap<String, String> formParams) {
        String userName = formParams.getFirst("userName");
        String password = formParams.getFirst("password");
        try {
            return usersDao.addUser(userName, password);
        } catch (TransactionSystemException e) {
            throw new MessageWebApplicationException(Response.Status.BAD_REQUEST, "Error: User with this name (" + userName + ") already exists");
        }
    }

    /**
     *
     * @param formParams
     * @return User key for authentication.
     */
    @GET
    @Consumes("application/x-www-form-urlencoded")
    @Produces({MediaType.TEXT_PLAIN})
    @Path("/user")
    public String login(@HeaderParam("userName") String userName, @HeaderParam("password") String password) {
        User user = usersDao.getUser(userName, password);
        if (user == null) {
            throw new MessageWebApplicationException(Response.Status.FORBIDDEN, "Login incorrect");
        } else {
            return user.getKey();
        }
    }

    @DELETE
    @Produces({MediaType.TEXT_PLAIN})
    @Path("/user")
    @Transactional
    public String deleteUser(@HeaderParam("userName") String userName, @HeaderParam("password") String password) {
        User user = usersDao.getUser(userName, password);
        if (user == null) {
            throw new MessageWebApplicationException(Response.Status.FORBIDDEN, "Login incorrect");
        } else {
            synchronized (RestService.class) {
                try {
                    List<TextFile> allFiles = filesDao.getAllFiles(user);
                    for (TextFile textFile : allFiles) {
                        keywordsOccurrencesDao.deleteOccurrencesForFile(textFile);
                        filesDao.deleteFile(textFile.getFileId(), user);
                    }
                    usersDao.delete(user);
                } catch (NotAuthorizedAccessException ex) {
                    throw new MessageWebApplicationException(Response.Status.FORBIDDEN, "Login incorrect");
                }
            }
            return "deleted " + user.getUserId();
        }
    }

    @POST
    @Consumes("application/x-www-form-urlencoded")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/{language}")
    @Transactional
    public Map<String, Object> submit(@PathParam("language") String languageParam, @HeaderParam("userKey") String userKey, MultivaluedMap<String, String> formParams) {
        String fileName = formParams.getFirst("fileName");
        String fileText = formParams.getFirst("fileText");

        User user = usersDao.getUserByKey(userKey);
        SupportedLanguages language = SupportedLanguages.languageFactory(languageParam);

        if (user != null) {
            try {
                synchronized (RestService.class) {
                    TextFile file = filesDao.getFile(fileName, fileText, user, language);
                    getIndexer(language).indexTextFile(file, fileText, user, language);
                    return simpleResponse("fileId", file.getFileId());
                }
            } catch (IndexingException e) {
                logger.error("Problem by indexing", e);
                throw new MessageWebApplicationException(Response.Status.BAD_REQUEST, "Problem by indexing");
            }
        } else {
            throw new MessageWebApplicationException(Response.Status.BAD_REQUEST, "User not found");
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<ResponseTextFile> listAllFiles(@HeaderParam("userKey") String userKey) {
        List<ResponseTextFile> files = new LinkedList<ResponseTextFile>();

        User user = usersDao.getUserByKey(userKey);
        if (user != null) {
            for (TextFile textFile : filesDao.getAllFiles(user)) {
                files.add(new ResponseTextFile(textFile));
            }
        }

        return files;
    }

    @DELETE
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/{language}/{fileId}")
    @Transactional
    public Map<String, Object> delete(@PathParam("language") String languageParam, @HeaderParam("userKey") String userKey, @PathParam("fileId") int fileId) {
        User user = authorizeUser(userKey);
        SupportedLanguages language = SupportedLanguages.languageFactory(languageParam);

        try {
            synchronized (RestService.class) {
                filesDao.deleteFile(fileId, user);
                keywordsOccurrencesDao.deleteOccurrencesForFile(fileId);
                //getIndexer(language).updateIdf(user, language);
                return simpleResponse("deleted", true);
            }
        } catch (NotAuthorizedAccessException e) {
            throw new MessageWebApplicationException(Response.Status.FORBIDDEN, "Unauthorized request");
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/{fileId}")
    public Map<String, Object> get(@PathParam("fileId") int fileId, @HeaderParam("userKey") String userKey) {
        User user = authorizeUser(userKey);
        List<ResponseKeyword> responseKeywords = new LinkedList<ResponseKeyword>();
//        Double maxTfIdf = keywordsDao.getMaxTfIdf();
//        logger.debug("Max TF-IDF: " + maxTfIdf);

        try {
            TextFile file = filesDao.getFile(fileId, user);
            SupportedLanguages lang = SupportedLanguages.ENGLISH;
            for (SupportedLanguages sl : SupportedLanguages.values()) {
                if (sl.getLanguageId() == file.getLanguageId()) {
                    lang = sl;
                }
            }
            for (Object[] res : keywordsDao.getTopKeywordsWithScores(20, fileId, keywordsDao.getDocumentCount(user.getUserId(), lang))) {
                String keyword = (String) res[0];
                //Double score = ((Double) res[1]) / maxTfIdf;
                Double score = (Double) res[1];
                responseKeywords.add(new ResponseKeyword(keyword, score));
            }

            Map<String, Object> response = new HashMap<String, Object>();
            response.put("originalText", file.getOriginalText());
            response.put("keywords", responseKeywords);

            return response;
        } catch (NotAuthorizedAccessException e) {
            throw new MessageWebApplicationException(Response.Status.FORBIDDEN, "Unauthorized request");
        }
    }

    private static Map<String, Object> simpleResponse(String name, Object value) {
        Map<String, Object> response = new HashMap<String, Object>();
        response.put(name, value);

        return response;
    }

    protected KeywordsIndexer<TextFile> getIndexer(SupportedLanguages language) {
        if (language.equals(SupportedLanguages.DUTCH)) {
            return dutchIndexer;
        } else if (language.equals(SupportedLanguages.GERMAN)) {
            return germanIndexer;
        } else {
            return englishIndexer;
        }
    }

    protected User authorizeUser(String userKey) {
        User user = usersDao.getUserByKey(userKey);
        if (user == null) {
            throw new MessageWebApplicationException(Response.Status.BAD_REQUEST, "User not found");
        } else {
            return user;
        }
    }
}
