package com.zuehlke.pgadmissions.mapping;

import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.rest.representation.AgeRangeRepresentation;
import com.zuehlke.pgadmissions.rest.representation.DomicileRepresentation;
import com.zuehlke.pgadmissions.rest.representation.OpportunityCategoryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.OpportunityCategoryRepresentation.OpportunityTypeRepresentation;
import com.zuehlke.pgadmissions.services.PrismService;
import com.zuehlke.pgadmissions.services.SystemService;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType.getOpportunityTypes;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class PrismMapper {

    @Inject
    private PrismService prismService;

    @Inject
    private ApplicationContext applicationContext;

    @Inject
    private SystemService systemService;

    public List<OpportunityCategoryRepresentation> getOpportunityTypeRepresentations() {
        return asList(PrismOpportunityCategory.values()).stream()
                .map(oc -> new OpportunityCategoryRepresentation(oc, oc.isPublished(),
                        getOpportunityTypes(oc)
                                .stream()
                                .map(ot -> new OpportunityTypeRepresentation(ot, ot.isPublished(), ot.isRequireEndorsement(), ot.getTermsAndConditions()))
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    public List<AgeRangeRepresentation> getAgeRangeRepresentations() {
        return prismService.getAgeRanges().stream().map(a -> new AgeRangeRepresentation(a.getId(), a.getLowerBound(), a.getUpperBound())).collect(toList());
    }

    public List<DomicileRepresentation> getDomicileRepresentations() {
        PropertyLoader propertyLoader = applicationContext.getBean(PropertyLoader.class).localizeLazy(systemService.getSystem());
        return prismService.getDomiciles().stream()
                .map(d -> new DomicileRepresentation(d.getId(), propertyLoader.loadEager(PrismDisplayPropertyDefinition.valueOf("SYSTEM_DOMICILE_" + d.getId())), d.getCurrency()))
                .collect(toList());
    }

}
