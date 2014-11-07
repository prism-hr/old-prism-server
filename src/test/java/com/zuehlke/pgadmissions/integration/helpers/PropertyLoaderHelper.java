package com.zuehlke.pgadmissions.integration.helpers;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.SYSTEM_NO;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.SYSTEM_YES;
import static com.zuehlke.pgadmissions.domain.definitions.PrismLocale.DE_DE;
import static org.junit.Assert.assertEquals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.display.DisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.system.System;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.exceptions.CustomizationException;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.WorkflowConfigurationException;
import com.zuehlke.pgadmissions.services.CustomizationService;
import com.zuehlke.pgadmissions.services.SystemService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

@Service
@Transactional
public class PropertyLoaderHelper {

    @Autowired
    private CustomizationService customizationService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationContext applicationContext;

    public void verifyPropertyLoader() throws WorkflowConfigurationException, DeduplicationException, CustomizationException {
        System system = systemService.getSystem();
        
        PropertyLoader propertyLoader = applicationContext.getBean(PropertyLoader.class).localize(system, system.getUser());
        assertEquals(propertyLoader.load(SYSTEM_YES), SYSTEM_YES.getDefaultValue());

        User herman = userService.getOrCreateUser("herman", "ze german", "hermanzegerman@germany.com", DE_DE);
        DisplayPropertyDefinition displayProperty = customizationService.getDisplayPropertyDefinitionById(SYSTEM_YES);
        customizationService.createOrUpdateDisplayProperty(systemService.getSystem(), DE_DE, null, displayProperty, "Ja");
        PropertyLoader propertyLoaderDe = applicationContext.getBean(PropertyLoader.class).localize(systemService.getSystem(), herman);
        
        assertEquals(propertyLoaderDe.load(SYSTEM_YES), "Ja");
        assertEquals(propertyLoader.load(SYSTEM_YES), SYSTEM_YES.getDefaultValue());
        assertEquals(propertyLoaderDe.load(SYSTEM_NO), SYSTEM_NO.getDefaultValue());
    }

}
