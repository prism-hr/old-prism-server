package com.zuehlke.pgadmissions.mvc.controllers;

import com.zuehlke.pgadmissions.services.ScraperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.List;

/**
 * Created by felipe on 02/06/2015.
 * <p/>
 * This controller is in charge of talking to search.ucas.com

 */
@RestController
@RequestMapping("api/scrapper")
public class ScraperController {
    @Autowired
    private ScraperService scraperService;

    private static Logger log = LoggerFactory.getLogger(ScraperController.class);


//   The response is an array of JSON as follows
//         [
//             "{"id": "41","label":"The University of Aberdeen "}",
//             "{"id": "1","label":"Abertay University "}",
//             "{"id": "21","label":"Aberystwyth University "}"
//         ]
    @ResponseBody
    @RequestMapping(value = "/institutions", method = RequestMethod.GET, produces = "application/json")
    public List<Object> getInstitutionIds() throws IOException {
        log.debug("getInstitutionIds() - start method");
        return scraperService.getInstitutionIdsBasedInUK();
    }

    @ResponseBody
    @RequestMapping(value = "/programs", method = RequestMethod.POST)
    public void getPrograms(@RequestParam String yearOfInterest) throws IOException, ParserConfigurationException, TransformerException {
        log.debug("getPrograms() - start method");
        scraperService.getProgramsForImportedInstitutions(yearOfInterest);
    }

    //manual importer for programs
    @ResponseBody
    @RequestMapping(value= "/importPrograms", method = RequestMethod.POST)
    public void importPrograms() throws IOException, SAXException, ParserConfigurationException {
    }

    @ResponseBody
    @RequestMapping(value= "/createScoring", method = RequestMethod.GET)
    public void generateScoringForProgramsAndSubjectAreas() throws IOException, SAXException, ParserConfigurationException {
        scraperService.generateScoringForProgramsAndSubjectAreas();
    }

    @ResponseBody
    @RequestMapping(value= "/importSubjectAreas", method = RequestMethod.POST)
    public void importSubjectAreas() throws IOException, SAXException, ParserConfigurationException {
        scraperService.importSubjectAreas();
    }

}
