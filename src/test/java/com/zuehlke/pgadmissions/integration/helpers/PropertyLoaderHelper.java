package com.zuehlke.pgadmissions.integration.helpers;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_NO;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_YES;
import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.resource.System;
import com.zuehlke.pgadmissions.rest.dto.DisplayPropertyConfigurationDTO;
import com.zuehlke.pgadmissions.services.CustomizationService;
import com.zuehlke.pgadmissions.services.SystemService;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

@Service
@Transactional
public class PropertyLoaderHelper {

	@Inject
	private CustomizationService customizationService;

	@Inject
	private SystemService systemService;

	@Inject
	private ApplicationContext applicationContext;

	public void verifyPropertyLoader() throws Exception {
		System system = systemService.getSystem();

		PropertyLoader propertyLoader = applicationContext.getBean(PropertyLoader.class).localize(system);
		assertEquals(propertyLoader.loadLazy(SYSTEM_YES), SYSTEM_YES.getDefaultValue());

		customizationService.createOrUpdateConfiguration(PrismConfiguration.DISPLAY_PROPERTY, systemService.getSystem(), null,
		        new DisplayPropertyConfigurationDTO().withDefinitionId(SYSTEM_YES).withValue("Hej"));
		PropertyLoader propertyLoaderSk = applicationContext.getBean(PropertyLoader.class).localize(system);

		assertEquals(propertyLoaderSk.loadLazy(SYSTEM_YES), "Hej");
		assertEquals(propertyLoader.loadLazy(SYSTEM_YES), SYSTEM_YES.getDefaultValue());
		assertEquals(propertyLoaderSk.loadLazy(SYSTEM_NO), SYSTEM_NO.getDefaultValue());
	}

}
