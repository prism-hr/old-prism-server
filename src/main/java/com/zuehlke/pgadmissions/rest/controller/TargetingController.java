package com.zuehlke.pgadmissions.rest.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import javax.inject.Inject;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.zuehlke.pgadmissions.mapping.ResourceMapper;
import com.zuehlke.pgadmissions.rest.representation.SubjectAreaRepresentation;
import com.zuehlke.pgadmissions.rest.representation.advert.CompetenceRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceChildCreationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.institution.ResourceRepresentationTargeting;
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

    @RequestMapping(value = "/resources", method = GET)
    public List<ResourceChildCreationRepresentation> getResources(@RequestParam String q) {
        return resourceMapper.getResourceTargetingRepresentations(q);
    }

    @ResponseBody
    @RequestMapping(value = "/targetResources", method = RequestMethod.GET)
    public List<ResourceRepresentationTargeting> getTargetResources(@RequestParam Integer advertId, @RequestParam(required = false) List<Integer> subjectAreas,
            @RequestParam(required = false) List<Integer> institutions, @RequestParam(required = false) List<Integer> departments) {
        return resourceMapper.getResourceTargetingRepresentations(advertService.getById(advertId), subjectAreas, institutions, departments);
    }

}
