package uk.co.alumeni.prism.integration.helpers;

import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_NO;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_YES;
import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.alumeni.prism.domain.definitions.PrismConfiguration;
import uk.co.alumeni.prism.domain.resource.System;
import uk.co.alumeni.prism.rest.dto.DisplayPropertyConfigurationDTO;
import uk.co.alumeni.prism.services.CustomizationService;
import uk.co.alumeni.prism.services.SystemService;
import uk.co.alumeni.prism.services.helpers.PropertyLoader;

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

		PropertyLoader propertyLoader = applicationContext.getBean(PropertyLoader.class).localizeLazy(system);
		assertEquals(propertyLoader.loadLazy(SYSTEM_YES), SYSTEM_YES.getDefaultValue());

		customizationService.createOrUpdateConfiguration(PrismConfiguration.DISPLAY_PROPERTY, systemService.getSystem(), null,
		        new DisplayPropertyConfigurationDTO().withDefinitionId(SYSTEM_YES).withValue("Hej"));
		PropertyLoader propertyLoaderSk = applicationContext.getBean(PropertyLoader.class).localizeLazy(system);

		assertEquals(propertyLoaderSk.loadLazy(SYSTEM_YES), "Hej");
		assertEquals(propertyLoader.loadLazy(SYSTEM_YES), SYSTEM_YES.getDefaultValue());
		assertEquals(propertyLoaderSk.loadLazy(SYSTEM_NO), SYSTEM_NO.getDefaultValue());
	}

}
