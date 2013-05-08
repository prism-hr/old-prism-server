package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.enums.NotificationType.INTERVIEW_ADMINISTRATION_REMINDER;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertNull;
import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.InterviewDAO;
import com.zuehlke.pgadmissions.dao.InterviewerDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewStateChangeEvent;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewStateChangeEventBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.NotificationRecordBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.InterviewStage;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.mail.MailSendingService;

public class InterviewServiceTest {

	private InterviewDAO interviewDAOMock;
	private InterviewService interviewService;
	private ApplicationFormDAO applicationFormDAOMock;
	private EventFactory eventFactoryMock;
	private InterviewerDAO interviewerDAO;
	private MailSendingService mailServiceMock;
	private Interview interview;
	private Interviewer interviewer;

	
	@Test
	public void shouldGetInterviewById() {
		Interview interview = EasyMock.createMock(Interview.class);
		interview.setId(2);
		EasyMock.expect(interviewDAOMock.getInterviewById(2)).andReturn(interview);
		EasyMock.replay(interview, interviewDAOMock);
		Assert.assertEquals(interview, interviewService.getInterviewById(2));
	}
	
	@Test
	public void shouldDelegateSaveToDAO() {
		Interview interview = EasyMock.createMock(Interview.class);
		interviewDAOMock.save(interview);
		EasyMock.replay(interviewDAOMock);
		interviewService.save(interview);
		EasyMock.verify(interviewDAOMock);
	}
	
	@Test
	public void shouldSetDueDateOnInterviewUpdateFormAndSaveBoth() throws ParseException{
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yyyy");
		Interviewer interviewer = new InterviewerBuilder().build();
		Interview interview = new InterviewBuilder().interviewers(interviewer).dueDate(dateFormat.parse("01 04 2012")).id(1).stage(InterviewStage.SCHEDULED).build();
		Referee referee = new RefereeBuilder().build();
		ApplicationForm applicationForm = new ApplicationFormBuilder().referees(referee).status(ApplicationFormStatus.VALIDATION).id(1).build();
		applicationForm.addNotificationRecord(new NotificationRecordBuilder().id(2).notificationType(NotificationType.INTERVIEW_FEEDBACK_REMINDER).build());
	
		interviewDAOMock.save(interview);
		applicationFormDAOMock.save(applicationForm);
		InterviewStateChangeEvent interviewStateChangeEvent = new InterviewStateChangeEventBuilder().id(1).build();
		EasyMock.expect(eventFactoryMock.createEvent(interview)).andReturn(interviewStateChangeEvent);
		mailServiceMock.sendInterviewConfirmationToApplicant(applicationForm);
		mailServiceMock.sendInterviewConfirmationToInterviewers(asList(interviewer));
		mailServiceMock.sendReferenceRequest(asList(referee), applicationForm);

		EasyMock.replay(interviewDAOMock, applicationFormDAOMock, eventFactoryMock, mailServiceMock);
		interviewService.moveApplicationToInterview(interview, applicationForm);
		EasyMock.verify(interviewDAOMock, applicationFormDAOMock, eventFactoryMock, mailServiceMock);
		
		assertEquals(dateFormat.parse("02 04 2012"), applicationForm.getDueDate());
		assertEquals(applicationForm, interview.getApplication());
		assertEquals(interview, applicationForm.getLatestInterview());
		assertEquals(ApplicationFormStatus.INTERVIEW, applicationForm.getStatus());
		
		assertEquals(1, applicationForm.getEvents().size());
		assertEquals(interviewStateChangeEvent, applicationForm.getEvents().get(0));
		assertTrue(applicationForm.getNotificationRecords().isEmpty());
	}
	
