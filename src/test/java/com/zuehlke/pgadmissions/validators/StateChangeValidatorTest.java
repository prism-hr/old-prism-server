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
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.enums.HomeOrOverseas;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
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
        Program program = new Program();
        ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).build();
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
        Program program = new Program();
        ApplicationForm application = new ApplicationFormBuilder().id(1).status(new State().withId(PrismState.APPLICATION_VALIDATION)).program(program).build();
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
        Program program = new Program();
        ApplicationForm application = new ApplicationFormBuilder().id(1).status(new State().withId(PrismState.APPLICATION_VALIDATION)).program(program).build();
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
        Program program = new Program();
        ApplicationForm application = new ApplicationFormBuilder().id(1).status(new State().withId(PrismState.APPLICATION_VALIDATION)).program(program).build();
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
        Program program = new Program();
        ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).build();
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
        Program program = new Program();
        ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).build();
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
        Program program = new Program();
        ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).build();
        stateChangeDTO.setApplicationForm(application);
		stateChangeValidator.validate(stateChangeDTO, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("nextStatus").getCode());
	}
	
	@Test
	public void shouldRejectIfNoConfirmationFieldWasSubmitted() {
	    DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(stateChangeDTO, "stateChangeDTO");
	    stateChangeDTO.setConfirmNextStage(null);
        Program program = new Program();
        ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).build();
        stateChangeDTO.setApplicationForm(application);
	    stateChangeValidator.validate(stateChangeDTO, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("checkbox.mandatory", mappingResult.getFieldError("confirmNextStage").getCode());
	}
	
	@Test
    public void shouldRejectIfNoConfirmationWasSelected() {
	    DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(stateChangeDTO, "stateChangeDTO");
        stateChangeDTO.setConfirmNextStage(false);
        Program program = new Program();
        ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).build();
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
	    Program program = new Program().withScoringDefinitions(scoringDefinitions);
        ApplicationForm application = new ApplicationFormBuilder().status(new State().withId(PrismState.APPLICATION_VALIDATION)).program(program).build();
        stateChangeDTO.setApplicationForm(application);
	    stateChangeDTO.setNextStatus(PrismState.APPLICATION_REVIEW);
	    stateChangeDTO.setConfirmNextStage(true);
        stateChangeValidator.validate(stateChangeDTO, mappingResult);
	    Assert.assertEquals(2, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("useCustomReferenceQuestions").getCode());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("useCustomQuestions").getCode());
        stateChangeDTO.setNextStatus(PrismState.APPLICATION_INTERVIEW);
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
        Program program = new Program().withScoringDefinitions(scoringDefinitions);
        ApplicationForm application = new ApplicationFormBuilder().status(new State().withId(PrismState.APPLICATION_VALIDATION)).program(program).build();
        stateChangeDTO.setApplicationForm(application);
        stateChangeDTO.setNextStatus(PrismState.APPLICATION_REVIEW);
        stateChangeDTO.setConfirmNextStage(true);
        stateChangeDTO.setUseCustomReferenceQuestions(false);
        stateChangeDTO.setUseCustomQuestions(false);
        stateChangeValidator.validate(stateChangeDTO, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
        stateChangeDTO.setNextStatus(PrismState.APPLICATION_INTERVIEW);
        mappingResult = new DirectFieldBindingResult(stateChangeDTO, "stateChangeDTO");
        stateChangeValidator.validate(stateChangeDTO, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
	}
	
	@Before
	public void setup() {
		stateChangeDTO = new StateChangeDTO();
		stateChangeDTO.setComment("Validation comment");
		stateChangeDTO.setNextStatus(PrismState.APPLICATION_APPROVAL);
		stateChangeDTO.setEnglishCompentencyOk(ValidationQuestionOptions.YES);
		stateChangeDTO.setQualifiedForPhd(ValidationQuestionOptions.NO);
		stateChangeDTO.setHomeOrOverseas(HomeOrOverseas.HOME);
		stateChangeDTO.setUser(new User());
		stateChangeValidator = new StateChangeValidator();
		stateChangeValidator.setValidator((javax.validation.Validator) validator);
	}
	
}
