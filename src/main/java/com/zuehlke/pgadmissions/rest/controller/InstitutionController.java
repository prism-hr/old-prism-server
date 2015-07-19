package com.zuehlke.pgadmissions.rest.controller;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import uk.co.alumeni.prism.api.model.imported.request.ImportedEntityRequest;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.address.AddressAdvert;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.dto.ResourceChildCreationDTO;
import com.zuehlke.pgadmissions.mapping.ImportedEntityMapper;
import com.zuehlke.pgadmissions.mapping.ResourceMapper;
import com.zuehlke.pgadmissions.rest.representation.DocumentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceChildCreationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
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
    private ImportedEntityService importedEntityService;

    @Inject
    private ImportedEntityMapper importedEntityMapper;

    @Inject
    private ResourceMapper resourceMapper;

    @RequestMapping(method = RequestMethod.GET, params = "type=simple")
    public List<ResourceRepresentationSimple> getInstitutions() {
        List<Institution> institutions;
        institutions = institutionService.list();
        List<ResourceRepresentationSimple> representations = Lists.newArrayListWithCapacity(institutions.size());
        for (Institution institution : institutions) {
            AddressAdvert address = institution.getAdvert().getAddress();
            String name = Joiner.on(" - ").skipNulls().join(institution.getTitle(), address.getAddressTown(), address.getAddressCode());
            ResourceRepresentationSimple representation = new ResourceRepresentationSimple().withId(institution.getId()).withTitle(name);

            Document logoImage = institution.getLogoImage();
            if (logoImage != null) {
                representation.setLogoImage(new DocumentRepresentation().withId(logoImage.getId()));
            }

            representations.add(representation);
        }
        return representations;
    }

    @RequestMapping(method = RequestMethod.GET, params = "query")
    public List<ResourceRepresentationSimple> getInstitutions(@RequestParam String query, @RequestParam(required = false) String[] googleIds) {
        List<ResourceRepresentationSimple> representations = Lists.newLinkedList();
        for (Institution institution : institutionService.getInstitutions(query, googleIds)) {
            representations.add(resourceMapper.getResourceRepresentationSimple(institution));
        }
        return representations;
    }

    @RequestMapping(method = RequestMethod.GET, params = "accepting")
    public List<ResourceChildCreationRepresentation> getAcceptingInstitutions(@RequestParam String accepting) {
        List<ResourceChildCreationDTO> institutions;
        if (accepting.equals("programs")) {
            institutions = institutionService.getInstitutionsForWhichUserCanCreateProgram();
        } else if (accepting.equals("projects")) {
            institutions = institutionService.getInstitutionsForWhichUserCanCreateProject();
        } else {
            throw new Error();
        }
        return Lists.transform(institutions, new AcceptingResourceToRepresentationFunction());
    }

    @RequestMapping(value = "/{institutionId}/programs", method = RequestMethod.GET, params = "accepting=projects")
    public List<ResourceChildCreationRepresentation> getAcceptingPrograms(@PathVariable Integer institutionId) {
        List<ResourceChildCreationDTO> programs = programService.getProgramsForWhichUserCanCreateProject(institutionId);
        return Lists.transform(programs, new AcceptingResourceToRepresentationFunction());
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
        categoryTags.put("themes", advertService.getAdvertThemes(institution.getAdvert()));
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

    @RequestMapping(value = "/{institutionId}/importedData/{type}", method = RequestMethod.POST)
    public <T extends ImportedEntityRequest> void importData(@PathVariable Integer institutionId, @PathVariable PrismImportedEntity type,
            HttpServletRequest request) throws Exception {
        List<T> representations = importedEntityMapper.getImportedEntityRepresentations(type, request.getInputStream());
        importedEntityService.mergeImportedEntities(institutionService.getById(institutionId), type, representations);
    }

    private static class AcceptingResourceToRepresentationFunction implements Function<ResourceChildCreationDTO, ResourceChildCreationRepresentation> {
        @Override
        public ResourceChildCreationRepresentation apply(ResourceChildCreationDTO input) {
            PrismOpportunityType opportunityType = null;
            ResourceParent resource = input.getResource();
            if (ResourceOpportunity.class.isAssignableFrom(resource.getClass())) {
                opportunityType = PrismOpportunityType.valueOf(((ResourceOpportunity) resource).getOpportunityType().getName());
            }
            return new ResourceChildCreationRepresentation().withId(resource.getId()).withTitle(resource.getTitle())
                    .withPartnerMode(input.getPartnerMode()).withOpportunityType(opportunityType);
        }
    }

}
