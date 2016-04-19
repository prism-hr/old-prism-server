package uk.co.alumeni.prism.services;

import static java.util.Collections.singletonList;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_OPPORTUNITIES_PROPERTY_CLOSING_DATE_LABEL;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_OPPORTUNITIES_PROPERTY_LOCATION_LABEL;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_OPPORTUNITIES_PROPERTY_NO_CLOSING_DATE;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_OPPORTUNITIES_PROPERTY_PAY_FROM;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_OPPORTUNITIES_PROPERTY_PAY_FROM_TO;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_OPPORTUNITIES_PROPERTY_PAY_LABEL;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_OPPORTUNITIES_PROPERTY_PAY_TO;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_OPPORTUNITIES_PROPERTY_STUDY_DURATION_FROM;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_OPPORTUNITIES_PROPERTY_STUDY_DURATION_FROM_TO;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_OPPORTUNITIES_PROPERTY_STUDY_LABEL;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_PARENT_OPPORTUNITY_TYPE_CONTRACT;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_PARENT_OPPORTUNITY_TYPE_PERMANENT;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.advert.AdvertFinancialDetail;
import uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition;
import uk.co.alumeni.prism.domain.resource.ResourceOpportunity;
import uk.co.alumeni.prism.exceptions.ResourceNotFoundException;
import uk.co.alumeni.prism.services.helpers.PropertyLoader;
import uk.co.alumeni.prism.utils.PrismTemplateUtils;

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
    private ApplicationContext applicationContext;

    public String getOpportunityBadge(Advert advert) {
        ResourceOpportunity opportunity = advert.getResourceOpportunity();
        if (opportunity == null) {
            throw new ResourceNotFoundException("Badge can be generated only for opportunities");
        }
        PropertyLoader propertyLoader = applicationContext.getBean(PropertyLoader.class).localizeLazy(advert.getResource());
        String opportunityType = propertyLoader.loadEager(opportunity.getOpportunityType().getId().getDisplayProperty());
        Integer durationMinimum = advert.getDurationMinimum();
        Integer durationMaximum = advert.getDurationMaximum();
        Set<String> locations = advertService.getAdvertLocations(opportunity.getResourceScope(), singletonList(opportunity.getId())).get(advert.getId());

        Map<String, Object> model = new HashMap<>();
        model.put("applicationUrl", applicationUrl);
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
        String payDisplayValue = null;
        if (pay != null && (pay.getMinimum() != null || pay.getMaximum() != null)) {
            if (pay.getMinimum() != null && pay.getMaximum() != null) {
                payDisplayValue = propertyLoader.loadEager(SYSTEM_OPPORTUNITIES_PROPERTY_PAY_FROM_TO);
            } else if (pay.getMinimum() != null) {
                payDisplayValue = propertyLoader.loadEager(SYSTEM_OPPORTUNITIES_PROPERTY_PAY_FROM);
            } else if (pay.getMaximum() != null) {
                payDisplayValue = propertyLoader.loadEager(SYSTEM_OPPORTUNITIES_PROPERTY_PAY_TO);
            }
            payDisplayValue = payDisplayValue.replace("{{minimum}}", "" + pay.getMinimum())
                    .replace("{{maximum}}", "" + pay.getMaximum()).replace("{{currency}}", pay.getCurrency());
            String frequency = propertyLoader.loadEager(PrismDisplayPropertyDefinition.valueOf("SYSTEM_DURATION_UNIT_PER_" + advert.getPay().getInterval()));
            payDisplayValue += ", " + frequency;
            model.put("pay", payDisplayValue);
        }

        return templateUtils.getContentFromLocation("resource_badge", "template/opportunity_badge.ftl", model);
    }
}
