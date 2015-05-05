package com.zuehlke.pgadmissions.rest.controller;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.resource.ResourceStudyLocation;
import com.zuehlke.pgadmissions.dto.AdvertRecommendationDTO;
import com.zuehlke.pgadmissions.rest.dto.OpportunitiesQueryDTO;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.InstitutionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.advert.AdvertRepresentation;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.utils.ToPropertyFunction;

@RestController
@RequestMapping("/api/opportunities")
@PreAuthorize("permitAll")
public class OpportunityController {
    
    @Inject
    private AdvertService advertService;

    @Inject
    private ApplicationService applicationService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private StateService stateService;

    @Inject
    private Mapper dozerBeanMapper;

    private AdvertToRepresentationFunction advertToRepresentationFunction = new AdvertToRepresentationFunction();

    @RequestMapping(method = RequestMethod.GET)
    public List<AdvertRepresentation> getAdverts(OpportunitiesQueryDTO query) {
        List<PrismState> activeInstitutionStates = stateService.getActiveInstitutionStates();
        List<PrismState> activeProgramStates = stateService.getActiveProgramStates();
        List<PrismState> activeProjectStates = stateService.getActiveProjectStates();
        List<Advert> adverts = advertService.getAdverts(query, activeInstitutionStates, activeProgramStates, activeProjectStates);
        return Lists.transform(adverts, advertToRepresentationFunction);
    }


    @RequestMapping(method = RequestMethod.GET, value = "{resourceScope:projects|programs|institutions}/{resourceId}")
    public AdvertRepresentation getAdvert(@PathVariable String resourceScope, @PathVariable Integer resourceId) {
        Advert advert = advertService.getAdvert(StringUtils.removeEnd(resourceScope, "s"), resourceId);
        return advertToRepresentationFunction.apply(advert);
    }


    @RequestMapping(method = RequestMethod.GET, value = "/{applicationId}")
    public List<AdvertRepresentation> getRecommendedAdverts(@PathVariable Integer applicationId) {
        Application application = applicationService.getById(applicationId);
        List<AdvertRecommendationDTO> advertRecommendations = advertService.getRecommendedAdverts(application.getUser());
        return Lists.transform(advertRecommendations, Functions.compose(advertToRepresentationFunction, new ToPropertyFunction<AdvertRecommendationDTO, Advert>("advert")));
    }

    private class AdvertToRepresentationFunction implements Function<Advert, AdvertRepresentation> {
        @Override
        public AdvertRepresentation apply(Advert advert) {
            AdvertRepresentation representation = dozerBeanMapper.map(advert, AdvertRepresentation.class);

            ResourceParent resource = advert.getResource();
            
            representation.setUser(dozerBeanMapper.map(resource.getUser(), UserRepresentation.class));
            representation.setResourceScope(resource.getResourceScope());
            representation.setResourceId(resource.getId());
            representation.setOpportunityType(advert.getOpportunityType());
            
            List<PrismAction> actions = resourceService.getPartnerActions(resource);
            List<PrismAction> partnerActions = Lists.newArrayListWithCapacity(actions.size());
            for (PrismAction action : actions) {
                partnerActions.add(action);
            }
            representation.setPartnerActions(partnerActions);
            
            if (resource.getResourceScope().ordinal() > INSTITUTION.ordinal()) {
                ResourceOpportunity opportunity = (ResourceOpportunity) resource;
                List<PrismStudyOption> studyOptions = resourceService.getStudyOptions(opportunity);
                List<PrismStudyOption> processedStudyOptions = Lists.newArrayListWithCapacity(studyOptions.size());
                for (PrismStudyOption studyOption : studyOptions) {
                    processedStudyOptions.add(studyOption);
                }
                representation.setStudyOptions(processedStudyOptions);
            }

            List<ResourceStudyLocation> studyLocations = resourceService.getStudyLocations(resource);
            List<String> locations = Lists.newArrayListWithCapacity(studyLocations.size());
            for (ResourceStudyLocation studyLocation : studyLocations) {
                locations.add(studyLocation.getStudyLocation());
            }
            representation.setLocations(locations);

            representation.setInstitution(dozerBeanMapper.map(resource.getInstitution(), InstitutionRepresentation.class));
            return representation;
        }
    }

}
