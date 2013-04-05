package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;

public class ReviewerDAOTest extends AutomaticRollbackTestCase {

	private RegisteredUser user;
	private ReviewerDAO dao;
	private Program program;
	private ReminderInterval reminderInterval;

	@Test
	public void shouldReturnReviewerOfLatestReviewRoundIfLastModifiedIsNull() {
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.REVIEW).build();		
		Reviewer reviewer = new ReviewerBuilder().user(user).build();
		ReviewRound reviewRound = new ReviewRoundBuilder().application(application).reviewers(reviewer).build();
		application.setLatestReviewRound(reviewRound);
		
		save(application, reviewer, reviewRound);
		flushAndClearSession();

		List<Reviewer> reviewers = dao.getReviewersDueNotification();
		assertTrue(listContainsId(reviewer, reviewers));
	}
	
	@Test
	public void shouldNotReturnReviewerOfPreviursReviewRoundIfLastModifiedIsNull() {
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.REVIEW).build();		
		Reviewer reviewer = new ReviewerBuilder().user(user).build();
		ReviewRound reviewRound = new ReviewRoundBuilder().application(application).reviewers(reviewer).build();
		application.getReviewRounds().add(reviewRound);
		
		save(application, reviewer, reviewRound);

		List<Reviewer> reviewers = dao.getReviewersDueNotification();
		assertFalse(reviewers.contains(reviewer));

	}

	@Test
	public void shouldNotReturnReviwerInLastNotifiedIsNotNull() {
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.REVIEW).build();		
		Reviewer reviewer = new ReviewerBuilder().user(user).lastNotified(new Date()).build();
		ReviewRound reviewRound = new ReviewRoundBuilder().application(application).reviewers(reviewer).build();
		application.setLatestReviewRound(reviewRound);
		
		save(application, reviewer, reviewRound);
		flushAndClearSession();

		List<Reviewer> reviewers = dao.getReviewersDueNotification();
		assertFalse(reviewers.contains(reviewer));
	}

	@Test
	public void shouldNotReturnReviewerIfApplicationNotInReview() {
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).build();		
		Reviewer reviewer = new ReviewerBuilder().user(user).lastNotified(new Date()).build();
		ReviewRound reviewRound = new ReviewRoundBuilder().application(application).reviewers(reviewer).build();
		application.setLatestReviewRound(reviewRound);
		
		save(application, reviewer, reviewRound);
		flushAndClearSession();
		List<Reviewer> reviewers = dao.getReviewersDueNotification();
		assertFalse(reviewers.contains(reviewer));

	}

	@Test
	public void shouldSaveReviewer() {
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).build();
		save(application);
		flushAndClearSession();

		Reviewer reviewer = new ReviewerBuilder().user(user).build();

		dao.save(reviewer);
		assertNotNull(reviewer.getId());
		flushAndClearSession();
		Reviewer returnedReviewer = (Reviewer) sessionFactory.getCurrentSession().get(Reviewer.class, reviewer.getId());
		assertEquals(returnedReviewer.getId(), reviewer.getId());
	}

	@Test
	public void shouldReturnReviewerOfLatestRoundReminded7Minus5minDaysAgoForASixDaysReminderInterval() {
		reminderInterval.setId(1);
		reminderInterval.setDuration(6);
		reminderInterval.setUnit(DurationUnitEnum.DAYS);
		
		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);
		
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.REVIEW).build();

		Date now = new Date();
		Date sevenDaysAgo = DateUtils.addDays(now, -7);
		Date sevenDaysMinus5MinutesAgo = DateUtils.addMinutes(sevenDaysAgo, 5);
		Reviewer reviewer = new ReviewerBuilder().user(user).lastNotified(sevenDaysMinus5MinutesAgo).build();
		ReviewRound reviewRound = new ReviewRoundBuilder().application(application).reviewers(reviewer).build();
		application.setLatestReviewRound(reviewRound);
		save(application,reviewer, reviewRound);
		flushAndClearSession();

		List<Reviewer> reviewers = dao.getReviewersDueReminder();
		assertTrue(listContainsId(reviewer, reviewers));
	}

	@Test
	public void shouldNotReturnReviewerOfPreviousRound() {
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.REVIEW).build();	

		Date now = new Date();
		Date sevenDaysAgo = DateUtils.addDays(now, -7);
		Date sevenDaysMinus5MinutesAgo = DateUtils.addMinutes(sevenDaysAgo, 5);
		Reviewer reviewer = new ReviewerBuilder().user(user).lastNotified(sevenDaysMinus5MinutesAgo).build();
		ReviewRound reviewRound = new ReviewRoundBuilder().application(application).reviewers(reviewer).build();
		application.getReviewRounds().add(reviewRound);
		save(application,reviewer, reviewRound);
		flushAndClearSession();

		List<Reviewer> reviewers = dao.getReviewersDueReminder();
		assertFalse(reviewers.contains(reviewer));
	}

	@Test
	public void shouldReturnReviewerReminded7Plus5minDaysAgo() {
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.REVIEW).build();

		Date now = new Date();
		Date sevenDaysAgo = DateUtils.addMinutes(now, -((int) TimeUnit.MINUTES.convert(7, TimeUnit.DAYS)));
		Date sevenDaysPlus5MinutesAgo = DateUtils.addMinutes(sevenDaysAgo, -5);
		Reviewer reviewer = new ReviewerBuilder().user(user).lastNotified(sevenDaysPlus5MinutesAgo).build();
		ReviewRound reviewRound = new ReviewRoundBuilder().application(application).reviewers(reviewer).build();
		application.setLatestReviewRound(reviewRound);
		save(application,reviewer, reviewRound);
		
		flushAndClearSession();

		List<Reviewer> reviewers = dao.getReviewersDueReminder();
		assertTrue(listContainsId(reviewer, reviewers));
	}

	@Test
	public void shouldReturnReviewerLastNotified6DaysAgo() {
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.REVIEW).build();
		
		Date now = new Date();
		Date sixDaysAgo = DateUtils.addDays(now, -6);

		Reviewer reviewer = new ReviewerBuilder().user(user).lastNotified(sixDaysAgo).build();
		ReviewRound reviewRound = new ReviewRoundBuilder().application(application).reviewers(reviewer).build();
		application.setLatestReviewRound(reviewRound);
		save(application,reviewer, reviewRound);
		flushAndClearSession();

		List<Reviewer> reviewers = dao.getReviewersDueReminder();
		assertFalse(reviewers.contains(reviewer));
	}

	@Test
	public void shouldReturnNotReviewerWithReview() {
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.REVIEW).build();
	
		Date now = new Date();
		Date sevenDaysAgo = DateUtils.addDays(now, -7);
		Reviewer reviewer = new ReviewerBuilder().user(user).lastNotified(sevenDaysAgo).build();
		ReviewRound reviewRound = new ReviewRoundBuilder().application(application).reviewers(reviewer).build();
		application.setLatestReviewRound(reviewRound);
		
		ReviewComment reviewComment = new ReviewCommentBuilder().reviewer(reviewer).adminsNotified(false).commentType(CommentType.REVIEW)
				.comment("This is a review comment").suitableCandidateForUCL(false).user(user).application(application).decline(true)
				.willingToInterview(false).build();
		save(application,reviewer, reviewRound, reviewComment);

		flushAndClearSession();

		List<Reviewer> reviewers = dao.getReviewersDueReminder();
		assertFalse(reviewers.contains(reviewer));
	}
	
	@Test
	public void shouldNotReturnReviewerIfApplicationFormNotInReview() {
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.APPROVAL).build();

		Date now = new Date();
		Date sevenDaysAgo = DateUtils.addDays(now, -7);		
		Reviewer reviewer = new ReviewerBuilder().user(user).lastNotified(sevenDaysAgo).build();
		ReviewRound reviewRound = new ReviewRoundBuilder().application(application).reviewers(reviewer).build();
		application.setLatestReviewRound(reviewRound);
		save(application,reviewer, reviewRound);
		flushAndClearSession();

		List<Reviewer> reviewers = dao.getReviewersDueReminder();
		assertFalse(reviewers.contains(reviewer));
	}
	
	@Test
	@SuppressWarnings("deprecation")
	public void shouldReturnReviewersRequireAdminNotification() {
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.APPROVAL).build();
		
        Reviewer reviewer1 = new ReviewerBuilder().user(user).id(1).requiresAdminNotification(CheckedStatus.YES).dateAdminsNotified(null).build();
		Reviewer reviewer2 = new ReviewerBuilder().user(user).id(2).requiresAdminNotification(CheckedStatus.NO).dateAdminsNotified(null).build();
		Reviewer reviewer3 = new ReviewerBuilder().user(user).id(1).requiresAdminNotification(CheckedStatus.YES).dateAdminsNotified(new Date()).build();
		Reviewer reviewer4 = new ReviewerBuilder().user(user).id(2).requiresAdminNotification(CheckedStatus.NO).dateAdminsNotified(new Date()).build();
		
		ReviewRound reviewRound = new ReviewRoundBuilder().application(application).reviewers(reviewer1, reviewer2, reviewer3, reviewer4).build();
		application.setLatestReviewRound(reviewRound);
		save(application,reviewer1, reviewer2, reviewer3, reviewer4, reviewRound);
		flushAndClearSession();

		List<Reviewer> reviewers = dao.getReviewersRequireAdminNotification();
		
		assertFalse(listContainsId(reviewer3, reviewers));
		assertFalse(listContainsId(reviewer4, reviewers));
		assertFalse(listContainsId(reviewer2, reviewers));
		assertTrue(listContainsId(reviewer1, reviewers));
	}
	
	@Before
	public void initialise() {
		user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
		program = new ProgramBuilder().code("doesntexist").title("another title").build();

		reminderInterval = new ReminderInterval();
		reminderInterval.setId(1);
		reminderInterval.setDuration(1);
		reminderInterval.setUnit(DurationUnitEnum.WEEKS);
		
		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);
		
		save(user, program);

		dao = new ReviewerDAO(sessionFactory);
	}
	
    private boolean listContainsId(Reviewer reviewer, List<Reviewer> reviewers) {
        for (Reviewer entry : reviewers) {
            if (entry.getId().equals(reviewer.getId())) {
                return true;
            }
        }
        return false;
    }
}
