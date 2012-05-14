package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

public class ReviewerDAOTest extends AutomaticRollbackTestCase {

	private RegisteredUser user;
	private ReviewerDAO dao;
	private Program program;

	@Test
	public void shouldReturnReviewerIfLastModifiedIsNull() {
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.REVIEW)
				.toApplicationForm();
		save(application);
		flushAndClearSession();
		Reviewer reviewer = new ReviewerBuilder().user(user).application(application).toReviewer();
		save(reviewer);
		flushAndClearSession();

		List<Reviewer> reviewers = dao.getReviewersDueNotification();
		assertTrue(reviewers.contains(reviewer));

	}

	@Test
	public void shouldNotReturnReviwerInLastNotifiedIsNotNull() {
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.REVIEW)
				.toApplicationForm();
		save(application);
		flushAndClearSession();
		Reviewer reviewer = new ReviewerBuilder().user(user).lastNotified(new Date()).application(application).toReviewer();
		save(reviewer);
		flushAndClearSession();

		List<Reviewer> reviewers = dao.getReviewersDueNotification();
		assertFalse(reviewers.contains(reviewer));

	}

	@Test
	public void shouldNotReturnReviewerIfApplicationNotInReview() {
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.VALIDATION)
				.toApplicationForm();
		save(application);
		flushAndClearSession();

		Reviewer reviewer = new ReviewerBuilder().user(user).application(application).toReviewer();
		save(reviewer);
		flushAndClearSession();

		List<Reviewer> reviewers = dao.getReviewersDueNotification();
		assertFalse(reviewers.contains(reviewer));

	}

	@Test
	public void shouldSaveReviewer() {
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.VALIDATION)
				.toApplicationForm();
		save(application);
		flushAndClearSession();

		Reviewer reviewer = new ReviewerBuilder().user(user).application(application).toReviewer();

		dao.save(reviewer);
		assertNotNull(reviewer.getId());
		flushAndClearSession();
		Reviewer returnedReviewer = (Reviewer) sessionFactory.getCurrentSession().get(Reviewer.class, reviewer.getId());
		assertEquals(returnedReviewer, reviewer);

	}


	@Test
	public void shouldReturnReviewerReminded7Minus5minDaysAgo() {
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.REVIEW)
				.toApplicationForm();
		save(application);
		flushAndClearSession();
		Date now = new Date();
		Date sevenDaysAgo = DateUtils.addDays(now, -7);
		Date sevenDaysMinus5MinutesAgo = DateUtils.addMinutes(sevenDaysAgo, 5);
		Reviewer reviewer = new ReviewerBuilder().user(user).application(application).lastNotified(sevenDaysMinus5MinutesAgo).toReviewer();
		save(reviewer);
		flushAndClearSession();

		List<Reviewer> reviewers = dao.getReviewersDueReminder();
		assertTrue(reviewers.contains(reviewer));
	}


	@Test
	public void shouldReturnReviewerReminded7Plus5minDaysAgo() {
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.REVIEW)
				.toApplicationForm();
		save(application);
		flushAndClearSession();
		Date now = new Date();
		Date sevenDaysAgo = DateUtils.addDays(now, -7);
		Date sevenDaysPlus5MinutesAgo = DateUtils.addMinutes(sevenDaysAgo, -5);
		Reviewer reviewer = new ReviewerBuilder().user(user).application(application).lastNotified(sevenDaysPlus5MinutesAgo).toReviewer();
		save(reviewer);
		flushAndClearSession();

		List<Reviewer> reviewers = dao.getReviewersDueReminder();
		assertTrue(reviewers.contains(reviewer));
	}

	@Test
	public void shouldNotReturnReviewerLastNotified6DaysAgo() {
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.REVIEW)
				.toApplicationForm();
		save(application);
		flushAndClearSession();
		Date now = new Date();
		Date sixDaysAgo = DateUtils.addDays(now, -6);

		Reviewer reviewer = new ReviewerBuilder().user(user).application(application).lastNotified(sixDaysAgo).toReviewer();
		save(reviewer);
		flushAndClearSession();

		List<Reviewer> reviewers = dao.getReviewersDueReminder();
		assertFalse(reviewers.contains(reviewer));
	}

	@Test
	public void shouldReturnNotReviewerWithReview() {
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.REVIEW)
				.toApplicationForm();
		save(application);
		flushAndClearSession();
		Date now = new Date();
		Date sevenDaysAgo = DateUtils.addDays(now, -7);
		Reviewer reviewer = new ReviewerBuilder().user(user).application(application).lastNotified(sevenDaysAgo).toReviewer();
		ReviewComment reviewComment = new ReviewCommentBuilder().reviewer(reviewer).adminsNotified(CheckedStatus.NO).commentType(CommentType.REVIEW)
				.comment("This is a review comment").suitableCandidate(CheckedStatus.NO).user(user).application(application).decline(CheckedStatus.YES)
				.willingToSupervice(CheckedStatus.NO).toReviewComment();
		save(reviewer, reviewComment);

		flushAndClearSession();

		List<Reviewer> reviewers = dao.getReviewersDueReminder();
		assertFalse(reviewers.contains(reviewer));
	}
	@Test
	public void shouldNotReturnReviewerIfApplicationFormNotInReview() {
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.APPROVAL)
				.toApplicationForm();
		save(application);
		flushAndClearSession();
		Date now = new Date();
		Date sevenDaysAgo = DateUtils.addDays(now, -7);		
		Reviewer reviewer = new ReviewerBuilder().user(user).application(application).lastNotified(sevenDaysAgo).toReviewer();
		save(reviewer);
		flushAndClearSession();

		List<Reviewer> reviewers = dao.getReviewersDueReminder();
		assertFalse(reviewers.contains(reviewer));
	}
	@Before
	public void setUp() {
		super.setUp();
		user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		program = new ProgramBuilder().code("doesntexist").title("another title").toProgram();

		save(user, program);

		dao = new ReviewerDAO(sessionFactory);
	}
}
