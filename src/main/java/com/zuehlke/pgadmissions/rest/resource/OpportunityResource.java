package com.zuehlke.pgadmissions.rest.resource;

import java.util.List;
import java.util.Set;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.program.ResourceLocation;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceStudyOption;
import com.zuehlke.pgadmissions.dto.AdvertRecommendationDTO;
import com.zuehlke.pgadmissions.rest.dto.OpportunitiesQueryDTO;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.InstitutionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.advert.AdvertRepresentation;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.StateService;

@RestController
@RequestMapping("/api/opportunities")
@PreAuthorize("permitAll")
public class OpportunityResource {

    @Autowired
    private AdvertService advertService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private StateService stateService;

    @Autowired
    private Mapper dozerBeanMapper;

    @RequestMapping(method = RequestMethod.GET)
    public List<AdvertRepresentation> getAdverts(OpportunitiesQueryDTO query) {
        List<PrismState> activeInstitutionStates = stateService.getActiveInstitutionStates();
        List<PrismState> activeProgramStates = stateService.getActiveProgramStates();
        List<PrismState> activeProjectStates = stateService.getActiveProjectStates();
        List<Advert> adverts = advertService.getAdverts(query, activeInstitutionStates, activeProgramStates, activeProjectStates);

        List<AdvertRepresentation> representations = Lists.newArrayListWithExpectedSize(adverts.size());
        for (Advert advert : adverts) {
            boolean acceptingApplications = advertService.getAcceptingApplications(activeProgramStates, activeProjectStates, advert);
            AdvertRepresentation representation = getAdvertRepresentation(advert, acceptingApplications);
            representations.add(representation);
        }

        return representations;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{applicationId}")
    public List<AdvertRepresentation> getRecommendedAdverts(Integer applicationId) {
        Application application = applicationService.getById(applicationId);
        List<AdvertRecommendationDTO> advertRecommendations = advertService.getRecommendedAdverts(application.getUser());

        List<AdvertRepresentation> representations = Lists.newArrayListWithExpectedSize(advertRecommendations.size());
        for (AdvertRecommendationDTO advertRecommendation : advertRecommendations) {
            AdvertRepresentation representation = getAdvertRepresentation(advertRecommendation.getAdvert(), true);
            representations.add(representation);
        }

        return representations;
    }

    private AdvertRepresentation getAdvertRepresentation(Advert advert, boolean acceptingApplications) {
        AdvertRepresentation representation = dozerBeanMapper.map(advert, AdvertRepresentation.class);
        representation.setAcceptingApplication(acceptingApplications);

        Resource resource = advert.getProgram() != null ? advert.getProgram() : advert.getProject();
        representation.setUser(dozerBeanMapper.map(resource.getUser(), UserRepresentation.class));
        representation.setResourceScope(resource.getResourceScope());
        representation.setResourceId(resource.getId());
        representation.setOpportunityType(advert.getOpportunityType());

        List<String> locations = Lists.newArrayListWithCapacity(resource.getProgram().getLocations().size());
        for (ResourceLocation programLocation : resource.getProgram().getLocations()) {
            locations.add(programLocation.getLocation());
        }
        representation.setLocations(locations);

        Set<PrismStudyOption> studyOptions = Sets.newHashSet();
        for (ResourceStudyOption studyOption : resource.getProgram().getStudyOptions()) {
            studyOptions.add(studyOption.getStudyOption().getPrismStudyOption());
        }
        representation.setStudyOptions(studyOptions);
        representation.setInstitution(dozerBeanMapper.map(resource.getInstitution(), InstitutionRepresentation.class));
        return representation;
    }

}
