package eu.linkedtv.keywords.v1.controllers;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import eu.linkedtv.keywords.v1.dao.FilesDao;
import eu.linkedtv.keywords.v1.dao.KeywordsDao;
import eu.linkedtv.keywords.v1.models.DutchKeyword;
import eu.linkedtv.keywords.v1.models.DutchTextFile;
import eu.linkedtv.keywords.v1.models.GermanKeyword;
import eu.linkedtv.keywords.v1.models.GermanTextFile;

@Controller
public class IndexController {

    private static Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    @Qualifier("germanKeywordsDao")
    private KeywordsDao<GermanKeyword> germanKeywordsDao;

    @Autowired
    @Qualifier("dutchKeywordsDao")
    private KeywordsDao<DutchKeyword> dutchKeywordsDao;
    
    @Autowired
    @Qualifier("germanFilesDao")
    private FilesDao<GermanTextFile> germanFilesDao;

    @Autowired
    @Qualifier("dutchFilesDao")
    private FilesDao<DutchTextFile> dutchFilesDao;

    @RequestMapping(method = RequestMethod.GET, value = "/index")
    public ModelAndView showIndexPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("Display indexpage");
        ModelAndView mav = new ModelAndView("index");
        
        mav.addObject("deFiles", germanFilesDao.getAllFiles());
        mav.addObject("nlFiles", dutchFilesDao.getAllFiles());
        

        if (request.getParameter("defile") != null) {
            int germanFileId = new Integer(request.getParameter("defile"));
            List<GermanKeyword> deKeywords = germanKeywordsDao.getTopGermanKeywords(20, germanFileId);
            mav.addObject("deKeywords", deKeywords);
            GermanTextFile germanTextFile = germanFilesDao.getFile(germanFileId);
            mav.addObject("deFileName", germanTextFile.getName());
            mav.addObject("deKvText", germanTextFile.getOriginalText());
        }

        if (request.getParameter("nlfile") != null) {
            int dutchFileId = new Integer(request.getParameter("nlfile"));
            List<DutchKeyword> nlKeywords = dutchKeywordsDao.getTopKeywords(20, dutchFileId);
            mav.addObject("nlKeywords", nlKeywords);
            DutchTextFile dutchTextFile = dutchFilesDao.getFile(dutchFileId);
            mav.addObject("nlFileName", dutchTextFile.getName());
            mav.addObject("nlKvText", dutchTextFile.getOriginalText());            
        }
        
        return mav;
    }

}
