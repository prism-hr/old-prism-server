package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.domain.Reference;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;


public class ReferenceValidatorTest {

	private ReferenceValidator validator;
	private Reference reference;

	@Test
	public void shouldSupporReference(){
		assertTrue(validator.supports(Reference.class));
	}
	@Test
	public void shouldAcceptReference(){

		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reference, "document");
		validator.validate(reference, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
	}
	@Test
	public void shouldRejectIfFileIsMissing(){
		reference.setDocument(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reference, "document");
		validator.validate(reference, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}

	@Before
	public void setup() throws ParseException{
		validator = new  ReferenceValidator();
		reference = new Reference();
		reference.setDocument(new DocumentBuilder().id(1).toDocument());
	}
}
