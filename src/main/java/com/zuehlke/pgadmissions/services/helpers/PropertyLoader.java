package com.zuehlke.pgadmissions.services.helpers;

import static com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration.DISPLAY_PROPERTY;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import com.zuehlke.pgadmissions.mapping.ResourceMapper;
import com.zuehlke.pgadmissions.rest.representation.configuration.DisplayPropertyConfigurationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.configuration.WorkflowConfigurationRepresentation;
import com.zuehlke.pgadmissions.services.CustomizationService;

@Component
@Scope(SCOPE_PROTOTYPE)
public class PropertyLoader {
    
    private static final Logger logger = LoggerFactory.getLogger(ResourceMapper.class);

    private Resource<?> resource;

    private PrismOpportunityType opportunityType;

    private final HashMap<PrismDisplayPropertyDefinition, String> properties = Maps.newHashMap();

    @Inject
    private CustomizationService customizationService;

    public String loadLazy(PrismDisplayPropertyDefinition property) {
        String value = properties.get(property);
        if (value == null) {
            logger.info("Not in map: " + property.name());
            PrismDisplayPropertyCategory category = property.getCategory();
            properties.putAll(getDisplayProperties(resource, property.getCategory().getScope(), category, opportunityType));
            value = properties.get(property);
        }
        return value;
    }
    
    public String loadEager(PrismDisplayPropertyDefinition property) {
        String value = properties.get(property);
        if (value == null) {
            logger.info("Not in map: " + property.name());
            properties.putAll(getDisplayProperties(resource, property.getCategory().getScope(), null, opportunityType));
            value = properties.get(property);
        }
        return value;
    }


    public String load(PrismDisplayPropertyDefinition trueIndex, PrismDisplayPropertyDefinition falseIndex, boolean evaluation) throws Exception {
        return evaluation ? loadLazy(trueIndex) : loadLazy(falseIndex);
    }

    public PropertyLoader localize(Resource<?> resource) {
        this.resource = resource;
        if (ResourceOpportunity.class.isAssignableFrom(resource.getClass())) {
            ResourceOpportunity<?> resourceOpportunity = (ResourceOpportunity<?>) resource;
            this.opportunityType = PrismOpportunityType.valueOf(resourceOpportunity.getOpportunityType().getName());
        }
        return this;
    }

    private HashMap<PrismDisplayPropertyDefinition, String> getDisplayProperties(Resource<?> resource, PrismScope scope, PrismDisplayPropertyCategory category,
            PrismOpportunityType opportunityType) {
        List<WorkflowConfigurationRepresentation> values = customizationService.getConfigurationRepresentations(DISPLAY_PROPERTY, resource, scope, opportunityType, category);
        HashMap<PrismDisplayPropertyDefinition, String> displayProperties = Maps.newHashMap();
        for (WorkflowConfigurationRepresentation value : values) {
            DisplayPropertyConfigurationRepresentation displayValue = (DisplayPropertyConfigurationRepresentation) value;
            PrismDisplayPropertyDefinition displayPropertyId = (PrismDisplayPropertyDefinition) value.getDefinitionId();
            if (!displayProperties.containsKey(displayPropertyId)) {
                displayProperties.put(displayPropertyId, displayValue.getValue());
            }
        }
        return displayProperties;
    }

}
