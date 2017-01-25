package uk.co.alumeni.prism.rest.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uk.co.alumeni.prism.domain.resource.Institution;
import uk.co.alumeni.prism.mapping.InstitutionMapper;
import uk.co.alumeni.prism.mapping.ResourceMapper;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationLocation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationSimple;
import uk.co.alumeni.prism.services.InstitutionService;

import javax.inject.Inject;
import java.util.List;

@RestController
@RequestMapping("api/institutions")
@PreAuthorize("permitAll")
public class InstitutionController {

    @Inject
    private InstitutionMapper institutionMapper;

    @Inject
    private InstitutionService institutionService;

    @Inject
    private ResourceMapper resourceMapper;

    @RequestMapping(method = RequestMethod.GET, params = "type=simple")
    public List<ResourceRepresentationLocation> getInstitutions(@RequestParam(required = false) String query, @RequestParam(required = false) String[] googleIds) {
        return institutionMapper.getInstitutionRepresentations(query, googleIds);
    }

    @RequestMapping(method = RequestMethod.GET, params = "googleId")
    @ResponseBody
    public ResourceRepresentationSimple getInstitution(String googleId) {
        Institution institution = institutionService.getInstitutionByGoogleId(googleId);
        return institution == null ? null : resourceMapper.getResourceRepresentationSimple(institution);
    }

}
