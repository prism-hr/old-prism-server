package com.zuehlke.pgadmissions.rest.resource;

import com.zuehlke.pgadmissions.rest.ResourceDescriptor;
import com.zuehlke.pgadmissions.rest.RestApiUtils;
import com.zuehlke.pgadmissions.rest.dto.AdvertDetailsDTO;
import com.zuehlke.pgadmissions.rest.dto.AdvertFeesAndPaymentsDTO;
import com.zuehlke.pgadmissions.rest.dto.AdvertFilterMetadataDTO;
import com.zuehlke.pgadmissions.services.AdvertService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController("api/{resourceScope:projects|programs}")
public class AdvertResource {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AdvertService advertService;

    @RequestMapping(value = "/{resourceId}/advertDetails", method = RequestMethod.PUT)
    public void updateAdvertDetails(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId,
                                    @Valid @RequestBody AdvertDetailsDTO advertDetailsDTO) throws Exception {
        advertService.saveAdvertDetails(resourceDescriptor.getType(), resourceId, advertDetailsDTO);
    }

    @RequestMapping(value = "/{resourceId}/feesAndPayments", method = RequestMethod.PUT)
    public void updateFeesAndPayments(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId,
                                      @Valid @RequestBody AdvertFeesAndPaymentsDTO feesAndPaymentsDTO) throws Exception {
        advertService.saveFeesAndPayments(resourceDescriptor.getType(), resourceId, feesAndPaymentsDTO);
    }

    @RequestMapping(value = "/{resourceId}/filterMetadata", method = RequestMethod.PUT)
    public void updateFilterMetadata(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId,
                                     @Valid @RequestBody AdvertFilterMetadataDTO metadataDTO) throws Exception {
        advertService.saveFilterMetadata(resourceDescriptor.getType(), resourceId, metadataDTO);
    }


    @ModelAttribute
    private ResourceDescriptor getResourceDescriptor(@PathVariable String resourceScope) {
        return RestApiUtils.getResourceDescriptor(resourceScope);
    }
}
