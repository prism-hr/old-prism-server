package uk.co.alumeni.prism.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.advert.AdvertFinancialDetail;
import uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition;
import uk.co.alumeni.prism.domain.resource.ResourceOpportunity;
import uk.co.alumeni.prism.domain.resource.ResourceParent;
import uk.co.alumeni.prism.exceptions.ResourceNotFoundException;
import uk.co.alumeni.prism.mapping.AdvertMapper;
import uk.co.alumeni.prism.services.helpers.PropertyLoader;
import uk.co.alumeni.prism.utils.PrismTemplateUtils;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.*;

@Service
@Transactional
public class WidgetService {

    @Value("${application.url}")
    private String applicationUrl;

    @Inject
    private PrismTemplateUtils templateUtils;

    @Inject
    private AdvertService advertService;

    @Inject
    private AdvertMapper advertMapper;

    @Inject
    private ApplicationContext applicationContext;

    public String getAdvertBadge(Advert advert, Map<String, String> options) {
        if (advert.getResourceOpportunity() != null) {
            return getOpportunityBadge(advert, advert.getResourceOpportunity());
        } else if (advert.getParentResources() != null) {
            return getParentResourceBadge(advert, advert.getResourceParent(), options);
        }
        throw new ResourceNotFoundException("Incorrect resource type");
    }

    public String getOpportunityBadge(Advert advert, ResourceOpportunity opportunity) {
        Map<String, Object> model = new HashMap<>();
        model.put("applicationUrl", applicationUrl);
        model.put("opportunity", createOpportunityModel(advert, opportunity));

        return templateUtils.getContentFromLocation("opportunity_badge.ftl", model);
    }

    public String getParentResourceBadge(Advert advert, ResourceParent resource, Map<String, String> options) {
        if (options == null) {
            options = new HashMap<>();
        }
        if (!options.containsKey("type")) {
            options.put("type", "SIMPLE");
        }

        Map<String, Object> model = new HashMap<>();
        model.put("applicationUrl", applicationUrl);
        model.put("advert", advert);
        model.put("options", options);

        List<Advert> opportunityAdverts = advertService.getBadgeAdverts(resource, 3);
        List<Map<String, Object>> opportunities = opportunityAdverts.stream().map(ad -> createOpportunityModel(ad, ad.getResourceOpportunity())).collect(Collectors.toList());
        model.put("opportunities", opportunities);

        return templateUtils.getContentFromLocation("resource_parent_badge.ftl", model);
    }

    private Map<String, Object> createOpportunityModel(Advert advert, ResourceOpportunity opportunity) {
        PropertyLoader propertyLoader = applicationContext.getBean(PropertyLoader.class).localizeLazy(opportunity);
        String opportunityType = propertyLoader.loadEager(opportunity.getOpportunityType().getId().getDisplayProperty());
        Integer durationMinimum = advert.getDurationMinimum();
        Integer durationMaximum = advert.getDurationMaximum();
        Set<String> locations = advertService.getAdvertLocations(opportunity.getResourceScope(), singletonList(opportunity.getId())).get(advert.getId());

        Map<String, Object> model = new HashMap<>();
        model.put("opportunityType", opportunityType);
        model.put("advert", advert);
        model.put("availabilityLabel", propertyLoader.loadEager(durationMinimum != null ? SYSTEM_RESOURCE_PARENT_OPPORTUNITY_TYPE_CONTRACT
                : SYSTEM_RESOURCE_PARENT_OPPORTUNITY_TYPE_PERMANENT));
        model.put("availability", propertyLoader.loadEager(SYSTEM_OPPORTUNITIES_PROPERTY_STUDY_LABEL));
        model.put("studyOptions", opportunity.getResourceStudyOptions().stream().map(s -> propertyLoader.loadEager(s.getStudyOption().getDisplayProperty()))
                .collect(Collectors.toList()));
        model.put("locationLabel", propertyLoader.loadEager(SYSTEM_OPPORTUNITIES_PROPERTY_LOCATION_LABEL));
        model.put("locations", locations);
        model.put("payLabel", propertyLoader.loadEager(SYSTEM_OPPORTUNITIES_PROPERTY_PAY_LABEL));
        model.put("closingDateLabel", propertyLoader.loadEager(SYSTEM_OPPORTUNITIES_PROPERTY_CLOSING_DATE_LABEL));
        model.put("noClosingDate", propertyLoader.loadEager(SYSTEM_OPPORTUNITIES_PROPERTY_NO_CLOSING_DATE));

        if (durationMinimum != null) {
            String durationString;
            if (durationMaximum != null && !Objects.equals(durationMaximum, durationMinimum)) {
                durationString = propertyLoader.loadEager(SYSTEM_OPPORTUNITIES_PROPERTY_STUDY_DURATION_FROM_TO);
            } else {
                durationString = propertyLoader.loadEager(SYSTEM_OPPORTUNITIES_PROPERTY_STUDY_DURATION_FROM);
            }
            durationString = durationString.replace("{{durationMinimum}}", "" + durationMinimum);
            durationString = durationString.replace("{{durationMaximum}}", "" + durationMaximum);
            model.put("duration", durationString);
        }

        AdvertFinancialDetail pay = advert.getPay();
        String payDisplayValue;
        if (pay != null && (pay.getMinimum() != null || pay.getMaximum() != null)) {
            if (pay.getMinimum() != null && pay.getMaximum() != null) {
                payDisplayValue = propertyLoader.loadEager(SYSTEM_OPPORTUNITIES_PROPERTY_PAY_FROM_TO);
            } else if (pay.getMinimum() != null) {
                payDisplayValue = propertyLoader.loadEager(SYSTEM_OPPORTUNITIES_PROPERTY_PAY_FROM);
            } else { // pay.getMaximum() != null
                payDisplayValue = propertyLoader.loadEager(SYSTEM_OPPORTUNITIES_PROPERTY_PAY_TO);
            }
            payDisplayValue = payDisplayValue.replace("{{minimum}}", "" + pay.getMinimum())
                    .replace("{{maximum}}", "" + pay.getMaximum()).replace("{{currency}}", pay.getCurrency());
            String frequency = propertyLoader.loadEager(PrismDisplayPropertyDefinition.valueOf("SYSTEM_DURATION_UNIT_PER_" + advert.getPay().getInterval()));
            payDisplayValue += ", " + frequency;
            model.put("pay", payDisplayValue);
        }

        return model;
    }

}
