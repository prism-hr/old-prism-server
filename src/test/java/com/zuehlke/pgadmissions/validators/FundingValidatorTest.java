package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.enums.FundingType;



public class FundingValidatorTest {

	private FundingValidator validator;
	private Funding funding;
	
	@Test
	public void shouldSupportFunding() {
		assertTrue(validator.supports(Funding.class));
	}
	
	@Test
	public void shouldAcceptFunding() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(funding, "funding");
		validator.validate(funding, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfAwardDateNull() {
		funding.setAwardDate(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(funding, "funding");
		validator.validate(funding, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfFundingDescriptionNull() {
		funding.setDescription(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(funding, "funding");
		validator.validate(funding, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfFundingTypeNull() {
		funding.setType(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(funding, "funding");
		validator.validate(funding, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfDocumentIsNull() {
		funding.setDocument(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(funding, "funding");
		validator.validate(funding, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	
	@Test 
	public void shouldRejectIfFundingValueNull() {
		funding.setValue(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(funding, "funding");
		validator.validate(funding, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfAwardFateIsFutureDate(){
		Date tomorrow;
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		tomorrow = calendar.getTime();
		funding.setAwardDate(tomorrow);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(funding, "awardDate");
		validator.validate(funding, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("funding.fundingAwardDate.future", mappingResult.getFieldError("awardDate").getCode());
	}
	
	
	
	@Before
	public void setup() throws ParseException{
		validator = new FundingValidator();
		funding = new Funding();
		funding.setAwardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09"));
		funding.setDescription("Description");
		funding.setId(2);
		funding.setType(FundingType.EMPLOYER);
		funding.setValue("2000");
		funding.setDocument( new DocumentBuilder().id(1).toDocument());
	}
}
