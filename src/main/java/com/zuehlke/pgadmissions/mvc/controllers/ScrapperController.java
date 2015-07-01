package com.zuehlke.pgadmissions.mvc.controllers;

import com.zuehlke.pgadmissions.services.ScrapperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.ws.spi.http.HttpContext;
import java.io.IOException;
import java.util.List;

/**
 * Created by felipe on 02/06/2015.
 * <p/>
 * This controller is in charge of talking to search.ucas.com

 */
@RestController
@RequestMapping("api/scrapper")
public class ScrapperController {
    @Autowired
    private ScrapperService scrapperService;

    private static Logger log = LoggerFactory.getLogger(ScrapperController.class);


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
        return scrapperService.getInstitutionIdsBasedInUK();
    }

    @ResponseBody
    @RequestMapping(value = "/programs", method = RequestMethod.GET, produces = "application/xml")
    public Object getPrograms(@RequestParam String yearOfInterest) throws IOException, ParserConfigurationException, TransformerException {
        log.debug("getPrograms() - start method");
        return scrapperService.getProgramsForImportedInstitutions(yearOfInterest);
    }
}
