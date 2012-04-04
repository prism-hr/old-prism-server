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

import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.dto.EmploymentPosition;

public class EmploymentPositionValidatorTest {

	private EmploymentPosition positionDto;
	private EmploymentPositionValidator positionValidator;
	
	@Test
	public void shouldSupportEmploymentPosition() {
		assertTrue(positionValidator.supports(EmploymentPosition.class));
	}
	
	@Test
	public void shouldRejectIfEmployerIsEmpty(){
		positionDto.setPosition_employer(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(positionDto, "position");
		positionValidator.validate(positionDto, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("position.position_employer.notempty",mappingResult.getFieldError("position_employer").getCode());
	}
	
	@Test
	public void shouldRejectIfStartDateAndEndDateAreFutureDates(){
		Date tomorrow, dayAfterTomorrow;
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		tomorrow = calendar.getTime();
		calendar.add(Calendar.DATE, 2);
		dayAfterTomorrow = calendar.getTime();
		positionDto.setPosition_startDate(tomorrow);
		positionDto.setPosition_endDate(dayAfterTomorrow);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(positionDto, "position");
		positionValidator.validate(positionDto, mappingResult);
		Assert.assertEquals(2, mappingResult.getErrorCount());
		Assert.assertEquals("position.position_startDate.future",mappingResult.getFieldError("position_startDate").getCode());
		Assert.assertEquals("position.position_endDate.future",mappingResult.getFieldError("position_endDate").getCode());
	}
	
	@Test
	public void shouldRejectIfStartDateIsEmpty(){
		positionDto.setPosition_startDate(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(positionDto, "position");
		positionValidator.validate(positionDto, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("position.position_startDate.notempty",mappingResult.getFieldError("position_startDate").getCode());
	}
	
	@Test
	public void shouldRejectIfEndDateIsNotSetForCompletedEmploymentPosition(){
		positionDto.setPosition_endDate(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(positionDto, "position");
		positionValidator.validate(positionDto, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("position.position_endDate.notempty",mappingResult.getFieldError("position_endDate").getCode());
	}
	
	@Test
	public void shouldRejectIfEndDateIsSetForNotCompletedEmploymentPosition(){
		positionDto.setCompleted(CheckedStatus.NO);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(positionDto, "position");
		positionValidator.validate(positionDto, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("position.position_endDate.empty",mappingResult.getFieldError("position_endDate").getCode());
	}
	
	@Test
	public void shouldRejectIfLanguageIsEmpty(){
		positionDto.setPosition_language(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(positionDto, "position");
		positionValidator.validate(positionDto, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("position.position_language.notempty",mappingResult.getFieldError("position_language").getCode());
	}
	
	@Test
	public void shouldRejectIfRemitIsEmpty(){
		positionDto.setPosition_remit(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(positionDto, "position");
		positionValidator.validate(positionDto, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("position.position_remit.notempty",mappingResult.getFieldError("position_remit").getCode());
	}
	
	@Test
	public void shouldRejectIfTitleIsEmpty(){
		positionDto.setPosition_title(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(positionDto, "position");
		positionValidator.validate(positionDto, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("position.position_title.notempty",mappingResult.getFieldError("position_title").getCode());
	}
	
	@Test
	public void shouldRejectIfStartDateIsAfterEndDate() throws ParseException{
		positionDto.setPosition_startDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/08/06"));
		positionDto.setPosition_endDate(new SimpleDateFormat("yyyy/MM/dd").parse("2009/08/06"));
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(positionDto, "position");
		positionValidator.validate(positionDto, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("position.position_startDate.notvalid",mappingResult.getFieldError("position_startDate").getCode());
	}
	
	@Before
	public void setup() throws ParseException{
		
		positionValidator = new EmploymentPositionValidator();
		positionDto = new EmploymentPosition();
		positionDto.setPosition_employer("Mark");
		positionDto.setPosition_endDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/08/06"));
		positionDto.setPosition_language(3);
		positionDto.setPosition_remit("cashier");
		positionDto.setCompleted(CheckedStatus.YES);
		positionDto.setPosition_startDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/08/06"));
		positionDto.setPosition_title("head of department");}
}
