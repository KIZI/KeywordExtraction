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
import eu.linkedtv.keywords.v1.models.DutchKeyword;
import eu.linkedtv.keywords.v1.models.DutchKeywordsOccurrence;
import eu.linkedtv.keywords.v1.models.DutchTextFile;

@Controller
@RequestMapping(value = "/dutch")
public class DutchController {
    private static Logger logger = LoggerFactory.getLogger(DutchController.class);

    @Autowired
    @Qualifier("dutchKeywordsIndexer")
    private KeywordsIndexer<DutchTextFile> indexer;

    @Autowired
    @Qualifier("dutchFilesDao")
    private FilesDao<DutchTextFile> filesDao;
    
    @Autowired
    @Qualifier("dutchKeywordsOccurrencesDao")
    private KeywordsOccurrencesDao<DutchKeywordsOccurrence, DutchKeyword, DutchTextFile> keywordsOccurrencesDao;

    @RequestMapping(method = RequestMethod.POST, value = "/index")
    public String indexFile(@RequestParam("filename") String fileName, @RequestParam("filetext") String fileText) {
        try {
            DutchTextFile dutchTextFile = filesDao.getFile(fileName, fileText);
            try {
                indexer.indexTextFile(dutchTextFile, fileText.toString());

                return "redirect:/app/index?nlfile=" + dutchTextFile.getFileId();
            } catch (IndexingException e) {
                logger.error("Problem by indexing", e);
            }
        } catch (DaoException e) {
            logger.error("Problem getting text file from database - text file: " + fileName, e);
        }

        return "redirect:/app/index?nlerror=1";
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/delete")
    public String deleteFile(@RequestParam("fileId") int fileId) {
        filesDao.deleteFile(fileId);
        keywordsOccurrencesDao.deleteOccurrencesForFile(fileId);
        indexer.updateIdf();

        return "redirect:/app/index?deleted=" + fileId;
    }    
}
