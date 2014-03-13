package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

public class InterviewerMappingTest extends AutomaticRollbackTestCase {

    private ApplicationForm applicationForm;
    private RegisteredUser interviewerUser;

    @Test
    public void shouldSaveAndLoadInterviewer() throws ParseException {
        Interviewer interviewer = new InterviewerBuilder().user(interviewerUser).build();
        save(interviewer);
        assertNotNull(interviewer.getId());
        Interviewer reloadedInterviewer = (Interviewer) sessionFactory.getCurrentSession().get(Interviewer.class, interviewer.getId());
        assertSame(interviewer, reloadedInterviewer);

        flushAndClearSession();
        reloadedInterviewer = (Interviewer) sessionFactory.getCurrentSession().get(Interviewer.class, interviewer.getId());

        assertNotSame(interviewer, reloadedInterviewer);
        assertEquals(interviewer.getId(), reloadedInterviewer.getId());

        assertEquals(interviewerUser.getId(), reloadedInterviewer.getUser().getId());
    }

    @Test
    public void shoulLoadInterviewCommentWithInterviewer() throws ParseException {
        Interviewer interviewer = new InterviewerBuilder().user(interviewerUser).build();
        InterviewComment interviewComment = new InterviewCommentBuilder().application(applicationForm).user(interviewerUser).comment("comment")
                .interviewer(interviewer).commentType(CommentType.INTERVIEW).build();

        save(interviewer, interviewComment);
        assertNotNull(interviewComment.getId());
        flushAndClearSession();

        Interviewer reloadedInterviewer = (Interviewer) sessionFactory.getCurrentSession().get(Interviewer.class, interviewer.getId());
        assertEquals(interviewComment.getId(), reloadedInterviewer.getInterviewComment().getId());
    }

    @Before
    public void prepare() {
        RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
        interviewerUser = new RegisteredUserBuilder().firstName("hanna").lastName("hoopla").email("hoopla@test.com").username("username1").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
        save(applicant, interviewerUser);
        applicationForm = new ApplicationFormBuilder().applicant(applicant).advert(testObjectProvider.getEnabledProgram()).build();
        save(applicationForm);
        flushAndClearSession();
    }

}
