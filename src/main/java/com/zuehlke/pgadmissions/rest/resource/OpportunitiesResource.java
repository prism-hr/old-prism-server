package com.zuehlke.pgadmissions.rest.resource;

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
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/opportunities")
public class OpportunitiesResource {

    @Autowired
    private AdvertService advertService;

    @Autowired
    private Mapper dozerBeanMapper;

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
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

}
