package com.zuehlke.pgadmissions.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EnvironmentTest {

	@Test
	public void shouldGetHostNameFromPropertiesFile(){
		assertEquals("localhost:8080", Environment.getInstance().getApplicationHostName());
	}
}
