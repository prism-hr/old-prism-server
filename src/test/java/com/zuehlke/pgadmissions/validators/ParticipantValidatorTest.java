package com.zuehlke.pgadmissions.validators;

import java.util.HashSet;
import java.util.Set;
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
import com.zuehlke.pgadmissions.domain.builders.InterviewTimeslotBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class ParticipantValidatorTest {

    @Autowired
    private Validator validator;

    private InterviewParticipant interviewParticipant;

    private ParticipantValidator participantValidator;

    @Test
    public void shouldRejectIfNoTimeSlotsSelected() {
        interviewParticipant.getAcceptedTimeslots().clear();
        interviewParticipant.setCantMakeIt(false);
        
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interviewParticipant, "cantMakeIt");
        participantValidator.validate(interviewParticipant, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("interviewVote.nooptionselected", mappingResult.getFieldError("cantMakeIt").getCode());
    }
    
    @Test
    public void shouldRejectIfNoTimeSlotsAndNoCantMakeItSelected() {
        interviewParticipant.getAcceptedTimeslots().clear();
        interviewParticipant.setCantMakeIt(false);
        
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interviewParticipant, "cantMakeIt");
        participantValidator.validate(interviewParticipant, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("interviewVote.nooptionselected", mappingResult.getFieldError("cantMakeIt").getCode());
    }

    @Before
    public void setup() {
        Set<InterviewTimeslot> acceptedTimeslots = new HashSet<InterviewTimeslot>();
        
        acceptedTimeslots.add(new InterviewTimeslotBuilder().build());
        acceptedTimeslots.add(new InterviewTimeslotBuilder().build());
        
        interviewParticipant = new InterviewParticipantBuilder().acceptedTimeslots(acceptedTimeslots).build();
        interviewParticipant.setResponded(true);
        participantValidator = new ParticipantValidator();
        participantValidator.setValidator((javax.validation.Validator) validator);
    }
}
