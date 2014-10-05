package com.zuehlke.pgadmissions.rest.resource;

import java.util.List;

import javax.validation.Valid;

import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.rest.dto.AdvertDetailsDTO;
import com.zuehlke.pgadmissions.rest.dto.AdvertFeesAndPaymentsDTO;
import com.zuehlke.pgadmissions.rest.dto.AdvertFilterMetadataDTO;
import com.zuehlke.pgadmissions.rest.representation.AdvertRepresentation;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import com.zuehlke.pgadmissions.services.AdvertService;

@RestController("api/{resourceScope:projects|programs}")
public class AdvertResource {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AdvertService advertService;

    @RequestMapping(value = "/{resourceId}/advertDetails", method = RequestMethod.PUT)
    public void updateAdvertDetails(@ModelAttribute Class<? extends Resource> resourceClass, @PathVariable Integer resourceId,
            @Valid @RequestBody AdvertDetailsDTO advertDetailsDTO) throws Exception {
        advertService.saveAdvertDetails(resourceClass, resourceId, advertDetailsDTO);
    }

    @RequestMapping(value = "/{resourceId}/feesAndPayments", method = RequestMethod.PUT)
    public void updateFeesAndPayments(@ModelAttribute Class<? extends Resource> resourceClass, @PathVariable Integer resourceId,
            @Valid @RequestBody AdvertFeesAndPaymentsDTO feesAndPaymentsDTO) throws Exception {
        advertService.saveFeesAndPayments(resourceClass, resourceId, feesAndPaymentsDTO);
    }

    @RequestMapping(value = "/{resourceId}/filterMetadata", method = RequestMethod.PUT)
    public void updateFilterMetadata(@ModelAttribute Class<? extends Resource> resourceClass, @PathVariable Integer resourceId,
            @Valid @RequestBody AdvertFilterMetadataDTO metadataDTO) throws Exception {
        advertService.saveFilterMetadata(resourceClass, resourceId, metadataDTO);
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
