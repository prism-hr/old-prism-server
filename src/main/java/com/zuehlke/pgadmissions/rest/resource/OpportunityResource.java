package com.zuehlke.pgadmissions.rest.resource;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.rest.dto.AdvertDetailsDTO;
import com.zuehlke.pgadmissions.rest.dto.FeesAndPaymentsDTO;
import com.zuehlke.pgadmissions.services.AdvertService;

@RestController
@RequestMapping(value = {"api/{resourceScope}"})
public class OpportunityResource {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AdvertService advertService;

    @RequestMapping(value = "/{resourceId}/advertDetails", method = RequestMethod.PUT)
    public void updateAdvertDetails(@ModelAttribute Class<? extends Resource> resourceClass, @PathVariable Integer resourceId, @Valid @RequestBody AdvertDetailsDTO advertDetailsDTO) throws Exception {
        advertService.saveAdvertDetails(resourceClass, resourceId, advertDetailsDTO);
    }

    @RequestMapping(value = "/{resourceId}/feesAndPayments", method = RequestMethod.PUT)
    public void updateFeesAndPayments(@ModelAttribute Class<? extends Resource> resourceClass, @PathVariable Integer resourceId, @Valid @RequestBody FeesAndPaymentsDTO feesAndPaymentsDTO) throws Exception {
        advertService.saveFeesAndPayments(resourceClass, resourceId, feesAndPaymentsDTO);
    }

    @ModelAttribute
    private Class<? extends Resource> getResourceClass(@PathVariable String resourceScope) {
        if ("projects".equals(resourceScope)) {
            return Project.class;
        } else if ("programs".equals(resourceScope)) {
            return Program.class;
        }
        logger.error("Unknown resource scope " + resourceScope);
        throw new ResourceNotFoundException();
    }


}
