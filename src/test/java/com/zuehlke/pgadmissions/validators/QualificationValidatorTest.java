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

import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationTypeBuilder;

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
    public void shouldRejectIfSubjectIsEmpty() {
        qualification.setSubject(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualification, "qualification");
        qualificationValidator.validate(qualification, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("qualificationSubject").getCode());
    }

    @Test
    public void shouldRejectIfStartDateIsEmpty() {
        qualification.setStartDate(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualification, "qualification");
        qualificationValidator.validate(qualification, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("qualificationStartDate").getCode());
    }
    
    @Test
    public void shouldRejectIfAwardDateIsEmpty() {
        qualification.setAwardDate(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualification, "qualification");
        qualificationValidator.validate(qualification, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("qualificationAwardDate").getCode());
    }

    @Test
    public void shouldRejectIfLanguageIsEmpty() {
        qualification.setLanguage(null);
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
        qualification.setStartDate(tomorrow);
        qualification.setAwardDate(dayAfterTomorrow);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualification, "qualification");
        qualificationValidator.validate(qualification, mappingResult);
        Assert.assertEquals(2, mappingResult.getErrorCount());
        Assert.assertEquals("date.field.notpast", mappingResult.getFieldError("qualificationStartDate").getCode());
        Assert.assertEquals("date.field.notpast", mappingResult.getFieldError("qualificationAwardDate").getCode());
    }
    
    @Test
    public void shouldRejectIfStartDateIsInFutureAndAfterAwardDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        Date tomorrow = calendar.getTime();
        calendar.add(Calendar.DATE, -1);
        Date yesterday = calendar.getTime();
        qualification.setStartDate(tomorrow);
        qualification.setAwardDate(yesterday);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualification, "qualification");
        qualificationValidator.validate(qualification, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("date.field.notpast", mappingResult.getFieldError("qualificationStartDate").getCode());
    }

    @Test
    public void shouldRejectIfTypeIsEmpty() {
        qualification.setType(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualification, "qualification");
        qualificationValidator.validate(qualification, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("qualificationType").getCode());
    }

    @Test
    public void shouldRejectIfGradeIsEmpty() {
        qualification.setGrade(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualification, "qualification");
        qualificationValidator.validate(qualification, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("qualificationGrade").getCode());
    }

    @Test
    public void shouldRejectIfStartDateIsAfterEndDate() throws ParseException {
        qualification.setStartDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/08/06"));
        qualification.setAwardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2009/08/06"));
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualification, "qualification");
        qualificationValidator.validate(qualification, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("qualification.start_date.notvalid", mappingResult.getFieldError("qualificationStartDate").getCode());
    }


    @Test
    public void shouldRejectIfQualificationLanguageIsLongerThan70Chars() {
        StringBuilder builder = new StringBuilder();
        for (int idx = 0; idx < 200; idx++) {
            builder.append("a");
        }
        qualification.setLanguage(builder.toString());
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
        qualification.setGrade(builder.toString());
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualification, "qualification");
        qualificationValidator.validate(qualification, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("A maximum of 70 characters are allowed.", mappingResult
                .getFieldError("qualificationGrade").getDefaultMessage());
    }
    
    @Test
    public void shouldRejectQualificationAwardDateIfItIsInThePastAndNotCompleted() {
        qualification.setCompleted(false);
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
        qualification.setAwardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/09/09"));
        qualification.setGrade("first");
        qualification.setLanguage("Abkhazian");
        qualification.setSubject("CS");
        qualification.setTitle("MS");
        qualification.setCompleted(true);
        qualification.setStartDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/08/06"));
        qualification.setType(new QualificationTypeBuilder().name("degree").build());

        qualificationValidator = new QualificationValidator();
        qualificationValidator.setValidator((javax.validation.Validator) validator);
    }
}
