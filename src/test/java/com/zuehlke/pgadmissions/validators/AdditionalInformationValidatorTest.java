package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.domain.AdditionalInformation;
import com.zuehlke.pgadmissions.domain.builders.AdditionalInformationBuilder;

public class AdditionalInformationValidatorTest {

	private AdditionalInformation info;
	private AdditionalInformationValidator infoValidator;

	@Before
	public void setup() {
		info = new AdditionalInformationBuilder().convictionsText("blabla").setConvictions(true).toAdditionalInformation();

		infoValidator = new AdditionalInformationValidator();
	}

	@Test
	public void shouldSupportPersonalDetail() {
		assertTrue(infoValidator.supports(AdditionalInformation.class));
	}

	@Test
	public void validateGoodAdditionalInfo() {
		info.setInformationText("add info");
		info.setConvictions(false);
		info.setConvictionsText("");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(info, "convictions");
		infoValidator.validate(info, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
	}
	
	@Test
	public void validateGoodConvictionsData() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(info, "convictions");
		infoValidator.validate(info, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
	}

	@Test
	public void validateGoodAddInfoEmpty() {
		info.setConvictions(false);
		info.setConvictionsText(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(info, "convictions");
		infoValidator.validate(info, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
	}

	@Test
	public void shouldRejectIfConvictionsNotSet() {
		info.setConvictions(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(info, "convictions");
		infoValidator.validate(info, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("additionalInformation.convictions.notempty", mappingResult.getFieldError("convictions").getCode());
	}
	
	@Test
	public void shouldRejectIfNullConvictionsTextProvided() {
		info.setConvictionsText(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(info, "informationText");
		infoValidator.validate(info, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("additionalInformation.convictionsText.notempty", mappingResult.getFieldError("convictionsText").getCode());
	}

	@Test
	public void shouldRejectIfEmptyConvictionsTextProvided() {
		info.setConvictionsText("   ");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(info, "informationText");
		infoValidator.validate(info, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("additionalInformation.convictionsText.notempty", mappingResult.getFieldError("convictionsText").getCode());
	}

	@Test
	public void shouldRejectIfNoCovictionsButConvictionTextProvided() {
		info.setConvictions(false);
		info.setConvictionsText("lalala");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(info, "informationText");
		infoValidator.validate(info, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("additionalInformation.convictionsText.noTextExpected", mappingResult.getFieldError("convictionsText").getCode());
	}

	@Test
	public void shouldRejectIfTooMuchAddtionalInfo() {
		StringBuilder addnInfo = new StringBuilder();
		for (int i = 0; i <= 5000; i++) {
			addnInfo.append("a");
		}
		info.setInformationText(addnInfo.toString());
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(info, "informationText");
		infoValidator.validate(info, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("additionalInformation.informationText.notvalid", mappingResult.getFieldError("informationText").getCode());
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
		Assert.assertEquals("additionalInformation.convictionsText.notvalid", mappingResult.getFieldError("convictionsText").getCode());
	}
}
