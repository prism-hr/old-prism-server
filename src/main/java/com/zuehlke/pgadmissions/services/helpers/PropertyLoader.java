package com.zuehlke.pgadmissions.services.helpers;

import static com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration.DISPLAY_PROPERTY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import com.zuehlke.pgadmissions.rest.representation.configuration.DisplayPropertyConfigurationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.configuration.WorkflowConfigurationRepresentation;
import com.zuehlke.pgadmissions.services.CustomizationService;
import com.zuehlke.pgadmissions.services.SystemService;

@Component
@Scope(SCOPE_PROTOTYPE)
public class PropertyLoader {

    private Resource resource;

    private PrismOpportunityType opportunityType;

    private final HashMap<PrismDisplayPropertyDefinition, String> properties = Maps.newHashMap();

    @Inject
    private CustomizationService customizationService;

    @Inject
    private SystemService systemService;

    public String loadLazy(PrismDisplayPropertyDefinition property) {
        String value = properties.get(property);
        if (value == null) {
            PrismDisplayPropertyCategory category = property.getCategory();
            properties.putAll(load(resource, property.getCategory().getScope(), opportunityType, category));
            value = properties.get(property);
        }
        return value;
    }

    public String loadEager(PrismDisplayPropertyDefinition property) {
        String value = properties.get(property);
        if (value == null) {
            properties.putAll(load(resource, property.getCategory().getScope(), opportunityType, null));
            value = properties.get(property);
        }
        return value;
    }

    public String loadLazy(PrismDisplayPropertyDefinition trueProperty, PrismDisplayPropertyDefinition falseProperty, boolean evaluation) throws Exception {
        return evaluation ? loadLazy(trueProperty) : loadLazy(falseProperty);
    }

    public String loadEager(PrismDisplayPropertyDefinition trueProperty, PrismDisplayPropertyDefinition falseProperty, boolean evaluation) throws Exception {
        return evaluation ? loadEager(trueProperty) : loadEager(falseProperty);
    }

    public PropertyLoader localizeLazy(Resource resource) {
        if (resource.getResourceScope().equals(SYSTEM)) {
            PropertyLoader loader = systemService.getPropertyLoader();
            if (loader != null) {
                return loader;
            }
        }
        localize(resource);
        return this;
    }

    public PropertyLoader localizeEager(Resource resource) {
        localizeLazy(resource);
        load(resource, resource.getResourceScope(), null, null);
        return this;
    }

    private void localize(Resource resource) {
        this.resource = resource;
        if (ResourceOpportunity.class.isAssignableFrom(resource.getClass())) {
            ResourceOpportunity resourceOpportunity = (ResourceOpportunity) resource;
            this.opportunityType = PrismOpportunityType.valueOf(resourceOpportunity.getOpportunityType().getName());
        }
    }

    private HashMap<PrismDisplayPropertyDefinition, String> load(Resource resource, PrismScope scope, PrismOpportunityType opportunityType, PrismDisplayPropertyCategory category) {
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
