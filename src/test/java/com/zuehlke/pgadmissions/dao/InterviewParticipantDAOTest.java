package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewParticipant;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewParticipantBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;
import com.zuehlke.pgadmissions.domain.enums.InterviewStage;
import com.zuehlke.pgadmissions.domain.enums.ReminderType;

public class InterviewParticipantDAOTest extends AutomaticRollbackTestCase {

    private Program program;

    private RegisteredUser user;

    private InterviewParticipantDAO interviewParticipantDAO;

    @Test
    public void shouldReturnParticipantsDueReminders() {
        ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).build();
        save(application);
        Date now = Calendar.getInstance().getTime();
        Date eightDaysAgo = DateUtils.addDays(now, -8);

        Interview interview = new InterviewBuilder().stage(InterviewStage.SCHEDULING).application(application).build();
        InterviewParticipant participant = new InterviewParticipantBuilder().lastNotified(eightDaysAgo).build();
        interview.getParticipants().add(participant);

        application.getInterviews().add(interview);
        application.setLatestInterview(interview);

        save(interview);
        flushAndClearSession();

        List<InterviewParticipant> participants = interviewParticipantDAO.getInterviewParticipantsDueReminder();
        assertNotNull(participants);
        assertTrue(participants.get(0).getId().equals(participant.getId()));
    }

    @Test
    public void shouldNotRemindParticipantsIfDontBelongToLatestInterview() {
        ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).build();
        save(application);
        Date now = Calendar.getInstance().getTime();
        Date eightDaysAgo = DateUtils.addDays(now, -8);

        Interview interview = new InterviewBuilder().stage(InterviewStage.SCHEDULING).application(application).build();
        InterviewParticipant participant = new InterviewParticipantBuilder().lastNotified(eightDaysAgo).build();
        interview.getParticipants().add(participant);

        application.getInterviews().add(interview);

        save(interview);
        flushAndClearSession();
        
        List<InterviewParticipant> participants = interviewParticipantDAO.getInterviewParticipantsDueReminder();
        assertTrue(participants.size() == 0);
    }

    @Test
    public void shouldNotRemindParticipantWhoHasRespondedAlready() {
        ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).build();
        save(application);
        Date now = Calendar.getInstance().getTime();
        Date eightDaysAgo = DateUtils.addDays(now, -8);

        Interview interview = new InterviewBuilder().stage(InterviewStage.SCHEDULING).application(application).build();
        InterviewParticipant participant = new InterviewParticipantBuilder().lastNotified(eightDaysAgo).responded(true).build();
        interview.getParticipants().add(participant);

        application.getInterviews().add(interview);

        save(interview);
        flushAndClearSession();

        List<InterviewParticipant> participants = interviewParticipantDAO.getInterviewParticipantsDueReminder();
        assertTrue(participants.size() == 0);
    }

    @Test
    public void shouldNotRemindParticipantsWhenInterviewAlreadyScheduled() {
        ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).build();
        save(application);
        Date now = Calendar.getInstance().getTime();
        Date eightDaysAgo = DateUtils.addDays(now, -8);

        Interview interview = new InterviewBuilder().stage(InterviewStage.SCHEDULED).application(application).build();
        InterviewParticipant participant = new InterviewParticipantBuilder().lastNotified(eightDaysAgo).build();
        interview.getParticipants().add(participant);

        application.getInterviews().add(interview);

        save(interview);
        flushAndClearSession();

        List<InterviewParticipant> participants = interviewParticipantDAO.getInterviewParticipantsDueReminder();
        assertTrue(participants.size() == 0);
    }

    @Test
    public void shouldNotRemindParticipantWhenRecentlyNotified() {
        ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).build();
        save(application);
        Date now = Calendar.getInstance().getTime();
        Date threeDaysAgo = DateUtils.addDays(now, -3);

        Interview interview = new InterviewBuilder().stage(InterviewStage.SCHEDULED).application(application).build();
        InterviewParticipant participant = new InterviewParticipantBuilder().lastNotified(threeDaysAgo).build();
        interview.getParticipants().add(participant);

        application.getInterviews().add(interview);

        save(interview);
        flushAndClearSession();

        List<InterviewParticipant> participants = interviewParticipantDAO.getInterviewParticipantsDueReminder();
        assertTrue(participants.size() == 0);
    }

    @Before
    public void prepare() {

        user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

        QualificationInstitution institution = new QualificationInstitutionBuilder().code("code").name("a").countryCode("AE").enabled(true).build();
        program = new ProgramBuilder().code("doesntexist").title("another title").institution(institution).build();

        ReminderIntervalDAO reminderIntervalDAO = new ReminderIntervalDAO(sessionFactory);
        ReminderInterval reminderInterval = reminderIntervalDAO.getReminderInterval(ReminderType.INTERVIEW_SCHEDULE);
        reminderInterval.setDuration(1);
        reminderInterval.setUnit(DurationUnitEnum.WEEKS);

        save(user, institution, program);

        flushAndClearSession();
        interviewParticipantDAO = new InterviewParticipantDAO(sessionFactory);
    }

}
