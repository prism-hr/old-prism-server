package com.zuehlke.pgadmissions.mapping;

import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType.getOpportunityTypes;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableMap;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertFunction;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertIndustry;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.rest.representation.AgeRangeRepresentation;
import com.zuehlke.pgadmissions.rest.representation.DomicileRepresentation;
import com.zuehlke.pgadmissions.rest.representation.OpportunityCategoryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.OpportunityCategoryRepresentation.OpportunityTypeRepresentation;
import com.zuehlke.pgadmissions.services.PrismService;
import com.zuehlke.pgadmissions.services.SystemService;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

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
                .map(oc -> new OpportunityCategoryRepresentation(oc, oc.isPublished(), oc.isDefaultPermanent(), oc.isPermittedOnCourse(),
                        getOpportunityTypes(oc)
                                .stream()
                                .map(ot -> new OpportunityTypeRepresentation(ot, ot.isPublished(), ot.getDescription()))
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

    public List<ImmutableMap<String, ? extends Object>> getAdvertFunctionRepresentations() {
        PropertyLoader propertyLoader = applicationContext.getBean(PropertyLoader.class).localizeLazy(systemService.getSystem());
        return Stream.of(PrismAdvertFunction.values())
                .map(function -> ImmutableMap.of("id", function, "name", propertyLoader.loadEager(PrismDisplayPropertyDefinition.valueOf("SYSTEM_ADVERT_FUNCTION_" + function))))
                .collect(toList());
    }

    public List<ImmutableMap<String, ? extends Object>> getAdvertIndustryRepresentations() {
        PropertyLoader propertyLoader = applicationContext.getBean(PropertyLoader.class).localizeLazy(systemService.getSystem());
        return Stream.of(PrismAdvertIndustry.values())
                .map(industry -> ImmutableMap.of("id", industry, "name", propertyLoader.loadEager(PrismDisplayPropertyDefinition.valueOf("SYSTEM_ADVERT_INDUSTRY_" + industry))))
                .collect(toList());
    }

}
