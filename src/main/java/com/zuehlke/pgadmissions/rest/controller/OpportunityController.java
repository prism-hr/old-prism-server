package com.zuehlke.pgadmissions.rest.controller;

import java.util.List;
import java.util.Set;

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
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.resource.ResourceCondition;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.resource.ResourceStudyLocation;
import com.zuehlke.pgadmissions.domain.resource.ResourceStudyOption;
import com.zuehlke.pgadmissions.dto.AdvertRecommendationDTO;
import com.zuehlke.pgadmissions.rest.dto.OpportunitiesQueryDTO;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.InstitutionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceConditionRepresentation;
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
    public List<AdvertRepresentation> getRecommendedAdverts(Integer applicationId) {
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

            List<ResourceStudyOption> studyOptions = resourceService.getStudyOptions(resource);
            List<PrismStudyOption> options = Lists.newArrayListWithCapacity(studyOptions.size());
            for (ResourceStudyOption studyOption : studyOptions) {
                options.add(studyOption.getStudyOption().getPrismStudyOption());
            }
            representation.setStudyOptions(options);

            List<ResourceStudyLocation> studyLocations = resourceService.getStudyLocations(resource);
            List<String> locations = Lists.newArrayListWithCapacity(studyLocations.size());
            for (ResourceStudyLocation studyLocation : studyLocations) {
                locations.add(studyLocation.getStudyLocation());
            }
            representation.setLocations(locations);

            Set<ResourceCondition> resourceConditions = resource.getResourceConditions();
            List<ResourceConditionRepresentation> resourceConditionRepresentations = Lists.newArrayListWithCapacity(studyLocations.size());
            for (ResourceCondition resourceCondition : resourceConditions) {
                resourceConditionRepresentations.add(dozerBeanMapper.map(resourceCondition, ResourceConditionRepresentation.class));
            }
            representation.setResourceConditions(resourceConditionRepresentations);

            representation.setInstitution(dozerBeanMapper.map(resource.getInstitution(), InstitutionRepresentation.class));
            return representation;
        }
    }

}
