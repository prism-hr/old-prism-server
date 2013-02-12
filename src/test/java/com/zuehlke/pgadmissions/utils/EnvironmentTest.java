package com.zuehlke.pgadmissions.utils;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class EnvironmentTest {

	@Test
	public void shouldGetHostNameFromPropertiesFile() {
		assertNotNull(Environment.getInstance().getApplicationHostName());
	}

	@Test
	public void shouldGetEmailFromAddressFromPropertiesFile() {
		assertNotNull(Environment.getInstance().getEmailFromAddress());
	}
	
	@Test
	public void shouldGetUclProspectusLinkFromPropertiesFile() {
		assertNotNull(Environment.getInstance().getUCLProspectusLink());
	}

	@Test
	public void shouldGetAdmissionOfferServiceLevelFromPropertiesFile() {
		assertNotNull(Environment.getInstance().getAdmissionsOfferServiceLevel());
	}
	
	@Test
	public void shouldGetAdmissionValidationServiceLevelFromPropertiesFile() {
		assertNotNull(Environment.getInstance().getAdmissionsValidationServiceLevel());
	}
}
