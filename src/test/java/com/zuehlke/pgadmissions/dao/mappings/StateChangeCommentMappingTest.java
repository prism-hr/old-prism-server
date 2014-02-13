package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.util.Calendar;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StateChangeComment;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

public class StateChangeCommentMappingTest extends AutomaticRollbackTestCase {

	@Test
	public void shouldSaveAndLoadStateChangeComment() {
	    QualificationInstitution institution = new QualificationInstitutionBuilder().code("code").name("a").domicileCode("AE").enabled(true).build();
		Program program = new ProgramBuilder().code("doesntexist").title("another title").institution(institution).build();
		save(institution, program);

		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

		RegisteredUser reviewer = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

		save(applicant, reviewer);

		ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(applicant).program(program).build();
		save(applicationForm);

		flushAndClearSession();

		StateChangeComment stateChangeComment = new StateChangeComment();
		stateChangeComment.setType(CommentType.REVIEW_EVALUATION);
		stateChangeComment.setComment("This is a validationComment");
		stateChangeComment.setUser(reviewer);
		stateChangeComment.setNextStatus(ApplicationFormStatus.INTERVIEW);

		stateChangeComment.setApplication(applicationForm);

		save(stateChangeComment);

		assertNotNull(stateChangeComment.getId());
		Integer id = stateChangeComment.getId();
		StateChangeComment reloadedComment = (StateChangeComment) sessionFactory.getCurrentSession().get(StateChangeComment.class, id);
		assertSame(stateChangeComment, reloadedComment);

		flushAndClearSession();

		reloadedComment = (StateChangeComment) sessionFactory.getCurrentSession().get(StateChangeComment.class, id);
		assertNotSame(stateChangeComment, reloadedComment);
		assertEquals(stateChangeComment.getId(), reloadedComment.getId());

		assertEquals(reviewer.getId(), reloadedComment.getUser().getId());
		assertEquals("This is a validationComment", reloadedComment.getComment());
		assertEquals(CommentType.REVIEW_EVALUATION, reloadedComment.getType());
		assertEquals(ApplicationFormStatus.INTERVIEW, reloadedComment.getNextStatus());
		assertEquals(DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE),
				DateUtils.truncate(reloadedComment.getDate(), Calendar.DATE));

	}

}
