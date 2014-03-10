package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

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
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.HomeOrOverseas;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;
import com.zuehlke.pgadmissions.domain.enums.ValidationQuestionOptions;
import com.zuehlke.pgadmissions.dto.StateChangeDTO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class StateChangeValidatorTest {
    
    @Autowired  
    private Validator validator;  
    
	private StateChangeValidator stateChangeValidator;

    private StateChangeDTO stateChangeDTO;
	
	@Test
	public void shouldSupportComment() {
		assertTrue(stateChangeValidator.supports(StateChangeDTO.class));
	}

	@Test
	public void shouldRejectIfNoCommentInValidationComment() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(stateChangeDTO, "stateChangeDTO");
		stateChangeDTO.setComment("");
		stateChangeDTO.setConfirmNextStage(true);
        Program program = new ProgramBuilder().build();
        ApplicationForm application = new ApplicationFormBuilder().id(1).advert(program).build();
        stateChangeDTO.setApplicationForm(application);
		stateChangeValidator.validate(stateChangeDTO, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("comment").getCode());
	}
	
	@Test
	public void shouldRejectIfNoEnglishCompetencyInValidationComment() {
	    DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(stateChangeDTO, "stateChangeDTO");
		stateChangeDTO.setEnglishCompentencyOk(null);
        stateChangeDTO.setConfirmNextStage(true);
        Program program = new ProgramBuilder().build();
        ApplicationForm application = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.VALIDATION).advert(program).build();
        stateChangeDTO.setApplicationForm(application);
		stateChangeValidator.validate(stateChangeDTO, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("englishCompentencyOk").getCode());
	}
	
	@Test
	public void shouldRejectIfNoHomeOrOverseasInValidationComment() {
	    DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(stateChangeDTO, "stateChangeDTO");
		stateChangeDTO.setHomeOrOverseas(null);
        stateChangeDTO.setConfirmNextStage(true);
        Program program = new ProgramBuilder().build();
        ApplicationForm application = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.VALIDATION).advert(program).build();
        stateChangeDTO.setApplicationForm(application);
		stateChangeValidator.validate(stateChangeDTO, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("homeOrOverseas").getCode());
	}
	
	@Test
	public void shouldRejectIfNoQualifiedForPhdInValidationComment() {
	    DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(stateChangeDTO, "stateChangeDTO");
		stateChangeDTO.setQualifiedForPhd(null);
        stateChangeDTO.setConfirmNextStage(true);
        Program program = new ProgramBuilder().build();
        ApplicationForm application = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.VALIDATION).advert(program).build();
        stateChangeDTO.setApplicationForm(application);
		stateChangeValidator.validate(stateChangeDTO, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("qualifiedForPhd").getCode());
	}
	
	@Test
	public void shouldRejectIfNoNextStatusInValidationComment() {
	    DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(stateChangeDTO, "stateChangeDTO");
		stateChangeDTO.setNextStatus(null);
        stateChangeDTO.setConfirmNextStage(true);
        Program program = new ProgramBuilder().build();
        ApplicationForm application = new ApplicationFormBuilder().id(1).advert(program).build();
        stateChangeDTO.setApplicationForm(application);
		stateChangeValidator.validate(stateChangeDTO, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("nextStatus").getCode());
	}
	
	@Test
	public void shouldRejectIfNoCommentInStateChangeComment() {
	    DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(stateChangeDTO, "stateChangeDTO");
		stateChangeDTO.setComment("");
		stateChangeDTO.setConfirmNextStage(true);
        Program program = new ProgramBuilder().build();
        ApplicationForm application = new ApplicationFormBuilder().id(1).advert(program).build();
        stateChangeDTO.setApplicationForm(application);
		stateChangeValidator.validate(stateChangeDTO, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("comment").getCode());
	}
	
	@Test
	public void shouldRejectIfNoNextStatusInStateChangeComment() {
	    DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(stateChangeDTO, "stateChangeDTO");
		stateChangeDTO.setNextStatus(null);
        stateChangeDTO.setConfirmNextStage(true);
        Program program = new ProgramBuilder().build();
        ApplicationForm application = new ApplicationFormBuilder().id(1).advert(program).build();
        stateChangeDTO.setApplicationForm(application);
		stateChangeValidator.validate(stateChangeDTO, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("nextStatus").getCode());
	}
	
	@Test
	public void shouldRejectIfNoConfirmationFieldWasSubmitted() {
	    DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(stateChangeDTO, "stateChangeDTO");
	    stateChangeDTO.setConfirmNextStage(null);
        Program program = new ProgramBuilder().build();
        ApplicationForm application = new ApplicationFormBuilder().id(1).advert(program).build();
        stateChangeDTO.setApplicationForm(application);
	    stateChangeValidator.validate(stateChangeDTO, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("checkbox.mandatory", mappingResult.getFieldError("confirmNextStage").getCode());
	}
	
	@Test
    public void shouldRejectIfNoConfirmationWasSelected() {
	    DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(stateChangeDTO, "stateChangeDTO");
        stateChangeDTO.setConfirmNextStage(false);
        Program program = new ProgramBuilder().build();
        ApplicationForm application = new ApplicationFormBuilder().id(1).advert(program).build();
        stateChangeDTO.setApplicationForm(application);
        stateChangeValidator.validate(stateChangeDTO, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("checkbox.mandatory", mappingResult.getFieldError("confirmNextStage").getCode());
    }
	
	@Test
	public void shouldRejectIfRequiredCustomQuestionOptionsNotCompleted() {
	    DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(stateChangeDTO, "stateChangeDTO");
	    Map<ScoringStage, ScoringDefinition> scoringDefinitions = new HashMap<ScoringStage, ScoringDefinition>();
	    for (ScoringStage stage : ScoringStage.values()) {
	        scoringDefinitions.put(stage, new ScoringDefinition());
	    }
	    Program program = new ProgramBuilder().scoringDefinitions(scoringDefinitions).build();
        ApplicationForm application = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).advert(program).build();
        stateChangeDTO.setApplicationForm(application);
	    stateChangeDTO.setNextStatus(ApplicationFormStatus.REVIEW);
	    stateChangeDTO.setConfirmNextStage(true);
        stateChangeValidator.validate(stateChangeDTO, mappingResult);
	    Assert.assertEquals(2, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("useCustomReferenceQuestions").getCode());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("useCustomQuestions").getCode());
        stateChangeDTO.setNextStatus(ApplicationFormStatus.INTERVIEW);
        mappingResult = new DirectFieldBindingResult(stateChangeDTO, "stateChangeDTO");
        stateChangeValidator.validate(stateChangeDTO, mappingResult);
        Assert.assertEquals(2, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("useCustomReferenceQuestions").getCode());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("useCustomQuestions").getCode());
	}
	
	@Test
	public void shouldNotRejectOnAccountOfMissingScoresIfThereAreNoCustomQuestions() {
	    DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(stateChangeDTO, "stateChangeDTO");
        Map<ScoringStage, ScoringDefinition> scoringDefinitions = new HashMap<ScoringStage, ScoringDefinition>();
        Program program = new ProgramBuilder().scoringDefinitions(scoringDefinitions).build();
        ApplicationForm application = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).advert(program).build();
        stateChangeDTO.setApplicationForm(application);
        stateChangeDTO.setNextStatus(ApplicationFormStatus.REVIEW);
        stateChangeDTO.setConfirmNextStage(true);
        stateChangeDTO.setUseCustomReferenceQuestions(false);
        stateChangeDTO.setUseCustomQuestions(false);
        stateChangeValidator.validate(stateChangeDTO, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
        stateChangeDTO.setNextStatus(ApplicationFormStatus.INTERVIEW);
        mappingResult = new DirectFieldBindingResult(stateChangeDTO, "stateChangeDTO");
        stateChangeValidator.validate(stateChangeDTO, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
	}
	
	@Before
	public void setup() {
		stateChangeDTO = new StateChangeDTO();
		stateChangeDTO.setComment("Validation comment");
		stateChangeDTO.setNextStatus(ApplicationFormStatus.APPROVAL);
		stateChangeDTO.setEnglishCompentencyOk(ValidationQuestionOptions.YES);
		stateChangeDTO.setQualifiedForPhd(ValidationQuestionOptions.NO);
		stateChangeDTO.setHomeOrOverseas(HomeOrOverseas.HOME);
		stateChangeDTO.setRegisteredUser(new RegisteredUserBuilder().build());
		stateChangeValidator = new StateChangeValidator();
		stateChangeValidator.setValidator((javax.validation.Validator) validator);
	}
	
}
