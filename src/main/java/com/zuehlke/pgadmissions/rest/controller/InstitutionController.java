package com.zuehlke.pgadmissions.rest.controller;

import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
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
import com.zuehlke.pgadmissions.rest.representation.resource.institution.InstitutionRepresentationTargeting;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.InstitutionService;
import com.zuehlke.pgadmissions.services.ProgramService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uk.co.alumeni.prism.api.model.imported.request.ImportedEntityRequest;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;

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
    private AdvertService advertService;

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
    public List<ResourceChildCreationRepresentation> getAcceptingInstitutions(@RequestParam PrismScope accepting) {
        List<ResourceChildCreationDTO> institutions;
        if (accepting == PROGRAM) {
            institutions = institutionService.getInstitutionsForWhichUserCanCreateProgram();
        } else if (accepting == PROJECT) {
            institutions = institutionService.getInstitutionsForWhichUserCanCreateProject();
        } else {
            throw new Error();
        }
        return institutions.stream().map(new AcceptingResourceToRepresentationFunction()).collect(Collectors.toList());
    }

    @RequestMapping(value = "/{institutionId}/resources", method = RequestMethod.GET, params = "accepting")
    public List<ResourceChildCreationRepresentation> getAcceptingResources(@PathVariable Integer institutionId, @RequestParam PrismScope accepting) {
        List<ResourceChildCreationDTO> programs = programService.getProgramsForWhichUserCanCreateProject(institutionId);
        return programs.stream().map(new AcceptingResourceToRepresentationFunction()).collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.GET, params = "googleId")
    @ResponseBody
    public ResourceRepresentationSimple getInstitution(String googleId) {
        Institution institution = institutionService.getActivatedInstitutionByGoogleId(googleId);
        return institution == null ? null : resourceMapper.getResourceRepresentationSimple(institution);
    }

    @RequestMapping(method = RequestMethod.GET, params = { "subjectAreas", "advertId" })
    @ResponseBody
    public List<InstitutionRepresentationTargeting> getInstitutionsBySubjectAreas(@RequestParam List<Integer> subjectAreas, @RequestParam Integer advertId) {
        return institutionService.getInstitutionBySubjectAreas(advertService.getById(advertId).getAddress().getCoordinates(), subjectAreas);
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
