package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;

public class InterviewerDAOTest extends AutomaticRollbackTestCase {

    private RegisteredUser user;
    private InterviewerDAO dao;
    private Program program;
    private ReminderInterval reminderInterval;

    @Test
    public void shouldGetInterviewerById() {
        ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).build();
        save(application);
        flushAndClearSession();

        Interviewer interviewer = new InterviewerBuilder().build();
        dao.save(interviewer);
        assertNotNull(interviewer.getId());
        flushAndClearSession();
        assertEquals(interviewer.getId(), dao.getInterviewerById(interviewer.getId()).getId());

    }

    @Test
    public void shouldSaveInterviewer() {
        ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).build();
        save(application);
        flushAndClearSession();

        Interviewer interviewer = new InterviewerBuilder().build();
        dao.save(interviewer);
        assertNotNull(interviewer.getId());
        flushAndClearSession();

        Interviewer returnedInterviewer = (Interviewer) sessionFactory.getCurrentSession().get(Interviewer.class, interviewer.getId());
        assertEquals(returnedInterviewer.getId(), interviewer.getId());

    }

    @Before
    public void initialise() {
        user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
        program = new ProgramBuilder().code("doesntexist").title("another title").build();

        reminderInterval = new ReminderInterval();
        reminderInterval.setId(1);
        reminderInterval.setDuration(1);
        reminderInterval.setUnit(DurationUnitEnum.WEEKS);

        sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);

        save(user, program);

        dao = new InterviewerDAO(sessionFactory);
    }

}
