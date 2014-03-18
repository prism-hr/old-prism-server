package com.zuehlke.pgadmissions.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.InterviewScheduleComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

public class CommentFactoryTest {

    private CommentFactory commentFactory;
    private ApplicationForm applicationForm;
    private RegisteredUser user;

    @Before
    public void prepare() {
        commentFactory = new CommentFactory();
        applicationForm = new ApplicationFormBuilder().id(1).build();
        user = new RegisteredUserBuilder().id(8).build();
    }

    @Test
    public void shouldCreateInterviewScheduleComment() {
        InterviewScheduleComment comment = commentFactory.createInterviewScheduleComment(user, applicationForm, "applicant!", "interviewer!", "loc");
        assertSame(applicationForm, comment.getApplication());
        assertEquals("", comment.getComment());
        assertEquals("applicant!", comment.getFurtherDetails());
        assertEquals("interviewer!", comment.getFurtherInterviewerDetails());
        assertEquals("loc", comment.getLocationUrl());
        assertEquals(CommentType.INTERVIEW_SCHEDULE, comment.getType());
        assertSame(user, comment.getUser());

    }

}
