package com.zuehlke.pgadmissions.rest.controller;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.definitions.PrismMotivationContext;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.exceptions.PrismForbiddenException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.mapping.AdvertMapper;
import com.zuehlke.pgadmissions.rest.dto.OpportunitiesQueryDTO;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertListRepresentation;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertRepresentationExtended;
import com.zuehlke.pgadmissions.services.AdvertService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
@RequestMapping("/api/opportunities")
@PreAuthorize("permitAll")
public class OpportunityController {

    @Inject
    private AdvertService advertService;

    @Inject
    private AdvertMapper advertMapper;

    @RequestMapping(method = RequestMethod.GET)
    public AdvertListRepresentation getAdverts(OpportunitiesQueryDTO query) {
        if(query.getContext() == PrismMotivationContext.UNIVERSITY && query.getContextScope() == PrismScope.INSTITUTION && query.getResourceId() != null) {
            throw new PrismForbiddenException("Cannot see university partners");
        }
        AdvertListRepresentation representation = advertMapper.getAdvertExtendedRepresentations(query);
        return representation;
    }

    @RequestMapping(method = RequestMethod.GET, value = "{resourceScope:projects|programs|departments|institutions}/{resourceId}")
    public AdvertRepresentationExtended getAdvert(@PathVariable String resourceScope, @PathVariable Integer resourceId) {
        Advert advert = advertService.getAdvert(PrismScope.valueOf(StringUtils.removeEnd(resourceScope, "s").toUpperCase()), resourceId);
        if (advert == null) {
            throw new ResourceNotFoundException("Advert not found");
        }
        return advertMapper.getAdvertRepresentationExtended(advert);
    }

}
