package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.StateChangeComment;
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.builders.ValidationCommentBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.HomeOrOverseas;
import com.zuehlke.pgadmissions.domain.enums.ValidationQuestionOptions;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class StateChangeValidatorTest {
    
    @Autowired  
    private Validator validator;  
    
	private StateChangeValidator stateChangeValidator;

    private ValidationComment validationComment;
	
    private StateChangeComment stateChangeComment;

	@Test
	public void shouldSupportComment() {
		assertTrue(stateChangeValidator.supports(ValidationComment.class));
		assertTrue(stateChangeValidator.supports(StateChangeComment.class));
	}

	@Test
	public void shouldRejectIfNoCommentInValidationComment() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(validationComment, "comment");
		validationComment.setComment("");
		validationComment.setConfirmNextStage(true);
		stateChangeValidator.validate(validationComment, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("comment").getCode());
	}
	
	@Test
	public void shouldRejectIfNoEnglishCompetencyInValidationComment() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(validationComment, "englishCompentencyOk");
		validationComment.setEnglishCompentencyOk(null);
        validationComment.setConfirmNextStage(true);
		stateChangeValidator.validate(validationComment, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("englishCompentencyOk").getCode());
	}
	
	@Test
	public void shouldRejectIfNoHomeOrOverseasInValidationComment() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(validationComment, "homeOrOverseas");
		validationComment.setHomeOrOverseas(null);
        validationComment.setConfirmNextStage(true);
		stateChangeValidator.validate(validationComment, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("homeOrOverseas").getCode());
	}
	
	@Test
	public void shouldRejectIfNoQualifiedForPhdInValidationComment() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(validationComment, "qualifiedForPhd");
		validationComment.setQualifiedForPhd(null);
        validationComment.setConfirmNextStage(true);
		stateChangeValidator.validate(validationComment, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("qualifiedForPhd").getCode());
	}
	
	@Test
	public void shouldRejectIfNoNextStatusInValidationComment() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(validationComment, "nextStatus");
		validationComment.setNextStatus(null);
        validationComment.setConfirmNextStage(true);
		stateChangeValidator.validate(validationComment, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("nextStatus").getCode());
	}
	
	@Test
	public void shouldRejectIfNoCommentInStateChangeComment() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(stateChangeComment, "comment");
		stateChangeComment.setComment("");
		stateChangeComment.setConfirmNextStage(true);
		stateChangeValidator.validate(stateChangeComment, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("comment").getCode());
	}
	
	@Test
	public void shouldRejectIfNoNextStatusInStateChangeComment() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(stateChangeComment, "nextStatus");
		stateChangeComment.setNextStatus(null);
        stateChangeComment.setConfirmNextStage(true);
		stateChangeValidator.validate(stateChangeComment, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("nextStatus").getCode());
	}
	
	@Test
	public void shouldRejectIfNoConfirmationFieldWasSubmitted() {
	    DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(stateChangeComment, "confirmNextStage");
	    stateChangeComment.setConfirmNextStage(null);
	    stateChangeValidator.validate(stateChangeComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("checkbox.mandatory", mappingResult.getFieldError("confirmNextStage").getCode());
	}
	
	@Test
    public void shouldRejectIfNoConfirmationWasSelected() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(stateChangeComment, "confirmNextStage");
        stateChangeComment.setConfirmNextStage(false);
        stateChangeValidator.validate(stateChangeComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("checkbox.mandatory", mappingResult.getFieldError("confirmNextStage").getCode());
    }
	
	@Before
	public void setup() {
		validationComment = new ValidationCommentBuilder().comment("validation comment").nextStatus(ApplicationFormStatus.APPROVAL).englishCompentencyOk(ValidationQuestionOptions.YES).qualifiedForPhd(ValidationQuestionOptions.YES).homeOrOverseas(HomeOrOverseas.HOME).build();
		stateChangeComment = new StateChangeComment();
		stateChangeComment.setComment("comment");
		stateChangeComment.setNextStatus(ApplicationFormStatus.APPROVAL);
		stateChangeValidator = new StateChangeValidator();
		stateChangeValidator.setValidator((javax.validation.Validator) validator);
	}
}
