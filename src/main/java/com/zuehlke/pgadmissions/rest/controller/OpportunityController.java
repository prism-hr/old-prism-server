package com.zuehlke.pgadmissions.rest.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.mapping.AdvertMapper;
import com.zuehlke.pgadmissions.rest.dto.OpportunitiesQueryDTO;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertRepresentationExtended;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.StateService;

@RestController
@RequestMapping("/api/opportunities")
@PreAuthorize("permitAll")
public class OpportunityController {

    @Inject
    private AdvertService advertService;

    @Inject
    private AdvertMapper advertMapper;

    @Inject
    private ApplicationService applicationService;

    @Inject
    private StateService stateService;

    @RequestMapping(method = RequestMethod.GET)
    public List<AdvertRepresentationExtended> getAdverts(OpportunitiesQueryDTO query) {
        List<PrismState> activeProgramStates = stateService.getActiveProgramStates();
        List<PrismState> activeProjectStates = stateService.getActiveProjectStates();
        List<Advert> adverts = advertService.getAdverts(query, activeProgramStates, activeProjectStates);
        return adverts.stream().map(advertMapper::getAdvertRepresentationExtended).collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.GET, value = "{resourceScope:projects|programs|institutions}/{resourceId}")
    public AdvertRepresentationExtended getAdvert(@PathVariable String resourceScope, @PathVariable Integer resourceId) {
        Advert advert = advertService.getAdvert(PrismScope.valueOf(StringUtils.removeEnd(resourceScope, "s").toUpperCase()), resourceId);
        if (advert == null) {
            throw new ResourceNotFoundException("Advert not found");
        }
        return advertMapper.getAdvertRepresentationExtended(advert);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{applicationId}")
    public List<AdvertRepresentationExtended> getRecommendedAdverts(@PathVariable Integer applicationId) {
        Application application = applicationService.getById(applicationId);
        return advertMapper.getRecommendedAdvertRepresentations(application);
    }

}
