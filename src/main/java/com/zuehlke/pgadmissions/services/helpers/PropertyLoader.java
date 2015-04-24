package com.zuehlke.pgadmissions.services.helpers;

import static com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration.DISPLAY_PROPERTY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
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

    private PrismProgramType programType;

    private final HashMap<PrismDisplayPropertyDefinition, String> properties = Maps.newHashMap();

    @Inject
    private CustomizationService customizationService;

    public String load(PrismDisplayPropertyDefinition property) throws Exception {
        String value = properties.get(property);
        if (value == null) {
            PrismDisplayPropertyCategory category = property.getCategory();
            properties.putAll(getDisplayProperties(resource, property.getCategory().getScope(), category, programType));
            value = properties.get(property);
        }
        return value;
    }

    public String load(PrismDisplayPropertyDefinition trueIndex, PrismDisplayPropertyDefinition falseIndex, boolean evaluation) throws Exception {
        return evaluation ? load(trueIndex) : load(falseIndex);
    }

    public PropertyLoader localize(Resource resource) {
        PrismScope resourceScope = resource.getResourceScope();
        if (resourceScope.ordinal() > INSTITUTION.ordinal()) {
            Program program = resource.getProgram();
            this.resource = program;
            this.programType = program.getProgramType().getPrismProgramType();
        } else {
            this.resource = resource;
            this.programType = null;
        }
        return this;
    }

    private HashMap<PrismDisplayPropertyDefinition, String> getDisplayProperties(Resource resource, PrismScope scope, PrismDisplayPropertyCategory category,
            PrismProgramType programType) throws Exception {
        List<WorkflowConfigurationRepresentation> values = customizationService.getConfigurationRepresentations( //
                DISPLAY_PROPERTY, resource, scope, programType, category);
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
