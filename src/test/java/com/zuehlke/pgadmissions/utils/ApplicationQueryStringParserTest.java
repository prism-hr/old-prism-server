package com.zuehlke.pgadmissions.utils;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

public class ApplicationQueryStringParserTest {

	@Test
	public void shouldParseStringAndReturnCorrectValuesAsMap(){
		String queryString = "program:TESTCODE||programHome:http://www.google.com||batchDeadline:29-Oct-2012||projectTitle:Test project";
		Map<String, String> parameters = new ApplicationQueryStringParser().parse(queryString);
		assertEquals(4, parameters.size());
		assertEquals("TESTCODE", parameters.get("program"));
		assertEquals("29-Oct-2012", parameters.get("batchDeadline"));
		assertEquals("http://www.google.com",parameters.get("programHome"));
		assertEquals("Test project", parameters.get("projectTitle"));
	}
	
	@Test 
	public void shouldReturnCorrectValuesIfOptionalParamsNotIncluded(){
		String queryString = "program:TESTCODE";
		Map<String, String> parameters = new ApplicationQueryStringParser().parse(queryString);
		assertEquals(1, parameters.size());
		assertEquals("TESTCODE", parameters.get("program"));
	}
	
}
