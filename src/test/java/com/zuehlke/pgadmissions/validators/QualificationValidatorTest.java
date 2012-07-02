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

import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.LanguageBuilder;

public class QualificationValidatorTest {
	
	private Qualification qualification;
	private QualificationValidator qualificationValidator;

	@Test
	public void shouldSupportQualification() {
		assertTrue(qualificationValidator.supports(Qualification.class));
	}
	
	@Test
	public void shouldRejectIfProviderIsEmpty(){
		qualification.setQualificationInstitution(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualification, "qualification");
		qualificationValidator.validate(qualification, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty",mappingResult.getFieldError("qualificationInstitution").getCode());
	}
	
	@Test
	public void shouldRejectIfInstaitutionCoutnryIssEmpty(){
		qualification.setInstitutionCountry(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualification, "qualification");
		qualificationValidator.validate(qualification, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none",mappingResult.getFieldError("institutionCountry").getCode());
	}
	@Test
	public void shouldRejectIfSubjectIsEmpty(){
		qualification.setQualificationSubject(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualification, "qualification");
		qualificationValidator.validate(qualification, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty",mappingResult.getFieldError("qualificationSubject").getCode());
	}
	@Test
	public void shouldRejectIfStartDateIsEmpty(){
		qualification.setQualificationStartDate(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualification, "qualification");
		qualificationValidator.validate(qualification, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty",mappingResult.getFieldError("qualificationStartDate").getCode());
	}
	@Test
	public void shouldRejectIfLanguageIsEmpty(){
		qualification.setQualificationLanguage(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualification, "qualification");
		qualificationValidator.validate(qualification, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none",mappingResult.getFieldError("qualificationLanguage").getCode());
	}

	@Test
	public void shouldRejectIfStartDateAndEndDateAreFutureDates(){
		Date tomorrow, dayAfterTomorrow;
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		tomorrow = calendar.getTime();
		calendar.add(Calendar.DATE, 2);
		dayAfterTomorrow = calendar.getTime();
		qualification.setQualificationStartDate(tomorrow);
		qualification.setQualificationAwardDate(dayAfterTomorrow);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualification, "qualification");
		qualificationValidator.validate(qualification, mappingResult);
		Assert.assertEquals(2, mappingResult.getErrorCount());
		Assert.assertEquals("date.field.notpast",mappingResult.getFieldError("qualificationStartDate").getCode());
		Assert.assertEquals("date.field.notpast",mappingResult.getFieldError("qualificationAwardDate").getCode());
	}
	
	public void shouldRejectIfTypeIsEmpty(){
		qualification.setQualificationType(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualification, "qualification");
		qualificationValidator.validate(qualification, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("qualification.type.notempty",mappingResult.getFieldError("qualificationType").getCode());
	}
	@Test
	public void shouldRejectIfGradeIsEmpty(){
		qualification.setQualificationGrade(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualification, "qualification");
		qualificationValidator.validate(qualification, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty",mappingResult.getFieldError("qualificationGrade").getCode());
	}

	
	@Test
	public void shouldRejectIfStartDateIsAfterEndDate() throws ParseException{
		qualification.setQualificationStartDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/08/06"));
		qualification.setQualificationAwardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2009/08/06"));
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualification, "qualification");
		qualificationValidator.validate(qualification, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("qualification.start_date.notvalid",mappingResult.getFieldError("qualificationStartDate").getCode());
	}

	@Before
	public void setup() throws ParseException{
		
		qualificationValidator = new QualificationValidator();
		qualification = new Qualification();
		qualification.setApplication(new ApplicationFormBuilder().id(9).toApplicationForm());
		qualification.setId(3);
		qualification.setQualificationAwardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/09/09"));
		qualification.setQualificationGrade("first");
		qualification.setQualificationInstitution("UCL");
		qualification.setInstitutionCountry(new Country());
		qualification.setQualificationLanguage(new LanguageBuilder().id(1).toLanguage());
		qualification.setQualificationSubject("CS");		
		qualification.setQualificationStartDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/08/06"));
		qualification.setQualificationType("degree");	}
}
