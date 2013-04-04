package com.zuehlke.pgadmissions.utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class ApplicationQueryStringParserTest {

	@Test
	public void shouldParseStringAndReturnCorrectValuesAsArray(){
		String queryString = "program:TESTCODE||programhome:http://www.google.com||bacthdeadline:29-Oct-2012||projectTitle:Test project";
		String[] parameters = new ApplicationQueryStringParser().parse(queryString);
		assertEquals("TESTCODE", parameters[0]);
		assertEquals("http://www.google.com", parameters[1]);
		assertEquals("29-Oct-2012", parameters[2]);
		assertEquals("Test project", parameters[3]);
	}
	
	@Test 
	public void shouldReturnCorrectValuesIfOptionalParamsNotIncluded(){
		String queryString = "program:TESTCODE";
		String[] parameters = new ApplicationQueryStringParser().parse(queryString);
		assertEquals("TESTCODE", parameters[0]);
		assertNull( parameters[1]);
		assertNull(parameters[2]);
		assertNull( parameters[3]);
	}
	
	@Test 
	public void shouldReturnCorrectValuesIfProjectTitleContainsDelimiter(){
		String queryString = "program:TESTCODE||programhome:http://www.google.com||bacthdeadline:29-Oct-2012||projectTitle:Test||project";
		String[] parameters = new ApplicationQueryStringParser().parse(queryString);
		assertEquals("TESTCODE", parameters[0]);
		assertEquals("http://www.google.com", parameters[1]);
		assertEquals("29-Oct-2012", parameters[2]);
		assertEquals("Test||project", parameters[3]);
	}
	
	@Test 
	public void shouldAddProtocolToUrlIfNotProvided(){
		String queryString ="programhome:www.google.com";
		String[] parameters = new ApplicationQueryStringParser().parse(queryString);
		assertNull( parameters[0]);
		assertEquals("http://www.google.com", parameters[1]);
		assertNull(parameters[2]);
		assertNull( parameters[3]);
	}
}
