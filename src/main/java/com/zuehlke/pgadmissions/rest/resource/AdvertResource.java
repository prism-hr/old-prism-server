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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/{resourceScope:projects|programs}/{resourceId}")
public class AdvertResource {

    @Autowired
    private AdvertService advertService;

    @RequestMapping(value = "/closingDates", method = RequestMethod.POST)
    public Integer addClosingDate(@ModelAttribute ResourceDescriptor resourceDescriptor,
                                  @PathVariable Integer resourceId,
                                  @Valid @RequestBody AdvertClosingDateDTO advertClosingDateDTO) throws Exception {
        AdvertClosingDate closingDate = advertService.addClosingDate(resourceDescriptor.getType(), resourceId, advertClosingDateDTO);
        return closingDate.getId();
    }

    @RequestMapping(value = "/closingDates/{closingDateId}", method = RequestMethod.PUT)
    public void updateClosingDate(@ModelAttribute ResourceDescriptor resourceDescriptor,
                                  @PathVariable Integer resourceId,
                                  @PathVariable Integer closingDateId,
                                  @Valid @RequestBody AdvertClosingDateDTO advertClosingDateDTO) throws Exception {
        advertService.updateClosingDate(resourceDescriptor.getType(), resourceId, closingDateId, advertClosingDateDTO);
    }

    @RequestMapping(value = "/closingDates/{closingDateId}", method = RequestMethod.DELETE)
    public void deleteClosingDate(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId, @PathVariable Integer closingDateId)
            throws Exception {
        advertService.deleteClosingDate(resourceDescriptor.getType(), resourceId, closingDateId);
    }

    @RequestMapping(value = "/advertDetails", method = RequestMethod.PUT)
    public void updateAdvert(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId,
                             @Valid @RequestBody AdvertDetailsDTO advertDetailsDTO) throws Exception {
        advertService.updateAdvert(resourceDescriptor.getType(), resourceId, advertDetailsDTO);
    }

    @RequestMapping(value = "/feesAndPayments", method = RequestMethod.PUT)
    public void updateFeesAndPayments(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId,
                                      @Valid @RequestBody AdvertFeesAndPaymentsDTO feesAndPaymentsDTO) throws Exception {
        advertService.updateAdvert(resourceDescriptor.getType(), resourceId, feesAndPaymentsDTO);
    }

    @RequestMapping(value = "/categories", method = RequestMethod.PUT)
    public void updateCategories(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId,
                                 @Valid @RequestBody AdvertCategoriesDTO categoriesDTO) throws Exception {
        advertService.updateAdvert(resourceDescriptor.getType(), resourceId, categoriesDTO);
    }

    @ModelAttribute
    private ResourceDescriptor getResourceDescriptor(@PathVariable String resourceScope) {
        return RestApiUtils.getResourceDescriptor(resourceScope);
    }

}
