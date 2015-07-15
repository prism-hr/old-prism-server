package com.zuehlke.pgadmissions.mvc.controllers;

import java.io.UnsupportedEncodingException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zuehlke.pgadmissions.services.SearchEngineOptimisationService;

@RestController
@RequestMapping("api/sitemap")
public class SearchEngineOptimisationController {
    
    @Autowired
    private SearchEngineOptimisationService searchEngineOptimisationService;

    @RequestMapping(value = "/sitemap_index.xml", method = RequestMethod.GET)
    public String getSitemapIndex() throws ParserConfigurationException, TransformerException {
        return searchEngineOptimisationService.getSitemapIndex();
    }

    @RequestMapping(value = "/project_sitemap.xml", method = RequestMethod.GET)
    public String getProjectSitemap() throws UnsupportedEncodingException, TransformerException, ParserConfigurationException {
        return searchEngineOptimisationService.getProjectSitemap();
    }

    @RequestMapping(value = "/program_sitemap.xml", method = RequestMethod.GET)
    public String getProgramSitemap() throws UnsupportedEncodingException, TransformerException, ParserConfigurationException {
        return searchEngineOptimisationService.getProgramSitemap();
    }

    @RequestMapping(value = "/institution_sitemap.xml", method = RequestMethod.GET)
    public String getInstitutionSitemap() throws UnsupportedEncodingException, TransformerException, ParserConfigurationException {
        return searchEngineOptimisationService.getInstitutionSitemap();
    }

}
