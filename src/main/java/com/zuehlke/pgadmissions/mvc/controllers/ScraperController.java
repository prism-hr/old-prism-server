package com.zuehlke.pgadmissions.mvc.controllers;

import com.zuehlke.pgadmissions.services.scrapping.InstitutionUcasScraper;
import com.zuehlke.pgadmissions.services.scrapping.ProgramUcasScraper;
import com.zuehlke.pgadmissions.services.scrapping.SubjectAreaHesaScraper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

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

    @Inject
    private SubjectAreaHesaScraper subjectAreaHesaScraper;

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
        institutionUcasScraper.scrapeInstitutions(null);
    }

    @ResponseBody
    @RequestMapping(value = "/programs", method = RequestMethod.GET)
    public void getPrograms(@RequestParam String yearOfInterest) throws IOException {
        log.debug("getPrograms() - start method");
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream("dupa.json"))) {
            programUcasScraper.scrapePrograms(yearOfInterest, writer);
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

    @ResponseBody
    @RequestMapping(value = "/importSubjectAreas", method = RequestMethod.GET)
    public void importSubjectAreas() throws IOException {
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream("dupa.json"))) {
            subjectAreaHesaScraper.scrapeSubjectAreas(writer);
        }
    }

}
