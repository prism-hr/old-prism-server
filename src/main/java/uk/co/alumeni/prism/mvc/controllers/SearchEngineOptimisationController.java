package uk.co.alumeni.prism.mvc.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import uk.co.alumeni.prism.services.SearchEngineOptimisationService;

@RestController
@RequestMapping("api/sitemap")
public class SearchEngineOptimisationController {

    @Autowired
    private SearchEngineOptimisationService searchEngineOptimisationService;

    @RequestMapping(value = "/sitemap_index.xml", method = RequestMethod.GET)
    public String getSitemapIndex() throws Exception {
        return searchEngineOptimisationService.getSitemapIndex();
    }

    @RequestMapping(value = "/project_sitemap.xml", method = RequestMethod.GET)
    public String getProjectSitemap() throws Exception {
        return searchEngineOptimisationService.getProjectSitemap();
    }

    @RequestMapping(value = "/program_sitemap.xml", method = RequestMethod.GET)
    public String getProgramSitemap() throws Exception {
        return searchEngineOptimisationService.getProgramSitemap();
    }

    @RequestMapping(value = "/department_sitemap.xml", method = RequestMethod.GET)
    public String getDepartmentSitemap() throws Exception {
        return searchEngineOptimisationService.getDepartmentSitemap();
    }

    @RequestMapping(value = "/institution_sitemap.xml", method = RequestMethod.GET)
    public String getInstitutionSitemap() throws Exception {
        return searchEngineOptimisationService.getInstitutionSitemap();
    }

}
