package com.zuehlke.pgadmissions.rest.resource;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.rest.domain.OpportunityRepresentation;
import com.zuehlke.pgadmissions.rest.domain.UserRepresentation;
import com.zuehlke.pgadmissions.security.TokenUtils;
import com.zuehlke.pgadmissions.services.AdvertService;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.ws.rs.WebApplicationException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/opportunities")
public class OpportunityResource {

    @Autowired
    private AdvertService advertService;

    @Autowired
    private DozerBeanMapper dozerBeanMapper;

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public List<OpportunityRepresentation> getOpportunities() {
        List<Advert> adverts = advertService.getAdverts();
        List<OpportunityRepresentation> representations = Lists.newArrayListWithExpectedSize(adverts.size());
        for (Advert advert : adverts) {
            representations.add(dozerBeanMapper.map(advert, OpportunityRepresentation.class));
        }
        return representations;
    }

}
