package uk.co.alumeni.prism.rest.controller;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.advert.AdvertCategories;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.resource.ResourceOpportunity;
import uk.co.alumeni.prism.dto.AdvertTargetDTO;
import uk.co.alumeni.prism.mapping.AdvertMapper;
import uk.co.alumeni.prism.rest.PrismRestUtils;
import uk.co.alumeni.prism.rest.ResourceDescriptor;
import uk.co.alumeni.prism.rest.dto.AddressDTO;
import uk.co.alumeni.prism.rest.dto.advert.AdvertCategoriesDTO;
import uk.co.alumeni.prism.rest.dto.advert.AdvertCompetenceDTO;
import uk.co.alumeni.prism.rest.dto.advert.AdvertSettingsDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceParentDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceRelationDTO;
import uk.co.alumeni.prism.rest.representation.advert.AdvertTargetRepresentation.AdvertTargetConnectionRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceLocationRepresentationRelation;
import uk.co.alumeni.prism.services.AdvertService;
import uk.co.alumeni.prism.services.ResourceService;
import uk.co.alumeni.prism.services.UserService;

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

    @Inject
    private UserService userService;

    @RequestMapping(value = "/targets", method = RequestMethod.GET)
    public List<AdvertTargetConnectionRepresentation> getTargets(
            @PathVariable Integer resourceId, @ModelAttribute ResourceDescriptor resourceDescriptor) {
        Advert advert = advertService.getAdvert(resourceDescriptor.getResourceScope(), resourceId);
        List<AdvertTargetDTO> advertTargets = advertService.getAdvertTargets(advert);
        return advertMapper.getAdvertTargetConnectionRepresentations(advertTargets, userService.getCurrentUser());
    }

    @RequestMapping(value = "/locations", method = RequestMethod.GET)
    public List<ResourceLocationRepresentationRelation> getLocations(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId) {
        Advert advert = advertService.getAdvert(resourceDescriptor.getResourceScope(), resourceId);
        AdvertCategories categories = advertService.getAdvertCategories(advert);
        if (categories != null) {
            return advertMapper.getAllAdvertLocationRepresentations(advert, categories);
        }
        return Collections.emptyList();
    }

    @RequestMapping(value = "/details", method = RequestMethod.PUT)
    public void updateResourceDetails(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId,
            @Valid @RequestBody ResourceParentDTO resourceDTO) {
        advertService.updateResourceDetails(resourceDescriptor.getResourceScope(), resourceId, resourceDTO);
    }

    @RequestMapping(value = "/settings", method = RequestMethod.PUT)
    public void updateAdvertSettings(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId,
            @Valid @RequestBody AdvertSettingsDTO advertSettingsDTO) {
        advertService.updateAdvertSettings(resourceDescriptor.getResourceScope(), resourceId, advertSettingsDTO);
    }

    @RequestMapping(value = "/address", method = RequestMethod.PUT)
    public void updateAddress(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId, @Valid @RequestBody AddressDTO addressDTO) {
        Resource resource = resourceService.getById(resourceDescriptor.getResourceScope(), resourceId);
        advertService.updateAddress(resource.getParentResource(), resource.getAdvert(), addressDTO);
    }

    @RequestMapping(value = "/locations", method = RequestMethod.PUT)
    public void updateLocations(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId,
            @Valid @RequestBody List<ResourceRelationDTO> locations) {
        ResourceOpportunity resource = (ResourceOpportunity) resourceService.getById(resourceDescriptor.getResourceScope(), resourceId);
        advertService.updateLocations(resource, locations);
    }

    @RequestMapping(value = "/categories", method = RequestMethod.PUT)
    public void updateCategories(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId,
            @Valid @RequestBody AdvertCategoriesDTO categoriesDTO) {
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
