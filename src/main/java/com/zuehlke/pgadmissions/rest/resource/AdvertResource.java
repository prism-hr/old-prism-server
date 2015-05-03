package com.zuehlke.pgadmissions.rest.resource;

import com.zuehlke.pgadmissions.domain.advert.AdvertClosingDate;
import com.zuehlke.pgadmissions.rest.ResourceDescriptor;
import com.zuehlke.pgadmissions.rest.RestApiUtils;
import com.zuehlke.pgadmissions.rest.dto.AdvertCategoriesDTO;
import com.zuehlke.pgadmissions.rest.dto.AdvertClosingDateDTO;
import com.zuehlke.pgadmissions.rest.dto.AdvertDetailsDTO;
import com.zuehlke.pgadmissions.rest.dto.AdvertFeesAndPaymentsDTO;
import com.zuehlke.pgadmissions.services.AdvertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/{resourceScope:projects|programs|institutions}/{resourceId}")
@PreAuthorize("isAuthenticated()")
public class AdvertResource {

    @Autowired
    private AdvertService advertService;

    @RequestMapping(value = "/advertDetails", method = RequestMethod.PUT)
    public void updateAdvert(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId,
            @Valid @RequestBody AdvertDetailsDTO advertDetailsDTO) throws Exception {
        advertService.updateDetail(resourceDescriptor.getResourceScope(), resourceId, advertDetailsDTO);
    }

    @RequestMapping(value = "/feesAndPayments", method = RequestMethod.PUT)
    public void updateFeesAndPayments(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId,
            @Valid @RequestBody AdvertFeesAndPaymentsDTO feesAndPaymentsDTO) throws Exception {
        advertService.updateFeesAndPayments(resourceDescriptor.getResourceScope(), resourceId, feesAndPaymentsDTO);
    }

    @RequestMapping(value = "/categories", method = RequestMethod.PUT)
    public void updateCategories(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId,
            @Valid @RequestBody AdvertCategoriesDTO categoriesDTO) throws Exception {
        advertService.updateCategories(resourceDescriptor.getResourceScope(), resourceId, categoriesDTO);
    }

    @RequestMapping(value = "/closingDates", method = RequestMethod.POST)
    public Integer addClosingDate(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId,
            @Valid @RequestBody AdvertClosingDateDTO advertClosingDateDTO) throws Exception {
        AdvertClosingDate closingDate = advertService.createClosingDate(resourceDescriptor.getResourceScope(), resourceId, advertClosingDateDTO);
        return closingDate.getId();
    }

    @RequestMapping(value = "/closingDates/{closingDateId}", method = RequestMethod.PUT)
    public void updateClosingDate(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId, @PathVariable Integer closingDateId,
            @Valid @RequestBody AdvertClosingDateDTO advertClosingDateDTO) throws Exception {
        advertService.updateClosingDate(resourceDescriptor.getResourceScope(), resourceId, closingDateId, advertClosingDateDTO);
    }

    @RequestMapping(value = "/closingDates/{closingDateId}", method = RequestMethod.DELETE)
    public void deleteClosingDate(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId, @PathVariable Integer closingDateId)
            throws Exception {
        advertService.deleteClosingDate(resourceDescriptor.getResourceScope(), resourceId, closingDateId);
    }

    @ModelAttribute
    private ResourceDescriptor getResourceDescriptor(@PathVariable String resourceScope) {
        return RestApiUtils.getResourceDescriptor(resourceScope);
    }

}
