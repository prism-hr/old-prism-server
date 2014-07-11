package com.zuehlke.pgadmissions.rest.resource;

import java.util.List;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.domain.OpportunityRepresentation;
import com.zuehlke.pgadmissions.services.AdvertService;

@RestController
@RequestMapping("/api/opportunities")
public class OpportunityResource {

    @Autowired
    private AdvertService advertService;

    @Autowired
    private Mapper dozerBeanMapper;

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public List<OpportunityRepresentation> getOpportunities() {
        List<Advert> adverts = advertService.getAdverts();
        List<OpportunityRepresentation> representations = Lists.newArrayListWithExpectedSize(adverts.size());
        for (Advert advert : adverts) {
            OpportunityRepresentation representation = dozerBeanMapper.map(advert, OpportunityRepresentation.class);
            representation.setResourceType(advert.getProject() == null ? PrismScope.PROGRAM : PrismScope.PROJECT);
            representations.add(representation);
        }
        return representations;
    }

}
