package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

public class InterviewerMappingTest extends AutomaticRollbackTestCase{

	private ApplicationForm applicationForm;
	private RegisteredUser interviewerUser;

	@Test
	public void shouldSaveAndLoadInterviewer() throws ParseException{
		Date lastNotified = new SimpleDateFormat("dd MM yyyy HH:mm:ss").parse("01 05 2012 13:08:45");		
		Interviewer interviewer = new InterviewerBuilder().user(interviewerUser).lastNotified(lastNotified).requiresAdminNotification(true).dateAdminsNotified(lastNotified).toInterviewer();
		save(interviewer);
		assertNotNull(interviewer.getId());
		Interviewer reloadedInterviewer = (Interviewer) sessionFactory.getCurrentSession().get(Interviewer.class,interviewer.getId());
		assertSame(interviewer, reloadedInterviewer);
		
		flushAndClearSession();
		reloadedInterviewer = (Interviewer) sessionFactory.getCurrentSession().get(Interviewer.class,interviewer.getId());
		
		assertNotSame(interviewer, reloadedInterviewer);
		assertEquals(interviewer, reloadedInterviewer);

		assertEquals(interviewerUser, reloadedInterviewer.getUser());
		assertEquals(lastNotified, reloadedInterviewer.getLastNotified());
		assertEquals(lastNotified, reloadedInterviewer.getDateAdminsNotified());
		assertEquals(true, reloadedInterviewer.isRequiresAdminNotification());
	}
	
	@Test
	public void shoulLoadInterviewCommentWithInterviewer() throws ParseException{
		Interviewer interviewer = new InterviewerBuilder().user(interviewerUser).toInterviewer();
		InterviewComment interviewComment = new InterviewCommentBuilder().application(applicationForm).user(interviewerUser).comment("comment").interviewer(interviewer).commentType(CommentType.INTERVIEW).toInterviewComment();
		
		save(interviewer, interviewComment);		
		assertNotNull(interviewComment.getId());
		flushAndClearSession();
		
		Interviewer reloadedInterviewer = (Interviewer) sessionFactory.getCurrentSession().get(Interviewer.class,interviewer.getId());	
		assertEquals(interviewComment, reloadedInterviewer.getInterviewComment());
		
	}
	

	
	@Before
	public void setUp() {
		super.setUp();

		Program program = new ProgramBuilder().code("doesntexist").title("another title").toProgram();
		
		save(program);

		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();

		
		interviewerUser = new RegisteredUserBuilder().firstName("hanna").lastName("hoopla").email("hoopla@test.com").username("username1")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		save(applicant, interviewerUser);

		applicationForm = new ApplicationFormBuilder().applicant(applicant).program(program).toApplicationForm();
		save(applicationForm);
		flushAndClearSession();
	}
	
}
