package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewParticipantBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

public class InterviewTest {

    @Test
    public void shouldGetCorrectValuesForTimeParts() {
        String time = "16:45";
        Interview interview = new InterviewBuilder().interviewTime(time).build();
        assertEquals("16", interview.getTimeHours());
        assertEquals("45", interview.getTimeMinutes());

    }

    @Test
    public void shouldReturnNullsIfTimeIsNull() {
        Interview interview = new InterviewBuilder().build();
        assertNull(interview.getTimeHours());
        assertNull(interview.getTimeMinutes());
    }

    @Test
    public void shouldReturnTrueIfUserIsParticipant() {
        RegisteredUser user1 = new RegisteredUserBuilder().id(1).build();
        RegisteredUser user2 = new RegisteredUserBuilder().id(2).build();
        RegisteredUser user3 = new RegisteredUserBuilder().id(3).build();

        InterviewParticipant participant1 = new InterviewParticipantBuilder().user(user1).build();
        InterviewParticipant participant2 = new InterviewParticipantBuilder().user(user2).build();

        Interview interview = new InterviewBuilder().participants(participant1, participant2).build();
        assertTrue(interview.isParticipant(user1));
        assertFalse(interview.isParticipant(user3));
    }

    @Test
    public void shouldReturnUserIfParticipant() {
        RegisteredUser user1 = new RegisteredUserBuilder().id(1).build();
        RegisteredUser user2 = new RegisteredUserBuilder().id(2).build();
        RegisteredUser user3 = new RegisteredUserBuilder().id(3).build();

        InterviewParticipant participant1 = new InterviewParticipantBuilder().user(user1).build();
        InterviewParticipant participant2 = new InterviewParticipantBuilder().user(user2).build();

        Interview interview = new InterviewBuilder().participants(participant1, participant2).build();
        assertEquals(participant1, interview.getParticipant(user1));
        assertNull(interview.getParticipant(user3));
    }

    @Test
    public void shouldReturnTrueIfAllInterviewerHasProvidedFeedback() {
        Interviewer interviewer1 = new InterviewerBuilder().interviewComment(new InterviewComment()).build();
        Interviewer interviewer2 = new InterviewerBuilder().interviewComment(new InterviewComment()).build();

        Interview interview = new InterviewBuilder().interviewers(interviewer1, interviewer2).build();
        assertTrue(interview.hasAllInterviewersProvidedFeedback());
    }
    
    @Test
    public void shouldReturnFalseIfOneInterviewerHasNotProvidedFeedback() {
        Interviewer interviewer1 = new InterviewerBuilder().interviewComment(new InterviewComment()).build();
        Interviewer interviewer2 = new InterviewerBuilder().interviewComment(null).build();

        Interview interview = new InterviewBuilder().interviewers(interviewer1, interviewer2).build();
        assertFalse(interview.hasAllInterviewersProvidedFeedback());
    }

    @Test
    public void shouldReturnTrueIfAllParticipantsHasResponded() {
        InterviewParticipant participant1 = new InterviewParticipantBuilder().responded(true).build();
        InterviewParticipant participant2 = new InterviewParticipantBuilder().responded(true).build();

        Interview interview = new InterviewBuilder().participants(participant1, participant2).build();
        assertTrue(interview.hasAllParticipantsProvidedAvailability());
    }
    
    @Test
    public void shouldReturnFalseIfAtLeastOneParticipantHasNotResponded() {
        InterviewParticipant participant1 = new InterviewParticipantBuilder().responded(true).build();
        InterviewParticipant participant2 = new InterviewParticipantBuilder().responded(false).build();

        Interview interview = new InterviewBuilder().participants(participant1, participant2).build();
        assertFalse(interview.hasAllParticipantsProvidedAvailability());
    }
    
    @Test
    public void shouldExpiredReturnTrueIfDueDateWasYesterday() {
        Date today = DateUtils.truncate(new Date(), Calendar.DATE);
        Date yesterday = DateUtils.addDays(today, -1);
        
        Interview interview = new InterviewBuilder().dueDate(yesterday).build();
        assertTrue(interview.isDateExpired());
    }
    
    @Test
    public void shouldExpiredReturnTrueIfDueDateIsToday() {
        Date today = DateUtils.truncate(new Date(), Calendar.DATE);
        
        Interview interview = new InterviewBuilder().dueDate(today).build();
        assertTrue(interview.isDateExpired());
    }
    @Test
    public void shouldExpiredReturnFalseIfDueDateIsTomorrow() {
        Date today = DateUtils.truncate(new Date(), Calendar.DATE);
        Date tomorrow = DateUtils.addDays(today, 1);
        
        Interview interview = new InterviewBuilder().dueDate(tomorrow).build();
        assertFalse(interview.isDateExpired());
    }
    
    
}
