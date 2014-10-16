package com.zuehlke.pgadmissions.rest.resource;

import java.util.List;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.rest.representation.AdvertRepresentation;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import com.zuehlke.pgadmissions.services.AdvertService;

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
