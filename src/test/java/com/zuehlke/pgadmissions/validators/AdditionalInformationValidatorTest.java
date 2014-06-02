package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.AdditionalInformation;
import com.zuehlke.pgadmissions.domain.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class AdditionalInformationValidatorTest {

	private AdditionalInformation info;
	
	private AdditionalInformationValidator infoValidator;
	
	@Autowired  
    private Validator validator;

	@Before
	public void setup() {
		info = new AdditionalInformation().withConvictionsText("blabla").withHasConvictions(true).withApplication(new Application());
		infoValidator = new AdditionalInformationValidator();
		infoValidator.setValidator((javax.validation.Validator) validator);
	}

	@Test
	public void shouldSupportPersonalDetail() {
		assertTrue(infoValidator.supports(AdditionalInformation.class));
	}

	@Test
	public void validateGoodConvictionsData() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(info, "convictions");
		infoValidator.validate(info, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
	}

	@Test
	public void validateGoodAddInfoEmpty() {
		info.setHasConvictions(false);
		info.setConvictionsText(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(info, "convictions");
		infoValidator.validate(info, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
	}

	@Test
	public void shouldRejectIfConvictionsNotSet() {
		info.setHasConvictions(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(info, "convictions");
		infoValidator.validate(info, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("convictions").getCode());
	}
	
	@Test
	public void shouldRejectIfNullConvictionsTextProvided() {
		info.setConvictionsText(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(info, "informationText");
		infoValidator.validate(info, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("convictionsText").getCode());
	}

	@Test
	public void shouldRejectIfEmptyConvictionsTextProvided() {
		info.setConvictionsText("   ");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(info, "informationText");
		infoValidator.validate(info, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("convictionsText").getCode());
	}

	@Test
	public void shouldRejectIfTooMuchConvictionsInfo() {
		StringBuilder addnInfo = new StringBuilder();
		for (int i = 0; i <= 5000; i++) {
			addnInfo.append("a");
		}
		info.setConvictionsText(addnInfo.toString());
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(info, "convictionsText");
		infoValidator.validate(info, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("A maximum of 400 characters are allowed.", mappingResult.getFieldError("convictionsText").getDefaultMessage());
	}
}
