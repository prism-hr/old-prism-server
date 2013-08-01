package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Collections;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class ApprovalRoundValidatorTest {

    @Autowired
    private Validator validator;

    private ApprovalRound approvalRound;

    private ApprovalRoundValidator approvalRoundValidator;

    @Test
    public void shouldSupportReviewRound() {
        assertTrue(approvalRoundValidator.supports(ApprovalRound.class));
    }

    @Test
    public void shouldValidateIfDataIsCorrect() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(approvalRound, "approvalRound");
        approvalRoundValidator.validate(approvalRound, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
    }
    
    @Test
    public void shouldValidateIfDataIsCorrectWithoutProjectDescriptionAndConditions() {
        approvalRound.setProjectDescriptionAvailable(false);
        approvalRound.setProjectTitle(null);
        approvalRound.setProjectAbstract(null);
        approvalRound.setRecommendedConditionsAvailable(false);
        approvalRound.setRecommendedConditions(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(approvalRound, "approvalRound");
        approvalRoundValidator.validate(approvalRound, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
    }

    @Test
    public void shouldRejectIfSupervisorListIsEmpty() {
        approvalRound.getSupervisors().clear();
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(approvalRound, "approvalRound");
        approvalRoundValidator.validate(approvalRound, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("approvalround.supervisors.incomplete", mappingResult.getFieldError("supervisors").getCode());
    }

    @Test
    public void shouldRejectIfOnlyOneSupervisor() {
        Supervisor supervisor = new SupervisorBuilder().id(4).build();

        approvalRound.setSupervisors(Collections.singletonList(supervisor));
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(approvalRound, "approvalRound");
        approvalRoundValidator.validate(approvalRound, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("approvalround.supervisors.incomplete", mappingResult.getFieldError("supervisors").getCode());
    }

    @Test
    public void shouldRejectIfNoSupervisorSetAsPrimary() {
        Supervisor supervisor2 = approvalRound.getSupervisors().get(1);
        supervisor2.setIsPrimary(false);

        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(approvalRound, "approvalRound");
        approvalRoundValidator.validate(approvalRound, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("approvalround.supervisors.noprimary", mappingResult.getFieldError("supervisors").getCode());
    }

    @Test
    public void shouldRejectIfProjectTitleIsEmpty() {
        approvalRound.setProjectTitle("");
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(approvalRound, "approvalRound");
        approvalRoundValidator.validate(approvalRound, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("projectTitle").getCode());
    }

    @Test
    public void shouldRejectIfProjectAbstractIsEmpty() {
        approvalRound.setProjectAbstract("");
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(approvalRound, "approvalRound");
        approvalRoundValidator.validate(approvalRound, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("projectAbstract").getCode());
    }
    
    @Test
    public void shouldRejectIfStartDateIsInThePast() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);

        approvalRound.setRecommendedStartDate(calendar.getTime());
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(approvalRound, "approvalRound");
        approvalRoundValidator.validate(approvalRound, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("date.field.notfuture", mappingResult.getFieldError("recommendedStartDate").getCode());
    }

    @Test
    public void shouldRejectIfConditionsTextIsEmpty() {
        approvalRound.setRecommendedConditions("");
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(approvalRound, "approvalRound");
        approvalRoundValidator.validate(approvalRound, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("recommendedConditions").getCode());
    }

    @Test
    public void shouldRejectIfProjectDescriptionAvailableIsNotSet() {
        approvalRound.setProjectDescriptionAvailable(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(approvalRound, "approvalRound");
        approvalRoundValidator.validate(approvalRound, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("projectDescriptionAvailable").getCode());
    }

    @Test
    public void shouldRejectIfRecommendedConditionsAvailableIsNotSet() {
        approvalRound.setRecommendedConditionsAvailable(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(approvalRound, "approvalRound");
        approvalRoundValidator.validate(approvalRound, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("recommendedConditionsAvailable").getCode());
    }
    
    @Test
    public void shouldRejectIfProjectTitleIsLongerThan255Chars() {
        StringBuilder longString = new StringBuilder();
        for (int i = 0; i < 300; i++) {
            longString.append("a");
        }
        approvalRound.setProjectTitle(longString.toString());
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(approvalRound, "approvalRound");
        approvalRoundValidator.validate(approvalRound, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("A maximum of 255 characters are allowed.", mappingResult.getFieldError("projectTitle").getDefaultMessage());
    }
    
    @Test
    public void shouldRejectIfMissingQualificationExplanationIsLongerThan500Chars() {
        StringBuilder longString = new StringBuilder();
        for (int i = 0; i < 520; i++) {
            longString.append("a");
        }
        approvalRound.setMissingQualificationExplanation(longString.toString());
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(approvalRound, "approvalRound");
        approvalRoundValidator.validate(approvalRound, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("A maximum of 500 characters are allowed.", mappingResult.getFieldError("missingQualificationExplanation").getDefaultMessage());
    }
    
    @Test
    public void shouldRejectIfrecommendedConditionsIsLongerThan1000Chars() {
        StringBuilder longString = new StringBuilder();
        for (int i = 0; i < 1010; i++) {
            longString.append("a");
        }
        approvalRound.setRecommendedConditions(longString.toString());
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(approvalRound, "approvalRound");
        approvalRoundValidator.validate(approvalRound, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("A maximum of 1000 characters are allowed.", mappingResult.getFieldError("recommendedConditions").getDefaultMessage());
    }    

    @Before
    public void setup() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);

        Supervisor supervisor1 = new SupervisorBuilder().id(4).build();
        Supervisor supervisor2 = new SupervisorBuilder().id(5).isPrimary(true).build();
        ApplicationForm application = new ApplicationFormBuilder().id(2).build();
        approvalRound = new ApprovalRoundBuilder() //
                .application(application)//
                .supervisors(supervisor1, supervisor2)//
                .projectDescriptionAvailable(true)//
                .projectTitle("title")//
                .projectAbstract("abstract")//
                .recommendedStartDate(calendar.getTime())//
                .recommendedConditionsAvailable(true)//
                .recommendedConditions("conditions")//
                .build();

        approvalRoundValidator = new ApprovalRoundValidator();
        approvalRoundValidator.setValidator((javax.validation.Validator) validator);
    }
    
}
