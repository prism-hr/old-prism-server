package com.zuehlke.pgadmissions.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.zuehlke.pgadmissions.domain.InterviewParticipant;

@Component
public class ParticipantValidator extends AbstractValidator {
    
    protected static final String INTERVIEW_VOTE_NO_OPTION_SELECTED = "interviewVote.nooptionselected";

    @Override
    public boolean supports(Class<?> clazz) {
        return InterviewParticipant.class.equals(clazz);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {
        InterviewParticipant interviewParticipant = (InterviewParticipant) target;
        
        if (!interviewParticipant.getCanMakeIt()) {
            return;
        }
        
        if(interviewParticipant.getAcceptedTimeslots().isEmpty()){
            errors.rejectValue("canMakeIt", INTERVIEW_VOTE_NO_OPTION_SELECTED);
        }
    }

}