	@Test
	public void shouldMoveToItnerviewIfInReview() throws ParseException{
		Interview interview = new InterviewBuilder().dueDate(new SimpleDateFormat("dd MM yyyy").parse("01 04 2012")).id(1).build();
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.REVIEW).id(1).build();
		interviewDAOMock.save(interview);
		applicationFormDAOMock.save(applicationForm);
		EasyMock.replay(interviewDAOMock, applicationFormDAOMock);		
		interviewService.moveApplicationToInterview(interview, applicationForm);	
		EasyMock.verify(interviewDAOMock, applicationFormDAOMock);
		
	}
	
	@Test
	public void shouldMoveToItnerviewIfInInterview() throws ParseException{
		Interview interview = new InterviewBuilder().dueDate(new SimpleDateFormat("dd MM yyyy").parse("01 04 2012")).id(1).build();
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.REVIEW).id(1).build();
		interviewDAOMock.save(interview);
		applicationFormDAOMock.save(applicationForm);
		EasyMock.replay(interviewDAOMock, applicationFormDAOMock);		
		interviewService.moveApplicationToInterview(interview, applicationForm);	
		EasyMock.verify(interviewDAOMock, applicationFormDAOMock);
		
	}
	
	@Test
	public void shouldMoveToItnerviewAndRemoveReminderForInterviewAdministrationDelegate() throws ParseException{
		Interview interview = new InterviewBuilder().dueDate(new SimpleDateFormat("dd MM yyyy").parse("01 04 2012")).id(1).build();
		RegisteredUser delegate = new RegisteredUserBuilder().id(12).build();
		ApplicationForm applicationForm = new ApplicationFormBuilder().applicationAdministrator(delegate )
				.status(ApplicationFormStatus.REVIEW).id(1)
				.notificationRecords(new NotificationRecordBuilder().notificationType(INTERVIEW_ADMINISTRATION_REMINDER).build())
				.build();
		interviewDAOMock.save(interview);
		applicationFormDAOMock.save(applicationForm);
		EasyMock.replay(interviewDAOMock, applicationFormDAOMock);		
		interviewService.moveApplicationToInterview(interview, applicationForm);	
		EasyMock.verify(interviewDAOMock, applicationFormDAOMock);
		
		assertNull(applicationForm.getApplicationAdministrator());
		assertNull(applicationForm.getNotificationForType(INTERVIEW_ADMINISTRATION_REMINDER));
	}
	
	@Test
	public void shouldCreateNewInterviewerInNewInterviewRoundIfLatestRoundIsNull(){
		RegisteredUser interviewerUser = new RegisteredUserBuilder().id(1).firstName("Maria").lastName("Doe").email("mari@test.com").username("mari").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(new ProgramBuilder().id(1).build()).applicant(new RegisteredUserBuilder().id(1).build()).status(ApplicationFormStatus.VALIDATION).build();
		interviewerDAO.save(interviewer);
		EasyMock.replay(interviewerDAO);
		interviewService.addInterviewerInPreviousInterview(application, interviewerUser);
		Assert.assertEquals(interviewerUser, interviewer.getUser());
		Assert.assertTrue(interview.getInterviewers().contains(interviewer));
		
	}
	
	@Test
	public void shouldCreateNewInterviewerInLatestInterviewRoundIfLatestRoundIsNotNull(){
		RegisteredUser interviewerUser = new RegisteredUserBuilder().id(1).firstName("Maria").lastName("Doe").email("mari@test.com").username("mari").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
		Interview latestInterview = new InterviewBuilder().build();
		ApplicationForm application = new ApplicationFormBuilder().latestInterview(latestInterview).id(1).program(new ProgramBuilder().id(1).build()).applicant(new RegisteredUserBuilder().id(1).build()).status(ApplicationFormStatus.VALIDATION).build();
		interviewerDAO.save(interviewer);
		EasyMock.replay(interviewerDAO);
		interviewService.addInterviewerInPreviousInterview(application, interviewerUser);
		Assert.assertEquals(interviewerUser, interviewer.getUser());
		Assert.assertTrue(latestInterview.getInterviewers().contains(interviewer));
	}
	
	@Before
	public void setUp() {
		interviewer = new InterviewerBuilder().id(1).build();
		interview = new InterviewBuilder().id(1).build();
		interviewerDAO = EasyMock.createMock(InterviewerDAO.class);
		applicationFormDAOMock = EasyMock.createMock(ApplicationFormDAO.class);
		interviewDAOMock = EasyMock.createMock(InterviewDAO.class);
		eventFactoryMock = createMock(EventFactory.class);
		mailServiceMock = createMock(MailSendingService.class);
		interviewService = new InterviewService(interviewDAOMock, applicationFormDAOMock, eventFactoryMock, interviewerDAO, mailServiceMock){
			@Override
			public Interview newInterview() {
				return interview;
			}
			
			@Override
			public Interviewer newInterviewer() {
				return interviewer;
			}
		};
	}
	
}
