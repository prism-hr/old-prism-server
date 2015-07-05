package com.zuehlke.pgadmissions.rest.controller;

import java.util.List;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.imported.ImportedAdvertDomicile;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.mapping.ResourceMapper;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.InstitutionService;

@RestController
@RequestMapping("api/domiciles/{domicileId}")
public class InstitutionDomicileController {

    @Inject
    private EntityService entityService;

    @Inject
    private ResourceMapper resourceMapper;

    @Inject
    private InstitutionService institutionService;

    @RequestMapping(value = "institutions", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public List<ResourceRepresentationSimple> getInstitutions(@PathVariable String domicileId) {
        ImportedAdvertDomicile domicile = entityService.getByProperty(ImportedAdvertDomicile.class, "id", domicileId);
        List<Institution> institutions = institutionService.getApprovedInstitutionsByDomicile(domicile);
        List<ResourceRepresentationSimple> institutionRepresentations = Lists.newArrayListWithCapacity(institutions.size());
        for (Institution institution : institutions) {
            institutionRepresentations.add(resourceMapper.getResourceRepresentationSimple(institution));
        }
        return institutionRepresentations;
    }

}
