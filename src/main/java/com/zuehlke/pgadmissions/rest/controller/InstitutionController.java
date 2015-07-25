package com.zuehlke.pgadmissions.rest.controller;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

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

import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.dto.ResourceChildCreationDTO;
import com.zuehlke.pgadmissions.mapping.ImportedEntityMapper;
import com.zuehlke.pgadmissions.mapping.InstitutionMapper;
import com.zuehlke.pgadmissions.mapping.ResourceMapper;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceChildCreationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.resource.institution.InstitutionRepresentationSimple;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.InstitutionService;
import com.zuehlke.pgadmissions.services.ProgramService;

@RestController
@RequestMapping("api/institutions")
@PreAuthorize("permitAll")
public class InstitutionController {

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

    @Inject
    private InstitutionMapper institutionMapper;

    @RequestMapping(method = RequestMethod.GET, params = "type=simple")
    public List<InstitutionRepresentationSimple> getInstitutions(@RequestParam(required = false) String query,
            @RequestParam(required = false) String[] googleIds) {
        return institutionService.getInstitutions(query, googleIds).stream()
                .map(institutionMapper::getInstitutionRepresentationSimple)
                .collect(Collectors.toList());
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
        return institutions.stream().map(new AcceptingResourceToRepresentationFunction()).collect(Collectors.toList());
    }

    @RequestMapping(value = "/{institutionId}/programs", method = RequestMethod.GET, params = "accepting=projects")
    public List<ResourceChildCreationRepresentation> getAcceptingPrograms(@PathVariable Integer institutionId) {
        List<ResourceChildCreationDTO> programs = programService.getProgramsForWhichUserCanCreateProject(institutionId);
        return programs.stream().map(new AcceptingResourceToRepresentationFunction()).collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.GET, params = "googleId")
    @ResponseBody
    public ResourceRepresentationSimple getInstitution(String googleId) {
        Institution institution = institutionService.getActivatedInstitutionByGoogleId(googleId);
        return institution == null ? null : resourceMapper.getResourceRepresentationSimple(institution);
    }

    @RequestMapping(value = "/{institutionId}/programs", method = RequestMethod.GET)
    public List<ResourceRepresentationSimple> getPrograms(@PathVariable Integer institutionId) {
        return programService.getApprovedPrograms(institutionId);
    }

    @RequestMapping(value = "/{institutionId}/similarPrograms", method = RequestMethod.GET)
    public List<ResourceRepresentationSimple> getSimilarPrograms(@PathVariable Integer institutionId, @RequestParam String searchTerm) {
        return programService.getSimilarPrograms(institutionId, searchTerm);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/{institutionId}/importedData/{type}", method = RequestMethod.POST)
    public <T extends ImportedEntityRequest> void importData(@PathVariable Integer institutionId, @PathVariable PrismImportedEntity type,
            HttpServletRequest request) throws IOException {
        Class<T> requestClass = (Class<T>) type.getRequestClass();
        List<T> representations = importedEntityMapper.getImportedEntityRepresentations(requestClass, request.getInputStream());
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
            return new ResourceChildCreationRepresentation().withId(resource.getId()).withName(resource.getName())
                    .withPartnerMode(input.getPartnerMode()).withOpportunityType(opportunityType);
        }
    }

}
