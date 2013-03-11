package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationTypeBuilder;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class QualificationValidatorTest {

    @Autowired
    private Validator validator;

    private Qualification qualification;

    private QualificationValidator qualificationValidator;

    @Test
    public void shouldSupportQualification() {
        assertTrue(qualificationValidator.supports(Qualification.class));
    }

    @Test
    public void shouldRejectIfProviderIsEmpty() {
        qualification.setQualificationInstitution(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualification, "qualification");
        qualificationValidator.validate(qualification, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("qualificationInstitution").getCode());
    }

    @Test
    public void shouldRejectIfInstitutionCountryIsEmpty() {
        qualification.setInstitutionCountry(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualification, "qualification");
        qualificationValidator.validate(qualification, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("institutionCountry").getCode());
    }

    @Test
    public void shouldRejectIfSubjectIsEmpty() {
        qualification.setQualificationSubject(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualification, "qualification");
        qualificationValidator.validate(qualification, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("qualificationSubject").getCode());
    }

    @Test
    public void shouldRejectIfStartDateIsEmpty() {
        qualification.setQualificationStartDate(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualification, "qualification");
        qualificationValidator.validate(qualification, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("qualificationStartDate").getCode());
    }
    
    @Test
    public void shouldRejectIfAwardDateIsEmpty() {
        qualification.setQualificationAwardDate(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualification, "qualification");
        qualificationValidator.validate(qualification, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("qualificationAwardDate").getCode());
    }

    @Test
    public void shouldRejectIfLanguageIsEmpty() {
        qualification.setQualificationLanguage(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualification, "qualification");
        qualificationValidator.validate(qualification, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("qualificationLanguage").getCode());
    }

    @Test
    public void shouldRejectIfStartDateAndEndDateAreFutureDates() {
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
        Assert.assertEquals("date.field.notpast", mappingResult.getFieldError("qualificationStartDate").getCode());
        Assert.assertEquals("date.field.notpast", mappingResult.getFieldError("qualificationAwardDate").getCode());
    }

    @Test
    public void shouldRejectIfTypeIsEmpty() {
        qualification.setQualificationType(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualification, "qualification");
        qualificationValidator.validate(qualification, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("qualificationType").getCode());
    }

    @Test
    public void shouldRejectIfGradeIsEmpty() {
        qualification.setQualificationGrade(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualification, "qualification");
        qualificationValidator.validate(qualification, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("qualificationGrade").getCode());
    }

    @Test
    public void shouldRejectIfStartDateIsAfterEndDate() throws ParseException {
        qualification.setQualificationStartDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/08/06"));
        qualification.setQualificationAwardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2009/08/06"));
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualification, "qualification");
        qualificationValidator.validate(qualification, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("qualification.start_date.notvalid", mappingResult.getFieldError("qualificationStartDate").getCode());
    }

    @Test
    public void shouldRejectIfQualificationInstitutionIsLongerThan200Chars() {
        StringBuilder builder = new StringBuilder();
        for (int idx = 0; idx < 250; idx++) {
            builder.append("a");
        }
        qualification.setQualificationInstitution(builder.toString());
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualification, "qualification");
        qualificationValidator.validate(qualification, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("A maximum of 200 characters are allowed.",
                mappingResult.getFieldError("qualificationInstitution").getDefaultMessage());
    }

    @Test
    public void shouldRejectIfQualificationLanguageIsLongerThan70Chars() {
        StringBuilder builder = new StringBuilder();
        for (int idx = 0; idx < 200; idx++) {
            builder.append("a");
        }
        qualification.setQualificationLanguage(builder.toString());
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualification, "qualification");
        qualificationValidator.validate(qualification, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("A maximum of 70 characters are allowed.",
                mappingResult.getFieldError("qualificationLanguage").getDefaultMessage());
    }

    @Test
    public void shouldRejectIfQualificationGradeIsLongerThan70Chars() {
        StringBuilder builder = new StringBuilder();
        for (int idx = 0; idx < 200; idx++) {
            builder.append("a");
        }
        qualification.setQualificationGrade(builder.toString());
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualification, "qualification");
        qualificationValidator.validate(qualification, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("A maximum of 70 characters are allowed.", mappingResult
                .getFieldError("qualificationGrade").getDefaultMessage());
    }
    
    @Test
    public void shouldRejectQualificationAwardDateIfItIsInThePastAndNotCompleted() {
        qualification.setCompleted(CheckedStatus.NO);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualification, "qualification");
        qualificationValidator.validate(qualification, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("date.field.notfuture", mappingResult.getFieldError("qualificationAwardDate").getCode());
    }

    @Before
    public void setup() throws ParseException {
        qualification = new Qualification();
        qualification.setApplication(new ApplicationFormBuilder().id(9).build());
        qualification.setId(3);
        qualification.setQualificationAwardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/09/09"));
        qualification.setQualificationGrade("first");
        qualification.setQualificationInstitution("UCL");
        qualification.setInstitutionCountry(new Domicile());
        qualification.setQualificationLanguage("Abkhazian");
        qualification.setQualificationSubject("CS");
        qualification.setQualificationTitle("MS");
        qualification.setCompleted(CheckedStatus.YES);
        qualification.setQualificationStartDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/08/06"));
        qualification.setQualificationType(new QualificationTypeBuilder().name("degree").build());

        qualificationValidator = new QualificationValidator();
        qualificationValidator.setValidator((javax.validation.Validator) validator);
    }
}
