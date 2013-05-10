package com.zuehlke.pgadmissions.validators;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewParticipant;
import com.zuehlke.pgadmissions.domain.InterviewTimeslot;
import com.zuehlke.pgadmissions.domain.enums.InterviewStage;

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
        
        if (!interviewParticipant.getResponded()) {
            errors.rejectValue("responded", INTERVIEW_VOTE_NO_OPTION_SELECTED);
        }
    }

}
