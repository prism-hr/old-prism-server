package com.zuehlke.pgadmissions.rest.resource;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zuehlke.pgadmissions.rest.ResourceDescriptor;
import com.zuehlke.pgadmissions.rest.RestApiUtils;
import com.zuehlke.pgadmissions.rest.dto.AdvertCategoriesDTO;
import com.zuehlke.pgadmissions.rest.dto.AdvertClosingDateDTO;
import com.zuehlke.pgadmissions.rest.dto.AdvertDetailsDTO;
import com.zuehlke.pgadmissions.rest.dto.AdvertFeesAndPaymentsDTO;
import com.zuehlke.pgadmissions.services.AdvertService;

@RestController
@RequestMapping("api/{resourceScope:projects|programs}/{resourceId}")
public class AdvertResource {

    @Autowired
    private AdvertService advertService;

    @RequestMapping(value = "/closingDate", method = RequestMethod.PUT)
    public void updateClosingDate(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId,
            @Valid @RequestBody AdvertClosingDateDTO advertClosingDateDTO) throws Exception {
        advertService.updateAdvert(resourceDescriptor.getType(), resourceId, advertClosingDateDTO);
    }

    @RequestMapping(value = "/closingDate", method = RequestMethod.DELETE)
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
