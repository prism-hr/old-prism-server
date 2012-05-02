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
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;

public class ReviewCommentMappingTest extends AutomaticRollbackTestCase{

	@Test
	public void shouldSaveAndLoadReviewComment(){
		Program program = new ProgramBuilder().code("doesntexist").title("another title").toProgram();		
		save( program);
		
		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		
		RegisteredUser reviewer = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		
		save(applicant, reviewer);
		
		ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(applicant).program(program).toApplicationForm();		
		save(applicationForm);
		
		flushAndClearSession();
		
		ReviewComment reviewComment = new ReviewCommentBuilder().comment("This is a review comment").suitableCandidate(CheckedStatus.NO).user(reviewer).application(applicationForm).decline(CheckedStatus.YES).willingToSupervice(CheckedStatus.NO).toReviewComment();
		save(reviewComment);
		
		assertNotNull(reviewComment.getId());
		Integer id = reviewComment.getId();
		
		ReviewComment reloadedComment = (ReviewComment) sessionFactory.getCurrentSession().get(ReviewComment.class, id);
		assertSame(reviewComment, reloadedComment);

		flushAndClearSession();

		reloadedComment = (ReviewComment) sessionFactory.getCurrentSession().get(ReviewComment.class, id);
		assertNotSame(reviewComment, reloadedComment);
		assertEquals(reviewComment, reloadedComment);

		assertEquals(reviewer, reloadedComment.getUser());
		assertEquals("This is a review comment", reloadedComment.getComment());
		assertEquals(DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE), DateUtils.truncate(reloadedComment.getCreatedTimestamp(), Calendar.DATE));
		
	}
	
}
