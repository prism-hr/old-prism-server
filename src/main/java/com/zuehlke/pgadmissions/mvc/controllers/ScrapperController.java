package com.zuehlke.pgadmissions.mvc.controllers;

import com.zuehlke.pgadmissions.services.ScrapperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * Created by felipe on 02/06/2015.
 * <p/>
 * This controller is in charge of exposing the institution ID's and label (university name) we gather from search.ucas.com
 * The response is an array of JSON as follows
 * [
 *  "{"id": "41","label":"The University of Aberdeen "}",
    "{"id": "1","label":"Abertay University "}",
    "{"id": "21","label":"Aberystwyth University "}"
   ]
 */
@RestController
@RequestMapping("api/scrapper")
public class ScrapperController {
    @Autowired
    private ScrapperService scrapperService;

    private static Logger log = LoggerFactory.getLogger(ScrapperController.class);

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public List<Object> getInstitutionIds() throws IOException {
        log.debug("getInstitutionIds() - start method");
        return scrapperService.getInstitutionIdsBasedInUK();
    }
}
