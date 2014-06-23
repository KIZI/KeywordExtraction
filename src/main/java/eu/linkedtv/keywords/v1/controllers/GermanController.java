package eu.linkedtv.keywords.v1.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eu.linkedtv.keywords.v1.dao.DaoException;
import eu.linkedtv.keywords.v1.dao.FilesDao;
import eu.linkedtv.keywords.v1.dao.KeywordsOccurrencesDao;
import eu.linkedtv.keywords.v1.indexers.IndexingException;
import eu.linkedtv.keywords.v1.indexers.KeywordsIndexer;
import eu.linkedtv.keywords.v1.models.GermanKeyword;
import eu.linkedtv.keywords.v1.models.GermanKeywordsOccurrence;
import eu.linkedtv.keywords.v1.models.GermanTextFile;

@Controller
@RequestMapping(value = "/german")
public class GermanController {
    private static Logger logger = LoggerFactory.getLogger(GermanController.class);

    @Autowired
    @Qualifier("germanKeywordsIndexer")
    private KeywordsIndexer<GermanTextFile> indexer;

    @Autowired
    @Qualifier("germanFilesDao")
    private FilesDao<GermanTextFile> filesDao;
    
    @Autowired
    @Qualifier("germanKeywordsOccurrencesDao")
    private KeywordsOccurrencesDao<GermanKeywordsOccurrence, GermanKeyword, GermanTextFile> keywordsOccurrencesDao;    

    @RequestMapping(method = RequestMethod.POST, value = "/index")
    public String indexFile(@RequestParam("filename") String fileName, @RequestParam("filetext") String fileText) {
        try {
            GermanTextFile germanTextFile = filesDao.getFile(fileName, fileText);
            try {
                indexer.indexTextFile(germanTextFile, fileText.toString());

                return "redirect:/app/index?defile=" + germanTextFile.getFileId();
            } catch (IndexingException e) {
                logger.error("Problem by indexing", e);
            }
        } catch (DaoException e) {
            logger.error("Problem getting text file from database - text file: " + fileName, e);
        }

        return "redirect:/app/index?deerror=1";
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/delete")
    public String deleteFile(@RequestParam("fileId") int fileId) {
        filesDao.deleteFile(fileId);
        keywordsOccurrencesDao.deleteOccurrencesForFile(fileId);
        indexer.updateIdf();

        return "redirect:/app/index?deleted=" + fileId;
    }    
}
