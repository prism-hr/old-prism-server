package com.zuehlke.pgadmissions.rest.controller;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.advert.AdvertClosingDate;
import com.zuehlke.pgadmissions.dto.AdvertTargetDTO;
import com.zuehlke.pgadmissions.mapping.AdvertMapper;
import com.zuehlke.pgadmissions.rest.PrismRestUtils;
import com.zuehlke.pgadmissions.rest.ResourceDescriptor;
import com.zuehlke.pgadmissions.rest.dto.advert.*;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertTargetRepresentation;
import com.zuehlke.pgadmissions.services.AdvertService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    private AdvertMapper advertMapper;


    @RequestMapping(value = "/targets", method = RequestMethod.GET)
    public List<AdvertTargetRepresentation> getTargets(@PathVariable Integer resourceId, @ModelAttribute ResourceDescriptor resourceDescriptor) {
        Advert advert = advertService.getAdvert(resourceDescriptor.getResourceScope(), resourceId);
        List<AdvertTargetDTO> advertTargets = advertService.getAdvertTargets(advert);
        return advertMapper.getAdvertTargetRepresentations(advertTargets);
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
