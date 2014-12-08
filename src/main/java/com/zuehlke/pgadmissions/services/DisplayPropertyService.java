package com.zuehlke.pgadmissions.services;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.display.DisplayPropertyConfiguration;
import com.zuehlke.pgadmissions.domain.display.DisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.resource.Resource;

@Service
@Transactional
public class DisplayPropertyService {

    @Autowired
    private EntityService entityService;

    @Autowired
    private CustomizationService customizationService;

    @Autowired
    private ResourceService resourceService;

    public DisplayPropertyDefinition getDefinitionById(PrismDisplayPropertyDefinition id) {
        return entityService.getById(DisplayPropertyDefinition.class, id);
    }

    public HashMap<PrismDisplayPropertyDefinition, String> getDisplayProperties(Resource resource, PrismScope scope, PrismDisplayPropertyCategory category,
            PrismLocale locale, PrismProgramType programType) {
        List<DisplayPropertyConfiguration> displayValues = customizationService.getDisplayPropertyConfiguration(resource, scope, category, locale, programType);
        HashMap<PrismDisplayPropertyDefinition, String> displayProperties = Maps.newHashMap();
        for (DisplayPropertyConfiguration displayValue : displayValues) {
            PrismDisplayPropertyDefinition displayPropertyId = (PrismDisplayPropertyDefinition) displayValue.getDisplayPropertyDefinition().getId();
            if (!displayProperties.containsKey(displayPropertyId)) {
                displayProperties.put(displayPropertyId, displayValue.getValue());
            }
        }
        return displayProperties;
    }

}
