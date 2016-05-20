package uk.co.alumeni.prism.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition;
import uk.co.alumeni.prism.domain.resource.ResourceOpportunity;
import uk.co.alumeni.prism.domain.resource.ResourceParent;
import uk.co.alumeni.prism.exceptions.ResourceNotFoundException;
import uk.co.alumeni.prism.mapping.AdvertMapper;
import uk.co.alumeni.prism.rest.dto.OpportunitiesQueryDTO;
import uk.co.alumeni.prism.rest.representation.advert.AdvertFinancialDetailRepresentation;
import uk.co.alumeni.prism.rest.representation.advert.AdvertListRepresentation;
import uk.co.alumeni.prism.rest.representation.advert.AdvertRepresentationExtended;
import uk.co.alumeni.prism.rest.representation.resource.ResourceOpportunityRepresentationSimple;
import uk.co.alumeni.prism.services.helpers.PropertyLoader;
import uk.co.alumeni.prism.utils.PrismTemplateUtils;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.*;
import static uk.co.alumeni.prism.domain.definitions.PrismResourceContext.APPLICANT;

@Service
@Transactional
public class WidgetService {

    @Value("${application.url}")
    private String applicationUrl;

    @Inject
    private PrismTemplateUtils templateUtils;

    @Inject
    private AdvertMapper advertMapper;

    @Inject
    private ApplicationContext applicationContext;

    public String getAdvertBadge(Advert advert, Map<String, String> options) {
        ResourceParent resourceParent = advert.getResourceParent();
        ResourceOpportunity resourceOpportunity = advert.getResourceOpportunity();
        if (resourceOpportunity != null) {
            return getOpportunityBadge(advert, resourceOpportunity, options);
        } else if (resourceParent != null) {
            return getResourceParentBadge(advert, resourceParent, options);
        }
        throw new ResourceNotFoundException("Incorrect resource type");
    }

    private String getOpportunityBadge(Advert advert, ResourceOpportunity resource, Map<String, String> options) {
        AdvertRepresentationExtended advertRepresentation = advertMapper.getAdvertRepresentationExtended(advert);
        PropertyLoader propertyLoader = applicationContext.getBean(PropertyLoader.class).localizeLazy(resource);
        Map<String, Object> model = createHeaderModel(advert, propertyLoader, options);

        if (advertRepresentation != null) {
            model.put("opportunity", createOpportunityModel(advertRepresentation, propertyLoader));
        }

        return templateUtils.getContentFromLocation("opportunity_badge.ftl", model);
    }

    private String getResourceParentBadge(Advert advert, ResourceParent resource, Map<String, String> options) {
        if (!options.containsKey("type")) {
            options.put("type", "SIMPLE");
        }
        if (!options.containsKey("positionCount")) {
            options.put("positionCount", "3");
        }
        if (!options.containsKey("context")) {
            return "Context missing";
        }

        PropertyLoader propertyLoader = applicationContext.getBean(PropertyLoader.class).localizeLazy(resource);
        Map<String, Object> model = createHeaderModel(advert, propertyLoader, options);

        OpportunitiesQueryDTO query = new OpportunitiesQueryDTO().withContext(APPLICANT)
                .withResourceScope(resource.getResourceScope()).withResourceId(resource.getId())
                .withMaxAdverts(Integer.parseInt(options.get("positionCount")));
        AdvertListRepresentation advertRepresentations = advertMapper.getAdvertExtendedRepresentations(query);
        List<Map<String, Object>> adverts = advertRepresentations.getRows().stream()
                .map(advertRepresentation -> createOpportunityModel(advertRepresentation, propertyLoader))
                .collect(toList());
        model.put("invisibleAdvertCount", advertRepresentations.getInvisibleAdvertCount());
        model.put("invisibleAdvertInstitutions", advertRepresentations.getInvisibleAdvertInstitutions());
        model.put("opportunities", adverts);

        return templateUtils.getContentFromLocation("resource_parent_badge.ftl", model);
    }

