package com.zuehlke.pgadmissions.rest.controller;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.rest.dto.OpportunitiesQueryDTO;
import com.zuehlke.pgadmissions.rest.representation.resource.advert.AdvertRepresentation;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.services.helpers.AdvertToRepresentationFunction;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

@RestController
@RequestMapping("/api/opportunities")
@PreAuthorize("permitAll")
public class OpportunityController {

    @Inject
    private AdvertService advertService;

    @Inject
    private StateService stateService;

    @Inject
    private AdvertToRepresentationFunction advertToRepresentationFunction;

    @RequestMapping(method = RequestMethod.GET)
    public List<AdvertRepresentation> getAdverts(OpportunitiesQueryDTO query) {
        List<PrismState> activeProgramStates = stateService.getActiveProgramStates();
        List<PrismState> activeProjectStates = stateService.getActiveProjectStates();
        List<Advert> adverts = advertService.getAdverts(query, activeProgramStates, activeProjectStates);
        return Lists.transform(adverts, advertToRepresentationFunction);
    }

    @RequestMapping(method = RequestMethod.GET, value = "{resourceScope:projects|programs|institutions}/{resourceId}")
    public AdvertRepresentation getAdvert(@PathVariable String resourceScope, @PathVariable Integer resourceId) {
        Advert advert = advertService.getAdvert(StringUtils.removeEnd(resourceScope, "s"), resourceId);
        if(advert == null) {
            throw new ResourceNotFoundException("Advert not found");
        }
        return advertToRepresentationFunction.apply(advert);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{applicationId}")
    public List<AdvertRepresentation> getRecommendedAdverts(@PathVariable Integer applicationId) {
        return advertService.getRecommentedAdverts(applicationId);
    }

}
