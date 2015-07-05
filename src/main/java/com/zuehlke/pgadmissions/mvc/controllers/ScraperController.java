package com.zuehlke.pgadmissions.mvc.controllers;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.zuehlke.pgadmissions.services.ScraperService;

/**
 * Created by felipe on 02/06/2015.
 * <p/>
 * This controller is in charge of talking to search.ucas.com

 */
@RestController
@RequestMapping("api/scrapper")
public class ScraperController {
    
    @Inject
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
    @RequestMapping(value = "/programs", method = RequestMethod.GET, produces = "application/xml")
    public Object getPrograms(@RequestParam String yearOfInterest) throws IOException, ParserConfigurationException, TransformerException {
        log.debug("getPrograms() - start method");
        return scraperService.getProgramsForImportedInstitutions(yearOfInterest);
    }
}
