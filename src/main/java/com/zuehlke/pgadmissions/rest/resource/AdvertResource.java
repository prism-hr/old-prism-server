package com.zuehlke.pgadmissions.rest.resource;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.rest.ResourceDescriptor;
import com.zuehlke.pgadmissions.rest.RestApiUtils;
import com.zuehlke.pgadmissions.rest.dto.AdvertCategoriesDTO;
import com.zuehlke.pgadmissions.rest.dto.AdvertDetailsDTO;
import com.zuehlke.pgadmissions.rest.dto.AdvertFeesAndPaymentsDTO;
import com.zuehlke.pgadmissions.rest.dto.AdvertFilterMetadataDTO;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.ResourceService;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/{resourceScope:projects|programs}/{resourceId}")
public class AdvertResource {

    @Autowired
    private AdvertService advertService;

    @Autowired
    private ResourceService resourceService;

    @RequestMapping(value = "/advertDetails", method = RequestMethod.PUT)
    public void updateAdvertDetails(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId,
                                    @Valid @RequestBody AdvertDetailsDTO advertDetailsDTO) throws Exception {
        advertService.saveAdvertDetails(resourceDescriptor.getType(), resourceId, advertDetailsDTO);
    }

    @RequestMapping(value = "/feesAndPayments", method = RequestMethod.PUT)
    public void updateFeesAndPayments(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId,
                                      @Valid @RequestBody AdvertFeesAndPaymentsDTO feesAndPaymentsDTO) throws Exception {
        advertService.saveFeesAndPayments(resourceDescriptor.getType(), resourceId, feesAndPaymentsDTO);
    }

    @RequestMapping(value = "/categories", method = RequestMethod.PUT)
    public void updateCategories(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId,
                                     @Valid @RequestBody AdvertCategoriesDTO categoriesDTO) throws Exception {
        advertService.saveCategories(resourceDescriptor.getType(), resourceId, categoriesDTO);
    }

    @RequestMapping(value = "/filterMetadata", method = RequestMethod.PUT)
    public void updateFilterMetadata(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId,
                                     @Valid @RequestBody AdvertFilterMetadataDTO metadataDTO) throws Exception {
        advertService.saveFilterMetadata(resourceDescriptor.getType(), resourceId, metadataDTO);
    }

    @ModelAttribute
    private ResourceDescriptor getResourceDescriptor(@PathVariable String resourceScope) {
        return RestApiUtils.getResourceDescriptor(resourceScope);
    }

}
