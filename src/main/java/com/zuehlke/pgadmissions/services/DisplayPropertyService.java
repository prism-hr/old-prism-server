package com.zuehlke.pgadmissions.services;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.display.DisplayPropertyConfiguration;
import com.zuehlke.pgadmissions.domain.display.DisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.exceptions.CustomizationException;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;

@Service
@Transactional
public class DisplayPropertyService {

    @Autowired
    private EntityService entityService;

    @Autowired
    private CustomizationService customizationService;

    @Autowired
    private ResourceService resourceService;

    public DisplayPropertyDefinition getDefinitionById(PrismDisplayProperty id) {
        return entityService.getById(DisplayPropertyDefinition.class, id);
    }
    
    public void updateDisplayPropertyConfiguration(Resource resource, PrismLocale locale, PrismProgramType programType,
            DisplayPropertyDefinition displayProperty, String value) throws DeduplicationException, CustomizationException {
        createOrUpdateDisplayPropertyConfiguration(resource, locale, programType, displayProperty, value);
        resourceService.executeUpdate(resource, PrismDisplayProperty.valueOf(resource.getResourceScope().name() + "_COMMENT_UPDATED_DISPLAY_PROPERTY"));
    }

    public void createOrUpdateDisplayPropertyConfiguration(Resource resource, PrismLocale locale, PrismProgramType programType,
            DisplayPropertyDefinition displayProperty, String value) throws DeduplicationException, CustomizationException {
        customizationService.validateConfiguration(resource, displayProperty, locale, programType);
        DisplayPropertyConfiguration transientConfiguration = new DisplayPropertyConfiguration().withResource(resource).withProgramType(programType)
                .withLocale(locale).withDisplayPropertyDefinition(displayProperty).withValue(value)
                .withSystemDefault(customizationService.isSystemDefault(displayProperty, locale, programType));
        entityService.createOrUpdate(transientConfiguration);
    }

    public HashMap<PrismDisplayProperty, String> getDisplayProperties(Resource resource, PrismLocale locale, PrismProgramType programType,
            PrismDisplayPropertyCategory displayPropertyCategory, PrismScope propertyScope) {
        List<DisplayPropertyConfiguration> displayValues = customizationService.getConfiguration(resource, locale, programType, displayPropertyCategory,
                propertyScope);
        HashMap<PrismDisplayProperty, String> displayProperties = Maps.newHashMap();
        for (DisplayPropertyConfiguration displayValue : displayValues) {
            PrismDisplayProperty displayPropertyId = (PrismDisplayProperty) displayValue.getDisplayPropertyDefinition().getId();
            if (!displayProperties.containsKey(displayPropertyId)) {
                displayProperties.put(displayPropertyId, displayValue.getValue());
            }
        }
        return displayProperties;
    }

}
