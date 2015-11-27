package uk.co.alumeni.prism.rest.controller;

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
import uk.co.alumeni.prism.domain.advert.AdvertClosingDate;
import uk.co.alumeni.prism.dto.AdvertTargetDTO;
import uk.co.alumeni.prism.mapping.AdvertMapper;
import uk.co.alumeni.prism.rest.PrismRestUtils;
import uk.co.alumeni.prism.rest.ResourceDescriptor;
import uk.co.alumeni.prism.rest.dto.advert.AdvertCategoriesDTO;
import uk.co.alumeni.prism.rest.dto.advert.AdvertClosingDateDTO;
import uk.co.alumeni.prism.rest.dto.advert.AdvertCompetenceDTO;
import uk.co.alumeni.prism.rest.dto.advert.AdvertDetailsDTO;
import uk.co.alumeni.prism.rest.dto.advert.AdvertFinancialDetailDTO;
import uk.co.alumeni.prism.rest.representation.advert.AdvertTargetRepresentation;
import uk.co.alumeni.prism.services.AdvertService;

@RestController
@RequestMapping("api/{resourceScope:projects|programs|departments|institutions}/{resourceId}")
@PreAuthorize("isAuthenticated()")
public class AdvertController {

    @Inject
    private AdvertService advertService;

    @Inject
    private AdvertMapper advertMapper;


    @RequestMapping(value = "/targets", method = RequestMethod.GET)
    public List<AdvertTargetRepresentation.AdvertTargetConnectionRepresentation> getTargets(@PathVariable Integer resourceId, @ModelAttribute ResourceDescriptor resourceDescriptor) {
        Advert advert = advertService.getAdvert(resourceDescriptor.getResourceScope(), resourceId);
        List<AdvertTargetDTO> advertTargets = advertService.getAdvertTargets(advert);
        return advertMapper.getAdvertTargetConnectionRepresentations(advertTargets);
    }

    @RequestMapping(value = "/advertDetails", method = RequestMethod.PUT)
    public void updateAdvert(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId, @Valid @RequestBody AdvertDetailsDTO advertDetailsDTO) {
        advertService.updateDetail(resourceDescriptor.getResourceScope(), resourceId, advertDetailsDTO);
    }

    @RequestMapping(value = "/financialDetails", method = RequestMethod.PUT)
    public void updateFeesAndPayments(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId,
                                      @Valid @RequestBody AdvertFinancialDetailDTO financialDetailDTO) {
        advertService.updateFinancialDetails(resourceDescriptor.getResourceScope(), resourceId, financialDetailDTO);
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

    @RequestMapping(value = "/closingDates", method = RequestMethod.POST)
    public Integer addClosingDate(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId,
                                  @Valid @RequestBody AdvertClosingDateDTO advertClosingDateDTO) {
        AdvertClosingDate closingDate = advertService.createClosingDate(resourceDescriptor.getResourceScope(), resourceId, advertClosingDateDTO);
        return closingDate.getId();
    }

    @RequestMapping(value = "/closingDates/{closingDateId}", method = RequestMethod.DELETE)
    public void deleteClosingDate(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId, @PathVariable Integer closingDateId) {
        advertService.deleteClosingDate(resourceDescriptor.getResourceScope(), resourceId, closingDateId);
    }

    @ModelAttribute
    private ResourceDescriptor getResourceDescriptor(@PathVariable String resourceScope) {
        return PrismRestUtils.getResourceDescriptor(resourceScope);
    }

}
