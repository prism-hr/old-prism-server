package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.AssignSupervisorsComment;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalCommentBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class ApprovalCommentValidatorTest {

    @Autowired
    private Validator validator;

    private AssignSupervisorsComment approvalComment;

    private ApprovalCommentValidator approvalCommentValidator;

    private CommentAssignedUserValidator supervisorsValidator;
    
    @Test
    public void shouldSupportReviewRound() {
        assertTrue(approvalCommentValidator.supports(AssignSupervisorsComment.class));
    }

    @Test
    public void shouldValidateIfDataIsCorrect() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(approvalComment, "approvalRound");
        approvalCommentValidator.validate(approvalComment, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
    }
    
    @Test
    public void shouldValidateIfDataIsCorrectWithoutProjectDescriptionAndConditions() {
        approvalComment.setProjectDescriptionAvailable(false);
        approvalComment.setProjectTitle(null);
        approvalComment.setProjectAbstract(null);
        approvalComment.setRecommendedConditionsAvailable(false);
        approvalComment.setRecommendedConditions(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(approvalComment, "approvalRound");
        approvalCommentValidator.validate(approvalComment, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
    }

    @Test
    public void shouldRejectIfProjectTitleIsEmpty() {
        approvalComment.setProjectTitle("");
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(approvalComment, "approvalRound");
        approvalCommentValidator.validate(approvalComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("projectTitle").getCode());
    }

    @Test
    public void shouldRejectIfProjectAbstractIsEmpty() {
        approvalComment.setProjectAbstract("");
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(approvalComment, "approvalRound");
        approvalCommentValidator.validate(approvalComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("projectAbstract").getCode());
    }
    
    @Test
    public void shouldRejectIfStartDateIsInThePast() {
        approvalComment.setRecommendedStartDate(new LocalDate().minusDays(1));
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(approvalComment, "approvalRound");
        approvalCommentValidator.validate(approvalComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("date.field.notfuture", mappingResult.getFieldError("recommendedStartDate").getCode());
    }

    @Test
    public void shouldRejectIfConditionsTextIsEmpty() {
        approvalComment.setRecommendedConditions("");
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(approvalComment, "approvalRound");
        approvalCommentValidator.validate(approvalComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("recommendedConditions").getCode());
    }

    @Test
    public void shouldRejectIfProjectDescriptionAvailableIsNotSet() {
        approvalComment.setProjectDescriptionAvailable(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(approvalComment, "approvalRound");
        approvalCommentValidator.validate(approvalComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("projectDescriptionAvailable").getCode());
    }

    @Test
    public void shouldRejectIfRecommendedConditionsAvailableIsNotSet() {
        approvalComment.setRecommendedConditionsAvailable(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(approvalComment, "approvalRound");
        approvalCommentValidator.validate(approvalComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("recommendedConditionsAvailable").getCode());
    }
    
    @Test
    public void shouldRejectIfProjectTitleIsLongerThan255Chars() {
        StringBuilder longString = new StringBuilder();
        for (int i = 0; i < 300; i++) {
            longString.append("a");
        }
        approvalComment.setProjectTitle(longString.toString());
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(approvalComment, "approvalRound");
        approvalCommentValidator.validate(approvalComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("A maximum of 255 characters are allowed.", mappingResult.getFieldError("projectTitle").getDefaultMessage());
    }
    
    @Test
    public void shouldRejectIfMissingQualificationExplanationIsLongerThan500Chars() {
        StringBuilder longString = new StringBuilder();
        for (int i = 0; i < 520; i++) {
            longString.append("a");
        }
        approvalComment.setMissingQualificationExplanation(longString.toString());
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(approvalComment, "approvalRound");
        approvalCommentValidator.validate(approvalComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("A maximum of 500 characters are allowed.", mappingResult.getFieldError("missingQualificationExplanation").getDefaultMessage());
    }
    
    @Test
    public void shouldRejectIfrecommendedConditionsIsLongerThan1000Chars() {
        StringBuilder longString = new StringBuilder();
        for (int i = 0; i < 1010; i++) {
            longString.append("a");
        }
        approvalComment.setRecommendedConditions(longString.toString());
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(approvalComment, "approvalRound");
        approvalCommentValidator.validate(approvalComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("A maximum of 1000 characters are allowed.", mappingResult.getFieldError("recommendedConditions").getDefaultMessage());
    }    

    @Before
    public void setup() {
//        Supervisor supervisor1 = new SupervisorBuilder().id(4).build();
//        Supervisor supervisor2 = new SupervisorBuilder().id(5).isPrimary(true).build();
        Application application = new ApplicationFormBuilder().id(2).build();
        approvalComment = new ApprovalCommentBuilder() //
                .application(application)//
//                .supervisors(supervisor1, supervisor2)//
                .projectDescriptionAvailable(true)//
                .projectTitle("title")//
                .projectAbstract("abstract")//
                .recommendedStartDate(new LocalDate().plusDays(1))//
                .recommendedConditionsAvailable(true)//
                .recommendedConditions("conditions")//
                .build();

        approvalCommentValidator = new ApprovalCommentValidator();
        approvalCommentValidator.setValidator((javax.validation.Validator) validator);

        supervisorsValidator = new CommentAssignedUserValidator();
        approvalCommentValidator.setSupervisorsValidator(supervisorsValidator);
    }
    
}