    private Map<String, Object> createHeaderModel(Advert advert, PropertyLoader propertyLoader, Map<String, String> options) {
        Map<String, Object> model = new HashMap<>();
        model.put("options", options);
        model.put("advert", advert);
        model.put("applicationUrl", applicationUrl);
        model.put("headerTitle", propertyLoader.loadLazy(SYSTEM_RESOURCE_SHARE_OPPORTUNITIES));
        model.put("viewOpportunitiesLabel", propertyLoader.loadLazy(SYSTEM_RESOURCE_SHARE_VIEW_OPPORTUNITIES));
        model.put("postOpportunityLabel", propertyLoader.loadLazy(SYSTEM_RESOURCE_SHARE_POST_OPPORTUNITY));
        model.put("otherOpportunitiesLabel", propertyLoader.loadLazy(SYSTEM_RESOURCE_SHARE_OTHER_OPPORTUNITIES));
        model.put("applyNowLabel", propertyLoader.loadLazy(SYSTEM_APPLY));
        return model;
    }

    private Map<String, Object> createOpportunityModel(AdvertRepresentationExtended advert, PropertyLoader propertyLoader) {
        ResourceOpportunityRepresentationSimple opportunity = (ResourceOpportunityRepresentationSimple) advert.getResource();

        String opportunityType = propertyLoader.loadLazy(advert.getOpportunityType().getDisplayProperty());
        Integer durationMinimum = opportunity.getDurationMinimum();
        Integer durationMaximum = opportunity.getDurationMaximum();

        Map<String, Object> model = new HashMap<>();
        model.put("opportunityType", opportunityType);
        model.put("advert", advert);
        model.put("availabilityLabel", propertyLoader.loadLazy(durationMinimum != null ? SYSTEM_RESOURCE_PARENT_OPPORTUNITY_TYPE_CONTRACT
                : SYSTEM_RESOURCE_PARENT_OPPORTUNITY_TYPE_PERMANENT));
        model.put("availability", propertyLoader.loadLazy(SYSTEM_OPPORTUNITIES_PROPERTY_STUDY_LABEL));
        model.put("studyOptions", advert.getStudyOptions().stream().map(s -> propertyLoader.loadLazy(s.getDisplayProperty()))
                .collect(Collectors.toList()));
        model.put("locationLabel", propertyLoader.loadLazy(SYSTEM_OPPORTUNITIES_PROPERTY_LOCATION_LABEL));
        model.put("locations", advert.getCategories().getLocationsDisplay());
        model.put("payLabel", propertyLoader.loadLazy(SYSTEM_OPPORTUNITIES_PROPERTY_PAY_LABEL));
        model.put("closingDateLabel", propertyLoader.loadLazy(SYSTEM_OPPORTUNITIES_PROPERTY_CLOSING_DATE_LABEL));
        model.put("noClosingDate", propertyLoader.loadLazy(SYSTEM_OPPORTUNITIES_PROPERTY_NO_CLOSING_DATE));

        if (durationMinimum != null) {
            String durationString;
            if (durationMaximum != null && !Objects.equals(durationMaximum, durationMinimum)) {
                durationString = propertyLoader.loadLazy(SYSTEM_OPPORTUNITIES_PROPERTY_STUDY_DURATION_FROM_TO);
            } else {
                durationString = propertyLoader.loadLazy(SYSTEM_OPPORTUNITIES_PROPERTY_STUDY_DURATION_FROM);
            }
            durationString = durationString.replace("{{durationMinimum}}", "" + durationMinimum);
            durationString = durationString.replace("{{durationMaximum}}", "" + durationMaximum);
            model.put("duration", durationString);
        }

        AdvertFinancialDetailRepresentation pay = advert.getFinancialDetail();
        String payDisplayValue;
        if (pay != null && (pay.getMinimum() != null || pay.getMaximum() != null)) {
            if (pay.getMinimum() != null && pay.getMaximum() != null) {
                payDisplayValue = propertyLoader.loadLazy(SYSTEM_OPPORTUNITIES_PROPERTY_PAY_FROM_TO);
            } else if (pay.getMinimum() != null) {
                payDisplayValue = propertyLoader.loadLazy(SYSTEM_OPPORTUNITIES_PROPERTY_PAY_FROM);
            } else { // pay.getMaximum() != null
                payDisplayValue = propertyLoader.loadLazy(SYSTEM_OPPORTUNITIES_PROPERTY_PAY_TO);
            }
            payDisplayValue = payDisplayValue.replace("{{minimum}}", "" + pay.getMinimum())
                    .replace("{{maximum}}", "" + pay.getMaximum()).replace("{{currency}}", pay.getCurrency());
            String frequency = propertyLoader.loadLazy(PrismDisplayPropertyDefinition.valueOf("SYSTEM_DURATION_UNIT_PER_" + pay.getInterval()));
            payDisplayValue += ", " + frequency;
            model.put("pay", payDisplayValue);
        }

        return model;
    }

}
