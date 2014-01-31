package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

public class InterviewCommentMappingTest extends AutomaticRollbackTestCase {

	@Test
	public void shouldSaveAndLoadInterviewComment() {
	    QualificationInstitution institution = new QualificationInstitutionBuilder().code("code").name("a").countryCode("AE").enabled(true).build();
		Program program = new ProgramBuilder().code("doesntexist").title("another title").institution(institution).build();
		save(institution, program);

		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

		RegisteredUser interviewerUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

		Interviewer interviewer = new InterviewerBuilder().user(interviewerUser).build();
		save(applicant, interviewerUser, interviewer);

		ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(applicant).program(program).build();
		save(applicationForm);

		flushAndClearSession();

		InterviewComment interviewComment = new InterviewCommentBuilder().interviewer(interviewer).adminsNotified(false).commentType(CommentType.INTERVIEW)
				.comment("This is an interview comment").suitableCandidateForUcl(false).user(interviewerUser).application(applicationForm)
				.build();
		save(interviewComment);

		assertNotNull(interviewComment.getId());
		Integer id = interviewComment.getId();

		InterviewComment reloadedInterviewComment = (InterviewComment) sessionFactory.getCurrentSession().get(InterviewComment.class, id);
		assertSame(interviewComment, reloadedInterviewComment);

		flushAndClearSession();

		reloadedInterviewComment = (InterviewComment) sessionFactory.getCurrentSession().get(InterviewComment.class, id);
		assertNotSame(interviewComment, reloadedInterviewComment);
		assertEquals(interviewComment.getId(), reloadedInterviewComment.getId());

		assertEquals(interviewerUser.getId(), reloadedInterviewComment.getUser().getId());
		assertEquals(interviewer.getId(), reloadedInterviewComment.getInterviewer().getId());
		assertEquals("This is an interview comment", reloadedInterviewComment.getComment());
		assertFalse(reloadedInterviewComment.getSuitableCandidateForUcl());
	}
}
