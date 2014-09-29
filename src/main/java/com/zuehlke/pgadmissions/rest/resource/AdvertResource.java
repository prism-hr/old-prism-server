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

@RestController
public class AdvertResource {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private AdvertService advertService;

    @Autowired
    private Mapper dozerBeanMapper;

    @RequestMapping(value = "/api/opportunities", method = RequestMethod.GET, produces = "application/json")
    public List<AdvertRepresentation> getAdverts() {
        List<Advert> adverts = advertService.getActiveAdverts();
        List<AdvertRepresentation> representations = Lists.newArrayListWithExpectedSize(adverts.size());
        for (Advert advert : adverts) {
            AdvertRepresentation representation = dozerBeanMapper.map(advert, AdvertRepresentation.class);

            Resource resource = advert.getProgram() != null ? advert.getProgram() : advert.getProject();
            representation.setUser(dozerBeanMapper.map(resource.getUser(), UserRepresentation.class));
            representation.setResourceScope(resource.getResourceScope());

            representations.add(representation);
        }
        return representations;
    }

    @RequestMapping(value = "api/{resourceScope}/{resourceId}/advertDetails", method = RequestMethod.PUT)
    public void updateAdvertDetails(@ModelAttribute Class<? extends Resource> resourceClass, @PathVariable Integer resourceId,
            @Valid @RequestBody AdvertDetailsDTO advertDetailsDTO) throws Exception {
        advertService.saveAdvertDetails(resourceClass, resourceId, advertDetailsDTO);
    }

    @RequestMapping(value = "api/{resourceScope}/{resourceId}/feesAndPayments", method = RequestMethod.PUT)
    public void updateFeesAndPayments(@ModelAttribute Class<? extends Resource> resourceClass, @PathVariable Integer resourceId,
            @Valid @RequestBody AdvertFeesAndPaymentsDTO feesAndPaymentsDTO) throws Exception {
        advertService.saveFeesAndPayments(resourceClass, resourceId, feesAndPaymentsDTO);
    }
    
    @RequestMapping(value = "api/{resourceScope}/{resourceId}/filterMetadata", method = RequestMethod.PUT)
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
