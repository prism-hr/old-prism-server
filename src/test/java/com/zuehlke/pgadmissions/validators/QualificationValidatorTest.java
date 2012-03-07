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
		qualificationDto.setInstitution(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualificationDto, "qualification");
		qualificationValidator.validate(qualificationDto, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("qualification.institution.notempty",mappingResult.getFieldError("institution").getCode());
	}
	@Test
	public void shouldRejectIfNameOfProgrammeIsEmpty(){
		qualificationDto.setName_of_programme(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualificationDto, "qualification");
		qualificationValidator.validate(qualificationDto, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("qualification.name_of_programme.notempty",mappingResult.getFieldError("name_of_programme").getCode());
	}
	@Test
	public void shouldRejectIfStartDateIsEmpty(){
		qualificationDto.setStart_date(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualificationDto, "qualification");
		qualificationValidator.validate(qualificationDto, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("qualification.start_date.notempty",mappingResult.getFieldError("start_date").getCode());
	}
	@Test
	public void shouldRejectIfLanguageIsEmpty(){
		qualificationDto.setLanguage_of_study(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualificationDto, "qualification");
		qualificationValidator.validate(qualificationDto, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("qualification.language_of_study.notempty",mappingResult.getFieldError("language_of_study").getCode());
	}
	@Test
	public void shouldRejectIfLevelIsEmpty(){
		qualificationDto.setLevel(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualificationDto, "qualification");
		qualificationValidator.validate(qualificationDto, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("qualification.level.notempty",mappingResult.getFieldError("level").getCode());
	}
	@Test
	public void shouldRejectIfTypeIsEmpty(){
		qualificationDto.setQualification_type(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualificationDto, "qualification");
		qualificationValidator.validate(qualificationDto, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("qualification.type.notempty",mappingResult.getFieldError("qualification_type").getCode());
	}
	@Test
	public void shouldRejectIfGradeIsEmpty(){
		qualificationDto.setGrade(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualificationDto, "qualification");
		qualificationValidator.validate(qualificationDto, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("qualification.grade.notempty",mappingResult.getFieldError("grade").getCode());
	}
	@Test
	public void shouldRejectIfScoreIsEmpty(){
		qualificationDto.setScore(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualificationDto, "qualification");
		qualificationValidator.validate(qualificationDto, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("qualification.score.notempty",mappingResult.getFieldError("score").getCode());
	}

	@Before
	public void setup() throws ParseException{
		
		qualificationValidator = new QualificationValidator();
		qualificationDto = new QualificationDTO();
		qualificationDto.setQualId(3);
		qualificationDto.setAward_date(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09"));
		qualificationDto.setGrade("first");
		qualificationDto.setInstitution("UCL");
		qualificationDto.setLanguage_of_study("EN");
		qualificationDto.setLevel("advance");
		qualificationDto.setName_of_programme("CS");
		qualificationDto.setScore("100");
		qualificationDto.setStart_date(new SimpleDateFormat("yyyy/MM/dd").parse("2010/08/06"));
		qualificationDto.setQualification_type("degree");	}
}
