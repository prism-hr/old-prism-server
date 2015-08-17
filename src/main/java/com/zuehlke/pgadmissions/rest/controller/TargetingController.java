package com.zuehlke.pgadmissions.rest.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import javax.inject.Inject;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zuehlke.pgadmissions.mapping.ResourceMapper;
import com.zuehlke.pgadmissions.rest.representation.SubjectAreaRepresentation;
import com.zuehlke.pgadmissions.rest.representation.advert.CompetenceRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceChildCreationRepresentation;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.ImportedEntityService;

@RestController
@RequestMapping("/api/targeting")
@PreAuthorize("permitAll")
public class TargetingController {

    @Inject
    private ImportedEntityService importedEntityService;

    @Inject
    private AdvertService advertService;

    @Inject
    private ResourceMapper resourceMapper;

    @RequestMapping(value = "/subjectAreas", method = GET)
    public List<SubjectAreaRepresentation> getSimilarImportedSubjectAreas(String q) {
        return importedEntityService.getSimilarImportedSubjectAreas(q);
    }


    @RequestMapping(value = "/competences", method = GET)
    public List<CompetenceRepresentation> searchCompetences(@RequestParam String q) {
        return advertService.searchCompetences(q);
    }

    @RequestMapping(value = "/targetResource", method = GET)
    public List<ResourceChildCreationRepresentation> getTargetResources(@RequestParam String q) {
        return resourceMapper.getResourceTargetingRepresentations(q);
    }

}
