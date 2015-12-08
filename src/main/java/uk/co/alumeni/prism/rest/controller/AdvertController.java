package uk.co.alumeni.prism.rest.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.dto.AdvertTargetDTO;
import uk.co.alumeni.prism.mapping.AdvertMapper;
import uk.co.alumeni.prism.rest.PrismRestUtils;
import uk.co.alumeni.prism.rest.ResourceDescriptor;
import uk.co.alumeni.prism.rest.dto.AddressDTO;
import uk.co.alumeni.prism.rest.dto.advert.AdvertApplicationOptionsDTO;
import uk.co.alumeni.prism.rest.dto.advert.AdvertCategoriesDTO;
import uk.co.alumeni.prism.rest.dto.advert.AdvertCompetenceDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceParentDTO;
import uk.co.alumeni.prism.rest.representation.advert.AdvertTargetRepresentation.AdvertTargetConnectionRepresentation;
import uk.co.alumeni.prism.services.AdvertService;
import uk.co.alumeni.prism.services.ResourceService;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/{resourceScope:projects|programs|departments|institutions}/{resourceId}")
@PreAuthorize("isAuthenticated()")
public class AdvertController {

    @Inject
    private AdvertService advertService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private AdvertMapper advertMapper;

    @RequestMapping(value = "/targets", method = RequestMethod.GET)
    public List<AdvertTargetConnectionRepresentation> getTargets(
            @PathVariable Integer resourceId, @ModelAttribute ResourceDescriptor resourceDescriptor) {
        Advert advert = advertService.getAdvert(resourceDescriptor.getResourceScope(), resourceId);
        List<AdvertTargetDTO> advertTargets = advertService.getAdvertTargets(advert);
        return advertMapper.getAdvertTargetConnectionRepresentations(advertTargets);
    }

    @RequestMapping(value = "/details", method = RequestMethod.PUT)
    public void updateResourceDetails(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId, @Valid @RequestBody ResourceParentDTO resourceDTO) {
        advertService.updateResourceDetails(resourceDescriptor.getResourceScope(), resourceId, resourceDTO);
    }

    @RequestMapping(value = "/applicationOptions", method = RequestMethod.PUT)
    public void updateApplicationOptions(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId, @Valid @RequestBody AdvertApplicationOptionsDTO applicationOptionsDTO) {
        advertService.updateApplicationOptions(resourceDescriptor.getResourceScope(), resourceId, applicationOptionsDTO);
    }

    @RequestMapping(value = "/address", method = RequestMethod.PUT)
    public void updateAddress(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId, @Valid @RequestBody AddressDTO addressDTO) {
        Resource resource = resourceService.getById(resourceDescriptor.getResourceScope(), resourceId);
        advertService.updateAddress(resource.getParentResource(), resource.getAdvert(), addressDTO);
    }

    @RequestMapping(value = "/categories", method = RequestMethod.PUT)
    public void updateCategories(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId, @Valid @RequestBody AdvertCategoriesDTO categoriesDTO) {
        advertService.updateCategories(resourceDescriptor.getResourceScope(), resourceId, categoriesDTO);
    }

    @RequestMapping(value = "/competences", method = RequestMethod.PUT)
    public void updateCompetences(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId,
                                  @Valid @RequestBody List<AdvertCompetenceDTO> competencesDTO) {
        advertService.updateCompetences(resourceDescriptor.getResourceScope(), resourceId, competencesDTO);
    }

    @ModelAttribute
    private ResourceDescriptor getResourceDescriptor(@PathVariable String resourceScope) {
        return PrismRestUtils.getResourceDescriptor(resourceScope);
    }

}
