package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ReviewRoundDAO;
import com.zuehlke.pgadmissions.dao.ReviewerDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.StateChangeEvent;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.NotificationRecordBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewStateChangeEventBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.StageDurationBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;

public class ReviewServiceTest {

	private ReviewService reviewService;

	private ApplicationFormDAO applicationFormDAOMock;
	private ReviewRoundDAO reviewRoundDAOMock;
	private ReviewerDAO reviewerDAO;
	private StageDurationService stageDurationDAOMock;
	private ReviewRound reviewRound;
	private Reviewer reviewer;
	private EventFactory eventFactoryMock;

	@Before
	public void setUp() {
		reviewer = new ReviewerBuilder().id(1).build();
		reviewRound = new ReviewRoundBuilder().id(1).build();
		reviewerDAO = EasyMock.createMock(ReviewerDAO.class);
		applicationFormDAOMock = EasyMock.createMock(ApplicationFormDAO.class);
		reviewRoundDAOMock = EasyMock.createMock(ReviewRoundDAO.class);
		stageDurationDAOMock = EasyMock.createMock(StageDurationService.class);
		eventFactoryMock = EasyMock.createMock(EventFactory.class);
		reviewService = new ReviewService(applicationFormDAOMock, reviewRoundDAOMock, stageDurationDAOMock, eventFactoryMock, reviewerDAO){
			@Override
			public ReviewRound newReviewRound() {
				return reviewRound;
			}
			
			@Override
			public Reviewer newReviewer() {
				return reviewer;
			}
		};
	}
	
	@Test
	public void shouldCreateNewInterviewerInNewInterviewRoundIfLatestRoundIsNull(){
		RegisteredUser reviewerUser = new RegisteredUserBuilder().id(1).firstName("Maria").lastName("Doe").email("mari@test.com").username("mari").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(new ProgramBuilder().id(1).build()).applicant(new RegisteredUserBuilder().id(1).build()).status(ApplicationFormStatus.VALIDATION).build();
		reviewerDAO.save(reviewer);
		EasyMock.replay(reviewerDAO);
		reviewService.createReviewerInNewReviewRound(application, reviewerUser);
		Assert.assertEquals(reviewerUser, reviewer.getUser());
		Assert.assertTrue(reviewRound.getReviewers().contains(reviewer));
		
	}
	
	@Test
	public void shouldCreateNewInterviewerInLatestInterviewRoundIfLatestRoundIsNotNull(){
		RegisteredUser reviewerUser = new RegisteredUserBuilder().id(1).firstName("Maria").lastName("Doe").email("mari@test.com").username("mari").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
		ReviewRound latestReviewRound = new ReviewRoundBuilder().build();
		ApplicationForm application = new ApplicationFormBuilder().latestReviewRound(latestReviewRound).id(1).program(new ProgramBuilder().id(1).build()).applicant(new RegisteredUserBuilder().id(1).build()).status(ApplicationFormStatus.VALIDATION).build();
		reviewerDAO.save(reviewer);
		EasyMock.replay(reviewerDAO);
		reviewService.createReviewerInNewReviewRound(application, reviewerUser);
		Assert.assertEquals(reviewerUser, reviewer.getUser());
		Assert.assertTrue(latestReviewRound.getReviewers().contains(reviewer));
		
	}

	@Test
	public void shouldSetDueDateOnApplicationUpdateFormAndSaveBoth() throws ParseException {

		ReviewRound reviewRound = new ReviewRoundBuilder().id(1).build();
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).id(1).build();
		applicationForm.addNotificationRecord(new NotificationRecordBuilder().id(2).notificationType(NotificationType.REVIEW_REMINDER).build());
		EasyMock.expect(stageDurationDAOMock.getByStatus(ApplicationFormStatus.REVIEW)).andReturn(
				new StageDurationBuilder().duration(2).unit(DurationUnitEnum.DAYS).build());
		
		reviewRoundDAOMock.save(reviewRound);
		applicationFormDAOMock.save(applicationForm);
		
		StateChangeEvent event = new ReviewStateChangeEventBuilder().id(1).build();		
		EasyMock.expect(eventFactoryMock.createEvent(reviewRound)).andReturn(event);
		
		EasyMock.replay(reviewRoundDAOMock, applicationFormDAOMock, stageDurationDAOMock, eventFactoryMock);

		reviewService.moveApplicationToReview(applicationForm, reviewRound);
		
		assertEquals(DateUtils.truncate(DateUtils.addDays(new Date(), 2), Calendar.DATE), DateUtils.truncate(applicationForm.getDueDate(), Calendar.DATE));
		assertEquals(applicationForm, reviewRound.getApplication());
		assertEquals(reviewRound, applicationForm.getLatestReviewRound());
		assertEquals(ApplicationFormStatus.REVIEW, applicationForm.getStatus());
		
		assertEquals(1, applicationForm.getEvents().size());
		assertEquals(event, applicationForm.getEvents().get(0));
		EasyMock.verify(reviewRoundDAOMock, applicationFormDAOMock);
		assertTrue(applicationForm.getNotificationRecords().isEmpty());

	}

	@Test
	public void shouldMoveToReviewIfInReview() throws ParseException {
		ReviewRound reviewRound = new ReviewRoundBuilder().id(1).build();
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.REVIEW).id(1).build();
		EasyMock.expect(stageDurationDAOMock.getByStatus(ApplicationFormStatus.REVIEW)).andReturn(
				new StageDurationBuilder().duration(2).unit(DurationUnitEnum.DAYS).build());
		reviewRoundDAOMock.save(reviewRound);
		applicationFormDAOMock.save(applicationForm);
		EasyMock.replay(reviewRoundDAOMock, applicationFormDAOMock, stageDurationDAOMock);
		reviewService.moveApplicationToReview(applicationForm, reviewRound);
		EasyMock.verify(reviewRoundDAOMock, applicationFormDAOMock);

	}

	@Test
	public void shouldFailIfApplicationInInvalidState() {
		ApplicationFormStatus[] values = ApplicationFormStatus.values();
		for (ApplicationFormStatus status : values) {
			if (status != ApplicationFormStatus.VALIDATION && status != ApplicationFormStatus.REVIEW) {
				ApplicationForm application = new ApplicationFormBuilder().id(3).status(status).build();
				boolean threwException = false;
				try {
					reviewService.moveApplicationToReview(application, new ReviewRoundBuilder().id(1).build());
				} catch (IllegalStateException ise) {
					if (ise.getMessage().equals("Application in invalid status: '" + status + "'!")) {
						threwException = true;
					}
				}
				Assert.assertTrue(threwException);
			}
		}
	}
	
	@Test
	public void shouldSaveReviewRound(){
		ReviewRound reviewRound = new ReviewRoundBuilder().id(5).build();
		reviewRoundDAOMock.save(reviewRound);
		EasyMock.replay(reviewRoundDAOMock);
		reviewService.save(reviewRound);
		EasyMock.verify(reviewRoundDAOMock);
	}

}
