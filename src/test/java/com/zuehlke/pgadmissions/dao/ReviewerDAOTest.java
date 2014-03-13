package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;
import com.zuehlke.pgadmissions.domain.enums.ReminderType;

public class ReviewerDAOTest extends AutomaticRollbackTestCase {

    private RegisteredUser user;
    private ReviewerDAO dao;
    private Program program;

    @Test
    public void shouldSaveReviewer() {
        ApplicationForm application = new ApplicationFormBuilder().id(1).advert(program).applicant(user).status(ApplicationFormStatus.VALIDATION).build();
        save(application);
        flushAndClearSession();

        Reviewer reviewer = new ReviewerBuilder().user(user).build();

        dao.save(reviewer);
        assertNotNull(reviewer.getId());
        flushAndClearSession();
        Reviewer returnedReviewer = (Reviewer) sessionFactory.getCurrentSession().get(Reviewer.class, reviewer.getId());
        assertEquals(returnedReviewer.getId(), reviewer.getId());
    }

    @Before
    public void initialise() {
        user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
        ReminderIntervalDAO reminderIntervalDAO = new ReminderIntervalDAO(sessionFactory);
        ReminderInterval reminderInterval = reminderIntervalDAO.getReminderInterval(ReminderType.INTERVIEW_SCHEDULE);
        reminderInterval.setDuration(1);
        reminderInterval.setUnit(DurationUnitEnum.WEEKS);
        sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);
        save(user);
        dao = new ReviewerDAO(sessionFactory);
        program = testObjectProvider.getEnabledProgram();
    }

}
