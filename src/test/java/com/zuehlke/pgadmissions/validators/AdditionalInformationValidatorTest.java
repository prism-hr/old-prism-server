package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.dto.AdditionalInformation;


public class AdditionalInformationValidatorTest {

	private AdditionalInformationValidator validator;
	
	@Test
	public void shouldSupportAdditionalInformation() {
		assertTrue(validator.supports(AdditionalInformation.class));
	}
	
	@Test
	public void shouldAcceptIfInfoNotExceedsMaxLimit() {
		AdditionalInformation info = new AdditionalInformation();
		info.setAdditionalInformation("my information test");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(info, "additionalInformation");
		validator.validate(info, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfInfoExceedsMaxLimit() {
		AdditionalInformation info = new AdditionalInformation();
		StringBuilder builderInfo = new StringBuilder();
		for (int i =0; i<=5000; i++) {
			builderInfo.append("a");
		}
		info.setAdditionalInformation(builderInfo.toString());
		
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(info, "additionalInformation");
		validator.validate(info, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Before
	public void setup(){
		validator = new AdditionalInformationValidator();
	}
}
