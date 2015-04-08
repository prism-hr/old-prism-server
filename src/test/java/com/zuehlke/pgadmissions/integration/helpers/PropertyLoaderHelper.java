package com.zuehlke.pgadmissions.integration.helpers;

import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.system.System;
import com.zuehlke.pgadmissions.exceptions.CustomizationException;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.WorkflowConfigurationException;
import com.zuehlke.pgadmissions.rest.dto.DisplayPropertyConfigurationDTO;
import com.zuehlke.pgadmissions.services.CustomizationService;
import com.zuehlke.pgadmissions.services.SystemService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_NO;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_YES;
import static com.zuehlke.pgadmissions.domain.definitions.PrismLocale.EN_GB;
import static org.junit.Assert.assertEquals;

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

    public void verifyPropertyLoader() throws WorkflowConfigurationException, DeduplicationException, CustomizationException, InstantiationException,
            IllegalAccessException {
        System system = systemService.getSystem();

        PropertyLoader propertyLoader = applicationContext.getBean(PropertyLoader.class).localize(system);
        assertEquals(propertyLoader.load(SYSTEM_YES), SYSTEM_YES.getDefaultValue());

        customizationService.createOrUpdateConfiguration(PrismConfiguration.DISPLAY_PROPERTY, systemService.getSystem(), EN_GB, null,
                new DisplayPropertyConfigurationDTO().withDefinitionId(SYSTEM_YES).withValue("Hej"));
        PropertyLoader propertyLoaderSk = applicationContext.getBean(PropertyLoader.class).localize(system);

        assertEquals(propertyLoaderSk.load(SYSTEM_YES), "Hej");
        assertEquals(propertyLoader.load(SYSTEM_YES), SYSTEM_YES.getDefaultValue());
        assertEquals(propertyLoaderSk.load(SYSTEM_NO), SYSTEM_NO.getDefaultValue());
    }
}
