package com.zuehlke.pgadmissions.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class EnvironmentTest {

	@Test
	public void shouldGetHostNameFromPropertiesFile(){
		assertNotNull( Environment.getInstance().getApplicationHostName());
	}
}
