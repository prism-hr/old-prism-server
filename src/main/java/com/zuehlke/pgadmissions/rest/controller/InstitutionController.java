package com.zuehlke.pgadmissions.rest.controller;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.address.AddressAdvert;
import com.zuehlke.pgadmissions.domain.advert.AdvertCompetence;
import com.zuehlke.pgadmissions.domain.advert.AdvertTheme;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.dto.ResourceForWhichUserCanCreateChildDTO;
import com.zuehlke.pgadmissions.mappers.ResourceMapper;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceChildCreationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.InstitutionService;
import com.zuehlke.pgadmissions.services.ProgramService;

@RestController
@RequestMapping("api/institutions")
@PreAuthorize("permitAll")
public class InstitutionController {

    @Inject
    private AdvertService advertService;

    @Inject
    private ProgramService programService;

    @Inject
    private InstitutionService institutionService;

    @Inject
    private ResourceMapper resourceMapper;

    @RequestMapping(method = RequestMethod.GET, params = "type=simple")
    public List<ResourceRepresentationSimple> getInstitutions() {
        List<Institution> institutions;
        institutions = institutionService.list();
        List<ResourceRepresentationSimple> institutionRepresentations = Lists.newArrayListWithCapacity(institutions.size());
        for (Institution institution : institutions) {
            AddressAdvert address = institution.getAdvert().getAddress();
            String title = Joiner.on(" - ").skipNulls().join(institution.getTitle(), address.getAddressTown(), address.getAddressCode());
            ResourceRepresentationSimple institutionRepresentation = new ResourceRepresentationSimple().withResourceId(institution.getId()).withTitle(title);
            institutionRepresentations.add(institutionRepresentation);
        }
        return institutionRepresentations;
    }

    @RequestMapping(method = RequestMethod.GET, params = "query")
    public List<ResourceRepresentationSimple> getInstitutions(@RequestParam String query, @RequestParam(required = false) String[] googleIds) {
        List<ResourceRepresentationSimple> representations = Lists.newLinkedList();
        List<Institution> institutions = institutionService.getInstitutions(query, googleIds);
        for (Institution institution : institutions) {
            representations.add(resourceMapper.getResourceRepresentationSimple(institution));
        }
        return representations;
    }

    @RequestMapping(method = RequestMethod.GET, params = "accepting")
    public List<ResourceRepresentationSimple> getAcceptingInstitutions(@RequestParam String accepting) {
        List<ResourceForWhichUserCanCreateChildDTO> institutions;
        if (accepting.equals("programs")) {
            institutions = institutionService.getInstitutionsForWhichUserCanCreateProgram();
        } else if (accepting.equals("projects")) {
            institutions = institutionService.getInstitutionsForWhichUserCanCreateProject();
        } else {
            throw new Error();
        }
        return Lists.transform(institutions, new ResourceToResourceChildCreationRepresentationFunction());
    }

    @RequestMapping(value = "/{institutionId}/programs", method = RequestMethod.GET, params = "accepting=projects")
    public List<ResourceRepresentationSimple> getAcceptingPrograms(@PathVariable Integer institutionId) {
        List<ResourceForWhichUserCanCreateChildDTO> programs = programService.getProgramsForWhichUserCanCreateProject(institutionId);
        return Lists.transform(programs, new ResourceToResourceChildCreationRepresentationFunction());
    }

    @RequestMapping(method = RequestMethod.GET, params = "googleId")
    @ResponseBody
    public ResourceRepresentationSimple getInstitution(String googleId) {
        Institution institution = institutionService.getActivatedInstitutionByGoogleId(googleId);
        return institution == null ? null : resourceMapper.getResourceRepresentationSimple(institution);
    }

    @RequestMapping(value = "/{institutionId}/categoryTags", method = RequestMethod.GET)
    public Map<String, List<String>> getCategoryTags(@PathVariable Integer institutionId) throws Exception {
        Map<String, List<String>> categoryTags = Maps.newLinkedHashMap();
        Institution institution = institutionService.getById(institutionId);

        categoryTags.put("competences", advertService.getAdvertAttributes(institution, AdvertCompetence.class));
        categoryTags.put("themes", advertService.getAdvertAttributes(institution, AdvertTheme.class));
        return categoryTags;
    }

    @RequestMapping(value = "/{institutionId}/programs", method = RequestMethod.GET)
    public List<ResourceRepresentationSimple> getPrograms(@PathVariable Integer institutionId) throws Exception {
        return programService.getApprovedPrograms(institutionId);
    }

    @RequestMapping(value = "/{institutionId}/similarPrograms", method = RequestMethod.GET)
    public List<ResourceRepresentationSimple> getSimilarPrograms(@PathVariable Integer institutionId, @RequestParam String searchTerm) {
        return programService.getSimilarPrograms(institutionId, searchTerm);
    }

    private static class ResourceToResourceChildCreationRepresentationFunction implements
            Function<ResourceForWhichUserCanCreateChildDTO, ResourceRepresentationSimple> {
        @Override
        public ResourceChildCreationRepresentation apply(ResourceForWhichUserCanCreateChildDTO input) {
            ResourceParent resource = input.getResource();
            ImportedEntitySimple opportunityType = resource.getOpportunityType();
            PrismOpportunityType prismOpportunityType = opportunityType == null ? null : PrismOpportunityType.valueOf(opportunityType.getName());
            return new ResourceChildCreationRepresentation().withResourceId(resource.getId()).withTitle(resource.getTitle()).withOpportunityType(prismOpportunityType)
                    .withPartnerMode(input.getPartnerMode());
        }
    }

}
