package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewParticipant;
import com.zuehlke.pgadmissions.domain.InterviewTimeslot;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewParticipantBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewTimeslotBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public class InterviewDAOTest extends AutomaticRollbackTestCase {

    private InterviewDAO dao;
    private RegisteredUser applicant;
    private Program program;

    @Test
    public void shouldSaveInterview() {
        ApplicationForm application = new ApplicationFormBuilder().id(2).program(program).applicant(applicant).status(ApplicationFormStatus.VALIDATION).build();
        save(application);
        flushAndClearSession();

        Interview interview = new InterviewBuilder().application(application).furtherDetails("at 9pm").lastNotified(new Date()).locationURL("pgadmissions.com")
                .build();
        dao.save(interview);
        assertNotNull(interview.getId());
        flushAndClearSession();

        Interview returnedInterview = (Interview) sessionFactory.getCurrentSession().get(Interview.class, interview.getId());
        assertEquals(returnedInterview.getId(), interview.getId());

    }

    @Test
    public void shouldGetInterviewerById() {
        ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(applicant).status(ApplicationFormStatus.VALIDATION).build();
        save(application);
        flushAndClearSession();

        Interview interview = new InterviewBuilder().build();
        dao.save(interview);
        assertNotNull(interview.getId());
        flushAndClearSession();
        assertEquals(interview.getId(), dao.getInterviewById(interview.getId()).getId());
    }

    @Test
    public void shouldSaveInterviewScheduleData() {
        ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(applicant).status(ApplicationFormStatus.VALIDATION).build();
        RegisteredUser user1 = new RegisteredUserBuilder().username("1@mail.com").build();
        RegisteredUser user2 = new RegisteredUserBuilder().username("2@mail.com").build();
        save(application, user1, user2);
        flushAndClearSession();

        Interview interview = new InterviewBuilder().build();
        InterviewParticipant participant1 = new InterviewParticipantBuilder().user(user1).build();
        InterviewParticipant participant2 = new InterviewParticipantBuilder().user(user2).build();
        interview.getParticipants().addAll(Arrays.asList(participant1, participant2));
        
        InterviewTimeslot timeslot1 = new InterviewTimeslotBuilder().dueDate(new Date()).startTime("09:11").build();
        InterviewTimeslot timeslot2 = new InterviewTimeslotBuilder().dueDate(new Date()).startTime("09:12").build();
        interview.getTimeslots().addAll(Arrays.asList(timeslot1, timeslot2));
        
        participant1.getAcceptedTimeslots().add(timeslot1);
        participant2.getAcceptedTimeslots().addAll(Arrays.asList(timeslot1, timeslot2));

        timeslot1.getAcceptedParticipants().addAll(Arrays.asList(participant1, participant2));
        timeslot2.getAcceptedParticipants().add(participant2);

        save(interview);
        flushAndClearSession();

        Interview returnedInterview = dao.getInterviewById(interview.getId());
        List<InterviewParticipant> participants = returnedInterview.getParticipants();
        InterviewParticipant returnedParticipant1 = participants.get(0);
        InterviewParticipant returnedParticipant2 = participants.get(1);
        
        assertEquals("1@mail.com", returnedParticipant1.getUser().getUsername());
        assertEquals("2@mail.com", returnedParticipant2.getUser().getUsername());
        
        assertEquals(1, returnedParticipant1.getAcceptedTimeslots().size());
        assertEquals(2, returnedParticipant2.getAcceptedTimeslots().size());
        
        InterviewTimeslot returnedTimeslot1 = returnedParticipant1.getAcceptedTimeslots().iterator().next();
        assertEquals("09:11", returnedTimeslot1.getStartTime());
    }
    
    @Test
    public void shouldRemoveInterviewScheduleData() {
        ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(applicant).status(ApplicationFormStatus.VALIDATION).build();
        RegisteredUser user1 = new RegisteredUserBuilder().username("1@mail.com").build();
        RegisteredUser user2 = new RegisteredUserBuilder().username("2@mail.com").build();
        save(application, user1, user2);
        flushAndClearSession();

        Interview interview = new InterviewBuilder().build();
        InterviewParticipant participant1 = new InterviewParticipantBuilder().user(user1).build();
        InterviewParticipant participant2 = new InterviewParticipantBuilder().user(user2).build();
        interview.getParticipants().addAll(Arrays.asList(participant1, participant2));
        
        InterviewTimeslot timeslot1 = new InterviewTimeslotBuilder().dueDate(new Date()).startTime("09:11").build();
        InterviewTimeslot timeslot2 = new InterviewTimeslotBuilder().dueDate(new Date()).startTime("09:12").build();
        interview.getTimeslots().addAll(Arrays.asList(timeslot1, timeslot2));
        
        participant1.getAcceptedTimeslots().add(timeslot1);
        participant2.getAcceptedTimeslots().addAll(Arrays.asList(timeslot1, timeslot2));

        timeslot1.getAcceptedParticipants().addAll(Arrays.asList(participant1, participant2));
        timeslot2.getAcceptedParticipants().add(participant2);

        save(interview);
        flushAndClearSession();

        Interview returnedInterview = dao.getInterviewById(interview.getId());
        assertEquals(2, returnedInterview.getParticipants().size());
        assertEquals(2, returnedInterview.getTimeslots().get(0).getAcceptedParticipants().size());
        returnedInterview.getTimeslots().clear();
        returnedInterview.getParticipants().clear();
        
        flushAndClearSession();
        
        Interview returnedInterview2 = dao.getInterviewById(interview.getId());
        assertEquals(0, returnedInterview2.getParticipants().size());
        assertEquals(0, returnedInterview2.getTimeslots().size());
    }

    @Before
    public void prepare() {
        applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
        program = new ProgramBuilder().code("doesntexist").title("another title").build();

        save(applicant, program, applicant);

        dao = new InterviewDAO(sessionFactory);
    }
}
