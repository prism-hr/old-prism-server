package com.zuehlke.pgadmissions.mvc.controllers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.zuehlke.pgadmissions.services.scraping.InstitutionUcasScraper;
import com.zuehlke.pgadmissions.services.scraping.ProgramUcasScraper;

/**
 * This controller is in charge of talking to search.ucas.com
 */
@RestController
@RequestMapping("api/scraper")
public class ScraperController {

    private static Logger log = LoggerFactory.getLogger(ScraperController.class);

    @Inject
    private ProgramUcasScraper programUcasScraper;

    @Inject
    private InstitutionUcasScraper institutionUcasScraper;

    //   The response is an array of JSON as follows
//         [
//             "{"id": "41","label":"The University of Aberdeen "}",
//             "{"id": "1","label":"Abertay University "}",
//             "{"id": "21","label":"Aberystwyth University "}"
//         ]
    @ResponseBody
    @RequestMapping(value = "/institutions", method = RequestMethod.GET, produces = "application/json")
    public void getInstitutionIds() throws IOException {
        log.debug("getInstitutionIds() - start method");
        institutionUcasScraper.scrape(null);
    }

    @ResponseBody
    @RequestMapping(value = "/programs", method = RequestMethod.GET)
    public void getPrograms() throws IOException {
        log.debug("getPrograms() - start method");
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream("dupa.json"))) {
            programUcasScraper.scrape(writer);
        }
    }

    //manual importer for programs
    @ResponseBody
    @RequestMapping(value = "/importPrograms", method = RequestMethod.GET)
    public void importPrograms() throws IOException {
    }

//    @ResponseBody
//    @RequestMapping(value = "/createScoring", method = RequestMethod.GET)
//    public void generateScoringForProgramsAndSubjectAreas() throws IOException, SAXException, ParserConfigurationException {
//        subjectAreaHesaScraper.generateScoringForProgramsAndSubjectAreas();
//    }

}
