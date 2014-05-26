package com.zuehlke.pgadmissions.utils;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.google.common.collect.Lists;

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
		String [] codes = {"code"};
		FieldError fieldError1 = new FieldError("object1", "field1", null, false, codes, null, "defaultMessage1");
		FieldError fieldError2 = new FieldError("object2", "field2", null, false, codes, null, "defaultMessage2");
		expect(mockBindingResult.getFieldErrors()).andReturn(Lists.newArrayList(fieldError1, fieldError2));
		expect(mockMessageSource.getMessage(fieldError1, Locale.getDefault())).andReturn("error message");
		expect(mockMessageSource.getMessage(fieldError2, Locale.getDefault())).andReturn("error message");
		
		replay(mockBindingResult, mockMessageSource);
		Map<String, Object> result = FieldErrorUtils.populateMapWithErrors(mockBindingResult, mockMessageSource);
		verify(mockBindingResult, mockMessageSource);
		
		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertEquals(2, result.size());
		assertEquals(result.get("field1"), "error message");
		assertEquals(result.get("field2"), "error message");
	}
	
}
