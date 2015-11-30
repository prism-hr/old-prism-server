package uk.co.alumeni.prism.mapping;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static uk.co.alumeni.prism.domain.definitions.PrismOpportunityType.getOpportunityTypes;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableMap;

import uk.co.alumeni.prism.domain.definitions.PrismAdvertFunction;
import uk.co.alumeni.prism.domain.definitions.PrismAdvertIndustry;
import uk.co.alumeni.prism.domain.definitions.PrismDisability;
import uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition;
import uk.co.alumeni.prism.domain.definitions.PrismEthnicity;
import uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory;
import uk.co.alumeni.prism.rest.representation.AgeRangeRepresentation;
import uk.co.alumeni.prism.rest.representation.DisabilityRepresentation;
import uk.co.alumeni.prism.rest.representation.DomicileRepresentation;
import uk.co.alumeni.prism.rest.representation.EthnicityRepresentation;
import uk.co.alumeni.prism.rest.representation.OpportunityCategoryRepresentation;
import uk.co.alumeni.prism.rest.representation.OpportunityCategoryRepresentation.OpportunityTypeRepresentation;
import uk.co.alumeni.prism.services.PrismService;
import uk.co.alumeni.prism.services.SystemService;
import uk.co.alumeni.prism.services.helpers.PropertyLoader;

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
                .map(d -> new DomicileRepresentation(d.getId(), propertyLoader.loadEager(d.getId().getDisplayProperty()), d.getCurrency()))
                .collect(toList());
    }

    public List<EthnicityRepresentation> getEthnicityRepresentations() {
        PropertyLoader propertyLoader = applicationContext.getBean(PropertyLoader.class).localizeLazy(systemService.getSystem());
        return Stream.of(PrismEthnicity.values())
                .map(e -> new EthnicityRepresentation(e, propertyLoader.loadEager(e.getDisplayProperty())))
                .collect(toList());
    }

    public List<DisabilityRepresentation> getDisabilityRepresentations() {
        PropertyLoader propertyLoader = applicationContext.getBean(PropertyLoader.class).localizeLazy(systemService.getSystem());
        return Stream.of(PrismDisability.values())
                .map(e -> new DisabilityRepresentation(e, propertyLoader.loadEager(e.getDisplayProperty())))
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
