package com.zuehlke.pgadmissions.services.helpers;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.services.CustomizationService;
import com.zuehlke.pgadmissions.services.SystemService;

@Component
@Scope("prototype")
public class PropertyLoader {
    
    private Resource resource;
    
    private PrismLocale locale;
    
    private final HashMap<PrismDisplayProperty, String> properties = Maps.newHashMap();
    
    @Autowired
    private CustomizationService customizationService;
    
    @Autowired
    private SystemService systemService;
    
    public String get(PrismDisplayProperty index) {
        this.resource = this.resource == null ? systemService.getSystem() : this.resource;
        String value = properties.get(index);
        if (value == null) {
            PrismDisplayCategory category = index.getCategory();
            properties.putAll(customizationService.getLocalizedProperties(resource, locale, category));
            value = properties.get(index);
        }
        return value;
    }
    
    public PropertyLoader withResource(Resource resource) {
        this.resource = resource;
        return this;
    }
    
    public PropertyLoader withLocale(PrismLocale locale) {
        this.locale = locale;
        return this;
    }
    
}
