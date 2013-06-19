package com.zuehlke.pgadmissions.utils;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public class FieldErrorUtilsTest {

	
	private MessageSource mockMessageSource;
	private BindingResult mockBindingResult;
	
	@Before
	public void setUp() {
		mockMessageSource = createMock(MessageSource.class);
		mockBindingResult= createMock(BindingResult.class);
	}
	
	@Test
	public void shouldSetErrorMessagesForNonBlankErrorCodes() {
		List<FieldError> fieldErrors = new ArrayList<FieldError>();
		String [] codes = {"code"};
		fieldErrors.add(new FieldError("object1", "field1", null, false, codes, null, "defaultMessage1"));
		fieldErrors.add(new FieldError("object2", "field2", null, false, codes, null, "defaultMessage2"));
		expect(mockBindingResult.getFieldErrors()).andReturn(fieldErrors);
		expect(mockMessageSource.getMessage(eq("code"), (Object[]) eq(null), eq(Locale.getDefault()))).andReturn("error message").times(2);
		
		replay(mockBindingResult, mockMessageSource);
		Map<String, Object> result = FieldErrorUtils.populateMapWithErrors(mockBindingResult, mockMessageSource);
		verify(mockBindingResult, mockMessageSource);
		
		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertEquals(2, result.size());
		assertEquals(result.get("field1"), "error message");
		assertEquals(result.get("field2"), "error message");
	}
	
	@Test
	public void shouldSetErrorMessagesForBlankErrorCodes() {
		List<FieldError> fieldErrors = new ArrayList<FieldError>();
		fieldErrors.add(new FieldError("object1", "field1", "defaultMessage1"));
		fieldErrors.add(new FieldError("object2", "field2", "defaultMessage2"));
		expect(mockBindingResult.getFieldErrors()).andReturn(fieldErrors);
		
		replay(mockBindingResult);
		Map<String, Object> result = FieldErrorUtils.populateMapWithErrors(mockBindingResult, mockMessageSource);
		verify(mockBindingResult);
		
		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertEquals(2, result.size());
		assertEquals(result.get("field1"), "defaultMessage1");
		assertEquals(result.get("field2"), "defaultMessage2");
	}
	
	@Test
	public void shouldSetErrorMessagesForBlankAndNonBlankErrorCodes() {
		List<FieldError> fieldErrors = new ArrayList<FieldError>();
		fieldErrors.add(new FieldError("object1", "field1", "defaultMessage1"));
		String [] codes = {"code"};
		fieldErrors.add(new FieldError("object2", "field2", null, false, codes, null, "defaultMessage2"));
		expect(mockBindingResult.getFieldErrors()).andReturn(fieldErrors);
		expect(mockMessageSource.getMessage(eq("code"), (Object[]) eq(null), eq(Locale.getDefault()))).andReturn("error message").times(1);
		
		replay(mockBindingResult, mockMessageSource);
		Map<String, Object> result = FieldErrorUtils.populateMapWithErrors(mockBindingResult, mockMessageSource);
		verify(mockBindingResult, mockMessageSource);
		
		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertEquals(2, result.size());
		assertEquals(result.get("field1"), "defaultMessage1");
		assertEquals(result.get("field2"), "error message");
	}
	
	
}
