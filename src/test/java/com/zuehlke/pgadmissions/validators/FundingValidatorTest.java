package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.enums.FundingType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class FundingValidatorTest {

    @Autowired  
    private Validator validator;  
    
	private FundingValidator fundingValidator;
    
	private Funding funding;
	
	@Test
	public void shouldSupportFunding() {
		assertTrue(fundingValidator.supports(Funding.class));
	}
	
	@Test
	public void shouldAcceptFunding() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(funding, "funding");
		fundingValidator.validate(funding, mappingResult);
		assertEquals(0, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfAwardDateNull() {
		funding.setAwardDate(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(funding, "funding");
		fundingValidator.validate(funding, mappingResult);
		assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfFundingDescriptionNull() {
		funding.setDescription(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(funding, "funding");
		fundingValidator.validate(funding, mappingResult);
		assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfFundingTypeNull() {
		funding.setType(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(funding, "funding");
		fundingValidator.validate(funding, mappingResult);
		assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfDocumentIsNull() {
		funding.setDocument(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(funding, "funding");
		fundingValidator.validate(funding, mappingResult);
		assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test 
	public void shouldRejectIfFundingValueNull() {
		funding.setValue(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(funding, "funding");
		fundingValidator.validate(funding, mappingResult);
		assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfAwardFateIsFutureDate(){
		Date tomorrow;
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		tomorrow = calendar.getTime();
		funding.setAwardDate(tomorrow);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(funding, "awardDate");
		fundingValidator.validate(funding, mappingResult);
		assertEquals(1, mappingResult.getErrorCount());
		assertEquals("date.field.notpast", mappingResult.getFieldError("awardDate").getCode());
	}
	
	@Test
	public void shouldRejectIfDescriptionTooLong() {
		StringBuilder fundDescription = new StringBuilder();
		for (int i = 0; i <=2000; i++) {
			fundDescription.append("a");
		}
		
		funding.setDescription(fundDescription.toString());
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(funding, "awardDate");
		fundingValidator.validate(funding, mappingResult);
		assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Before
    public void setup() throws ParseException{
		funding = new Funding();
		funding.setApplication(new ApplicationFormBuilder().id(3).build());
		funding.setAwardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09"));
		funding.setDescription("Description");
		funding.setId(2);
		funding.setType(FundingType.EMPLOYER);
		funding.setValue("2000");
		funding.setDocument( new DocumentBuilder().id(1).build());
		
		fundingValidator = new FundingValidator();
		fundingValidator.setValidator((javax.validation.Validator) validator);
	}
}
