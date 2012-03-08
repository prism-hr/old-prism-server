package com.zuehlke.pgadmissions.validators;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.dto.EmploymentPosition;

public class EmploymentPositionValidatorTest {

	private EmploymentPosition positionDto;
	private EmploymentPositionValidator positionValidator;
	
	@Test
	public void shouldRejectIfEmployerIsEmpty(){
		positionDto.setPosition_employer(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(positionDto, "position");
		positionValidator.validate(positionDto, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("position.position_employer.notempty",mappingResult.getFieldError("position_employer").getCode());
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
	public void shouldNotRejectIfEndIsEmpty(){
		positionDto.setPosition_endDate(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(positionDto, "position");
		positionValidator.validate(positionDto, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
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
		positionDto.setPosition_language("English");
		positionDto.setPosition_remit("cashier");
		positionDto.setPosition_startDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/08/06"));
		positionDto.setPosition_title("head of department");}
}
