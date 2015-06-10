package com.zuehlke.pgadmissions.services.helpers;

import static com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration.DISPLAY_PROPERTY;
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
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.rest.representation.configuration.DisplayPropertyConfigurationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.configuration.WorkflowConfigurationRepresentation;
import com.zuehlke.pgadmissions.services.CustomizationService;

@Component
@Scope(SCOPE_PROTOTYPE)
public class PropertyLoader {

    private Resource resource;

    private PrismOpportunityType opportunityType;

    private final HashMap<PrismDisplayPropertyDefinition, String> properties = Maps.newHashMap();

    @Inject
    private CustomizationService customizationService;

    public String load(PrismDisplayPropertyDefinition property) {
        String value = properties.get(property);
        if (value == null) {
            PrismDisplayPropertyCategory category = property.getCategory();
            properties.putAll(getDisplayProperties(resource, property.getCategory().getScope(), category, opportunityType));
            value = properties.get(property);
        }
        return value;
    }

    public String load(PrismDisplayPropertyDefinition trueIndex, PrismDisplayPropertyDefinition falseIndex, boolean evaluation) throws Exception {
        return evaluation ? load(trueIndex) : load(falseIndex);
    }

    public PropertyLoader localize(Resource resource) {
        this.resource = resource;
        Program program = resource.getProgram();
        if (program != null) {
            this.opportunityType = program == null ? resource.getProject().getOpportunityType().getPrismOpportunityType() : program.getOpportunityType()
                    .getPrismOpportunityType();
        } else {
            this.opportunityType = null;
        }
        return this;
    }

    private HashMap<PrismDisplayPropertyDefinition, String> getDisplayProperties(Resource resource, PrismScope scope, PrismDisplayPropertyCategory category,
            PrismOpportunityType opportunityType) {
        List<WorkflowConfigurationRepresentation> values = customizationService.getConfigurationRepresentations( //
                DISPLAY_PROPERTY, resource, scope, opportunityType, category);
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
