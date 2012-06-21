package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.*;

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
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewStateChangeEventBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.PersonBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.utils.EventFactory;

public class InterviewServiceTest {

	private InterviewDAO interviewDAOMock;
	private InterviewService interviewService;
	private ApplicationFormDAO applicationFormDAOMock;
	private EventFactory eventFactoryMock;
	private InterviewerDAO interviewerDAO;
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
		Interview interview = new InterviewBuilder().dueDate(dateFormat.parse("01 04 2012")).id(1).toInterview();
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).id(1).toApplicationForm();
		interviewDAOMock.save(interview);
		applicationFormDAOMock.save(applicationForm);
		InterviewStateChangeEvent interviewStateChangeEvent = new InterviewStateChangeEventBuilder().id(1).toInterviewStateChangeEvent();
		EasyMock.expect(eventFactoryMock.createEvent(interview)).andReturn(interviewStateChangeEvent);
		EasyMock.replay(interviewDAOMock, applicationFormDAOMock, eventFactoryMock);
		
		interviewService.moveApplicationToInterview(interview, applicationForm);
		
		assertEquals(dateFormat.parse("02 04 2012"), applicationForm.getDueDate());
		assertEquals(applicationForm, interview.getApplication());
		assertEquals(interview, applicationForm.getLatestInterview());
		assertEquals(ApplicationFormStatus.INTERVIEW, applicationForm.getStatus());
		EasyMock.verify(interviewDAOMock, applicationFormDAOMock);
		
		assertEquals(1, applicationForm.getEvents().size());
		assertEquals(interviewStateChangeEvent, applicationForm.getEvents().get(0));
	}
	
	@Test
	public void shouldMoveToItnerviewIfInReview() throws ParseException{
		Interview interview = new InterviewBuilder().dueDate(new SimpleDateFormat("dd MM yyyy").parse("01 04 2012")).id(1).toInterview();
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.REVIEW).id(1).toApplicationForm();
		interviewDAOMock.save(interview);
		applicationFormDAOMock.save(applicationForm);
		EasyMock.replay(interviewDAOMock, applicationFormDAOMock);		
		interviewService.moveApplicationToInterview(interview, applicationForm);	
		EasyMock.verify(interviewDAOMock, applicationFormDAOMock);
		
	}
	
	@Test
	public void shouldMoveToItnerviewIfInInterview() throws ParseException{
		Interview interview = new InterviewBuilder().dueDate(new SimpleDateFormat("dd MM yyyy").parse("01 04 2012")).id(1).toInterview();
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.REVIEW).id(1).toApplicationForm();
		interviewDAOMock.save(interview);
		applicationFormDAOMock.save(applicationForm);
		EasyMock.replay(interviewDAOMock, applicationFormDAOMock);		
		interviewService.moveApplicationToInterview(interview, applicationForm);	
		EasyMock.verify(interviewDAOMock, applicationFormDAOMock);
		
	}
	
	
	@Test(expected=IllegalStateException.class)
	public void shouldThrowIllegalStateExceptionIfApplicatioNotInReviewInterviewOrValidation() {		
		Interview interview = new InterviewBuilder().id(1).toInterview();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.UNSUBMITTED).toApplicationForm();		
		
		interviewService.moveApplicationToInterview(interview, applicationForm);
	}
	
	@Test
	public void shouldCreateNewInterviewerInNewInterviewRoundIfLatestRoundIsNull(){
		RegisteredUser interviewerUser = new RegisteredUserBuilder().id(1).firstName("Maria").lastName("Doe").email("mari@test.com").username("mari").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(new ProgramBuilder().id(1).toProgram()).applicant(new RegisteredUserBuilder().id(1).toUser()).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		interviewerDAO.save(interviewer);
		EasyMock.replay(interviewerDAO);
		interviewService.createInterviewerInNewInterview(application, interviewerUser);
		Assert.assertEquals(interviewerUser, interviewer.getUser());
		Assert.assertTrue(interview.getInterviewers().contains(interviewer));
		
	}
	
	@Test
	public void shouldCreateNewInterviewerInLatestInterviewRoundIfLatestRoundIsNotNull(){
		RegisteredUser interviewerUser = new RegisteredUserBuilder().id(1).firstName("Maria").lastName("Doe").email("mari@test.com").username("mari").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		Interview latestInterview = new InterviewBuilder().toInterview();
		ApplicationForm application = new ApplicationFormBuilder().latestInterview(latestInterview).id(1).program(new ProgramBuilder().id(1).toProgram()).applicant(new RegisteredUserBuilder().id(1).toUser()).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		interviewerDAO.save(interviewer);
		EasyMock.replay(interviewerDAO);
		interviewService.createInterviewerInNewInterview(application, interviewerUser);
		Assert.assertEquals(interviewerUser, interviewer.getUser());
		Assert.assertTrue(latestInterview.getInterviewers().contains(interviewer));
		
	}
	
	@Before
	public void setUp() {
		interviewer = new InterviewerBuilder().id(1).toInterviewer();
		interview = new InterviewBuilder().id(1).toInterview();
		interviewerDAO = EasyMock.createMock(InterviewerDAO.class);
		applicationFormDAOMock = EasyMock.createMock(ApplicationFormDAO.class);
		interviewDAOMock = EasyMock.createMock(InterviewDAO.class);
		eventFactoryMock = EasyMock.createMock(EventFactory.class);
		interviewService = new InterviewService(interviewDAOMock, applicationFormDAOMock, eventFactoryMock, interviewerDAO){
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
