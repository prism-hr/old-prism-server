package com.zuehlke.pgadmissions.validators;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.dto.QualificationDTO;

public class QualificationValidatorTest {
	
	private QualificationDTO qualificationDto;
	private QualificationValidator qualificationValidator;

	@Test
	public void shouldRejectIfProviderIsEmpty(){
		qualificationDto.setQualificationInstitution(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualificationDto, "qualification");
		qualificationValidator.validate(qualificationDto, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("qualification.institution.notempty",mappingResult.getFieldError("qualificationInstitution").getCode());
	}
	@Test
	public void shouldRejectIfNameOfProgrammeIsEmpty(){
		qualificationDto.setQualificationProgramName(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualificationDto, "qualification");
		qualificationValidator.validate(qualificationDto, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("qualification.name_of_programme.notempty",mappingResult.getFieldError("qualificationProgramName").getCode());
	}
	@Test
	public void shouldRejectIfStartDateIsEmpty(){
		qualificationDto.setQualificationStartDate(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualificationDto, "qualification");
		qualificationValidator.validate(qualificationDto, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("qualification.start_date.notempty",mappingResult.getFieldError("qualificationStartDate").getCode());
	}
	@Test
	public void shouldRejectIfLanguageIsEmpty(){
		qualificationDto.setQualificationLanguage(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualificationDto, "qualification");
		qualificationValidator.validate(qualificationDto, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("qualification.language_of_study.notempty",mappingResult.getFieldError("qualificationLanguage").getCode());
	}
	@Test
	public void shouldRejectIfLevelIsEmpty(){
		qualificationDto.setQualificationLevel(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualificationDto, "qualification");
		qualificationValidator.validate(qualificationDto, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("qualification.level.notempty",mappingResult.getFieldError("qualificationLevel").getCode());
	}
	@Test
	public void shouldRejectIfTypeIsEmpty(){
		qualificationDto.setQualificationType(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualificationDto, "qualification");
		qualificationValidator.validate(qualificationDto, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("qualification.type.notempty",mappingResult.getFieldError("qualificationType").getCode());
	}
	@Test
	public void shouldRejectIfGradeIsEmpty(){
		qualificationDto.setQualificationGrade(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualificationDto, "qualification");
		qualificationValidator.validate(qualificationDto, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("qualification.grade.notempty",mappingResult.getFieldError("qualificationGrade").getCode());
	}
	@Test
	public void shouldRejectIfScoreIsEmpty(){
		qualificationDto.setQualificationScore(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualificationDto, "qualification");
		qualificationValidator.validate(qualificationDto, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("qualification.score.notempty",mappingResult.getFieldError("qualificationScore").getCode());
	}
	
	@Test
	public void shouldRejectIfStartDateIsAfterEndDate() throws ParseException{
		qualificationDto.setQualificationStartDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/08/06"));
		qualificationDto.setQualificationAwardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2009/08/06"));
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualificationDto, "qualification");
		qualificationValidator.validate(qualificationDto, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("qualification.start_date.notvalid",mappingResult.getFieldError("qualificationStartDate").getCode());
	}

	@Before
	public void setup() throws ParseException{
		
		qualificationValidator = new QualificationValidator();
		qualificationDto = new QualificationDTO();
		qualificationDto.setQualificationId(3);
		qualificationDto.setQualificationAwardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/09/09"));
		qualificationDto.setQualificationGrade("first");
		qualificationDto.setQualificationInstitution("UCL");
		qualificationDto.setQualificationLanguage("EN");
		qualificationDto.setQualificationLevel("advance");
		qualificationDto.setQualificationProgramName("CS");
		qualificationDto.setQualificationScore("100");
		qualificationDto.setQualificationStartDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/08/06"));
		qualificationDto.setQualificationType("degree");	}
}
