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

import com.zuehlke.pgadmissions.domain.InterviewParticipant;
import com.zuehlke.pgadmissions.domain.InterviewTimeslot;
import com.zuehlke.pgadmissions.domain.builders.InterviewParticipantBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class InterviewParticipantValidatorTest {

    @Autowired
    private Validator validator;

    private InterviewParticipant interviewParticipant;

    private InterviewParticipantValidator interviewParticipantValidator;

    @Test
    public void shouldSupportParticipant() {
        assertTrue(interviewParticipantValidator.supports(InterviewParticipant.class));
    }

    @Test
    public void shouldRejectIfCanMakeItAndNoTimeSlotsSelected() {
        interviewParticipant.setCanMakeIt(true);
        interviewParticipant.getAcceptedTimeslots().clear();

        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interviewParticipant, "interviewParticipant");
        interviewParticipantValidator.validate(interviewParticipant, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("interviewVote.nooptionselected", mappingResult.getFieldError("canMakeIt").getCode());
    }

    @Test
    public void shouldAcceptIfCantMakeIt() {
        interviewParticipant.setCanMakeIt(false);

        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interviewParticipant, "interviewParticipant");
        interviewParticipantValidator.validate(interviewParticipant, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
    }

    @Test
    public void shouldAcceptIfCanMakeItAndTimeSlotsSelected() {
        interviewParticipant.setCanMakeIt(true);

        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interviewParticipant, "interviewParticipant");
        interviewParticipantValidator.validate(interviewParticipant, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
    }

    @Before
    public void setup() {

        interviewParticipant = new InterviewParticipantBuilder().acceptedTimeslots(new InterviewTimeslot(), new InterviewTimeslot()).responded(true).build();
        interviewParticipantValidator = new InterviewParticipantValidator();
        interviewParticipantValidator.setValidator((javax.validation.Validator) validator);
    }
}
