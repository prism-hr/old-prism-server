package com.zuehlke.pgadmissions.services.helpers;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;

import java.util.List;

import javax.inject.Inject;

import org.dozer.Mapper;
import org.springframework.stereotype.Component;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.resource.ResourceStudyLocation;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.InstitutionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.advert.AdvertRepresentation;
import com.zuehlke.pgadmissions.services.ResourceService;

@Component
public class AdvertToRepresentationFunction implements Function<Advert, AdvertRepresentation> {

    @Inject
    private ResourceService resourceService;

    @Inject
    private Mapper mapper;

    @Override
    public AdvertRepresentation apply(Advert advert) {
        AdvertRepresentation representation = mapper.map(advert, AdvertRepresentation.class);
        ResourceParent resource = advert.getResource();

        representation.setBackgroundImage(resourceService.getBackgroundImage(resource));
        representation.setUser(mapper.map(resource.getUser(), UserRepresentation.class));
        representation.setResourceScope(resource.getResourceScope());
        representation.setResourceId(resource.getId());
        representation.setOpportunityType(advert.getOpportunityType());

        List<PrismAction> actions = resourceService.getPartnerActions(resource);
        List<PrismAction> partnerActions = Lists.newArrayListWithCapacity(actions.size());
        for (PrismAction action : actions) {
            partnerActions.add(action);
        }
        representation.setPartnerActions(partnerActions);

        representation.setSponsorCount(resourceService.getResourceSponsorCount(resource));
        representation.setTopTenSponsors(resourceService.getResourceTopTenSponsors(resource));

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

        Institution institution = resource.getInstitution();
        representation.setInstitution(mapper.map(institution, InstitutionRepresentation.class));

        Institution partner = resource.getPartner();
        if (partner != null && !partner.sameAs(institution)) {
            representation.setPartner(mapper.map(resource.getPartner(), InstitutionRepresentation.class));
        }

        return representation;
    }

}
