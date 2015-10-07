package com.zuehlke.pgadmissions.mapping;

import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType.getOpportunityTypes;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.rest.representation.AgeRangeRepresentation;
import com.zuehlke.pgadmissions.rest.representation.DomicileRepresentation;
import com.zuehlke.pgadmissions.rest.representation.OpportunityCategoryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.OpportunityCategoryRepresentation.OpportunityTypeRepresentation;
import com.zuehlke.pgadmissions.services.PrismService;

@Service
@Transactional
public class PrismMapper {

    @Inject
    private PrismService prismService;

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
        return prismService.getDomiciles().stream().map(d -> new DomicileRepresentation(d.getId(), d.getCurrency())).collect(toList());
    }

}
