package com.zuehlke.pgadmissions.rest.controller;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.mapping.ImportedEntityMapper;
import com.zuehlke.pgadmissions.mapping.ResourceMapper;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationLocation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.InstitutionService;
import com.zuehlke.pgadmissions.services.ProgramService;

import uk.co.alumeni.prism.api.model.imported.request.ImportedEntityRequest;

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

    @RequestMapping(method = RequestMethod.GET, params = "type=simple")
    public List<ResourceRepresentationLocation> getInstitutions(@RequestParam(required = false) Boolean activeOnly,
            @RequestParam(required = false) String query, @RequestParam(required = false) String[] googleIds) {
        return institutionService.getInstitutions(BooleanUtils.isTrue(activeOnly), query, googleIds);
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

}
