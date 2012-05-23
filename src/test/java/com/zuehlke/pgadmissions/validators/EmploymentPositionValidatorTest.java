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

import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.LanguageBuilder;


public class EmploymentPositionValidatorTest {

	private EmploymentPosition position;
	private EmploymentPositionValidator positionValidator;
	
	@Test
	public void shouldSupportEmploymentPosition() {
		assertTrue(positionValidator.supports(EmploymentPosition.class));
	}
	
	@Test
	public void shouldRejectIfEmployerAddressIsEmpty(){
		position.setEmployerAddress(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(position, "position");
		positionValidator.validate(position, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("position.position_employer_address.notempty",mappingResult.getFieldError("employerAddress").getCode());
	}
	
	
	@Test
	public void shouldRejectIfEmployerCountryIsEmpty(){
		position.setEmployerCountry(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(position, "position");
		positionValidator.validate(position, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("position.position_employer_country.notempty",mappingResult.getFieldError("employerCountry").getCode());
	}
	
	
	@Test
	public void shouldRejectIfEmployerNameIsEmpty(){
		position.setEmployerName(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(position, "position");
		positionValidator.validate(position, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("position.position_employer.notempty",mappingResult.getFieldError("employerName").getCode());
	}
	
	
	@Test
	public void shouldRejectIfStartDateAndEndDateAreFutureDates(){
		Date tomorrow, dayAfterTomorrow;
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		tomorrow = calendar.getTime();
		calendar.add(Calendar.DATE, 2);
		dayAfterTomorrow = calendar.getTime();
		position.setStartDate(tomorrow);
		position.setEndDate(dayAfterTomorrow);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(position, "position");
		positionValidator.validate(position, mappingResult);
		Assert.assertEquals(2, mappingResult.getErrorCount());
		Assert.assertEquals("position.position_startDate.future",mappingResult.getFieldError("startDate").getCode());
		Assert.assertEquals("position.position_endDate.future",mappingResult.getFieldError("endDate").getCode());
	}
	
	@Test
	public void shouldRejectIfStartDateIsEmpty(){
		position.setStartDate(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(position, "position");
		positionValidator.validate(position, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("position.position_startDate.notempty",mappingResult.getFieldError("startDate").getCode());
	}
	
	@Test
	public void shouldRejectIfEndDateIsNotSetForCompletedEmploymentPosition(){
		position.setEndDate(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(position, "position");
		positionValidator.validate(position, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("position.position_endDate.notempty",mappingResult.getFieldError("endDate").getCode());
	}
	
	@Test
	public void shouldRejectIfEndDateIsSetForNotCompletedEmploymentPosition(){
		position.setCurrent(true);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(position, "position");
		positionValidator.validate(position, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("position.position_endDate.empty",mappingResult.getFieldError("endDate").getCode());
	}
	
	@Test
	public void shouldRejectIfLanguageIsEmpty(){
		position.setLanguage(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(position, "position");
		positionValidator.validate(position, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("position.position_language.notempty",mappingResult.getFieldError("language").getCode());
	}
	
	@Test
	public void shouldRejectIfRemitIsEmpty(){
		position.setRemit(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(position, "position");
		positionValidator.validate(position, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("position.position_remit.notempty",mappingResult.getFieldError("remit").getCode());
	}
	
	@Test
	public void shouldRejectIfPositionIsEmpty(){
		position.setPosition(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(position, "position");
		positionValidator.validate(position, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("position.position_title.notempty",mappingResult.getFieldError("position").getCode());
	}
	
	@Test
	public void shouldRejectIfStartDateIsAfterEndDate() throws ParseException{
		position.setStartDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/08/06"));
		position.setEndDate(new SimpleDateFormat("yyyy/MM/dd").parse("2009/08/06"));
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(position, "position");
		positionValidator.validate(position, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("position.position_startDate.notvalid",mappingResult.getFieldError("startDate").getCode());
	}
	
	@Test
	public void shouldRejectifEmployerAddressTooLong() {
		StringBuilder employerAddressLoc = new StringBuilder();
		for (int i = 0; i <=1000; i++) {
			employerAddressLoc.append("a");
		}
		position.setEmployerAddress(employerAddressLoc.toString());
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(position, "position");
		positionValidator.validate(position, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfJobDescriptionTooLong() {
		StringBuilder jobDescription = new StringBuilder();
		for (int i = 0; i <=2000; i++) {
			jobDescription.append("a");
		}
		
		position.setRemit(jobDescription.toString());
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(position, "position");
		positionValidator.validate(position, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Before
	public void setup() throws ParseException{
		
		positionValidator = new EmploymentPositionValidator();
		position = new EmploymentPosition();
		position.setEmployerName("Mark");
		position.setEndDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/08/06"));
		position.setLanguage(new LanguageBuilder().id(3).toLanguage());
		position.setRemit("cashier");
		position.setCurrent(false);
		position.setStartDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/08/06"));
		position.setPosition("head of department");
		position.setEmployerAddress("address");
		position.setEmployerCountry(new CountryBuilder().id(1).toCountry());
	}
}
