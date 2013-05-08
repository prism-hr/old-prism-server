package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.*;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewParticipantBuilder;
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

}
