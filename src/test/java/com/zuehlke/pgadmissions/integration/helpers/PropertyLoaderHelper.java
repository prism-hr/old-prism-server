package com.zuehlke.pgadmissions.integration.helpers;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.SYSTEM_NO;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.SYSTEM_YES;
import static com.zuehlke.pgadmissions.domain.definitions.PrismLocale.DE_DE;
import static org.junit.Assert.assertEquals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.WorkflowConfigurationException;
import com.zuehlke.pgadmissions.services.CustomizationService;
import com.zuehlke.pgadmissions.services.SystemService;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

@Service
@Transactional
public class PropertyLoaderHelper {

    @Autowired
    private CustomizationService customizationService;
    
    @Autowired
    private SystemService systemService;
    
    @Autowired
    private ApplicationContext applicationContext;
    
    public void verifyPropertyLoader() throws WorkflowConfigurationException, DeduplicationException {
        PropertyLoader propertyLoader = applicationContext.getBean(PropertyLoader.class);
        assertEquals(propertyLoader.load(SYSTEM_YES), SYSTEM_YES.getDefaultValue());
        
        customizationService.createOrUpdateDisplayProperty(systemService.getSystem(), DE_DE, SYSTEM_YES, "Ja");
        PropertyLoader propertyLoaderDe = applicationContext.getBean(PropertyLoader.class).withLocale(DE_DE);
        assertEquals(propertyLoaderDe.load(SYSTEM_YES), "Ja");
        assertEquals(propertyLoader.load(SYSTEM_YES), SYSTEM_YES.getDefaultValue());
        assertEquals(propertyLoaderDe.load(SYSTEM_NO), SYSTEM_NO.getDefaultValue());        
    }
    
}
