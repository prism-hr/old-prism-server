package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ReviewRoundDAO;
import com.zuehlke.pgadmissions.dao.StageDurationDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.StageDurationBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;

public class ReviewServiceTest {

	private ReviewService reviewService;

	private ApplicationFormDAO applicationFormDAOMock;
	private ReviewRoundDAO reviewRoundDAOMock;

	private StageDurationDAO stageDurationDAOMock;

	@Before
	public void setUp() {

		applicationFormDAOMock = EasyMock.createMock(ApplicationFormDAO.class);
		reviewRoundDAOMock = EasyMock.createMock(ReviewRoundDAO.class);
		stageDurationDAOMock = EasyMock.createMock(StageDurationDAO.class);
		reviewService = new ReviewService(applicationFormDAOMock, reviewRoundDAOMock, stageDurationDAOMock);
	}

	@Test
	public void shouldSetDueDateOnApplicationUpdateFormAndSaveBoth() throws ParseException {

		ReviewRound reviewRound = new ReviewRoundBuilder().id(1).toReviewRound();
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).id(1).toApplicationForm();
		EasyMock.expect(stageDurationDAOMock.getByStatus(ApplicationFormStatus.REVIEW)).andReturn(
				new StageDurationBuilder().duration(2).unit(DurationUnitEnum.DAYS).toStageDuration());
		reviewRoundDAOMock.save(reviewRound);
		applicationFormDAOMock.save(applicationForm);
		EasyMock.replay(reviewRoundDAOMock, applicationFormDAOMock, stageDurationDAOMock);

		reviewService.moveApplicationToReview(applicationForm, reviewRound);
		assertEquals(DateUtils.truncate(DateUtils.addDays(new Date(), 2), Calendar.DATE), DateUtils.truncate(applicationForm.getDueDate(), Calendar.DATE));
		assertEquals(applicationForm, reviewRound.getApplication());
		assertEquals(reviewRound, applicationForm.getLatestReviewRound());
		assertEquals(ApplicationFormStatus.REVIEW, applicationForm.getStatus());
		EasyMock.verify(reviewRoundDAOMock, applicationFormDAOMock);

	}

	@Test
	public void shouldMoveToReviewIfInReview() throws ParseException {
		ReviewRound reviewRound = new ReviewRoundBuilder().id(1).toReviewRound();
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.REVIEW).id(1).toApplicationForm();
		EasyMock.expect(stageDurationDAOMock.getByStatus(ApplicationFormStatus.REVIEW)).andReturn(
				new StageDurationBuilder().duration(2).unit(DurationUnitEnum.DAYS).toStageDuration());
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
				ApplicationForm application = new ApplicationFormBuilder().id(3).status(status).toApplicationForm();
				boolean threwException = false;
				try {
					reviewService.moveApplicationToReview(application, new ReviewRoundBuilder().id(1).toReviewRound());
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
		ReviewRound reviewRound = new ReviewRoundBuilder().id(5).toReviewRound();
		reviewRoundDAOMock.save(reviewRound);
		EasyMock.replay(reviewRoundDAOMock);
		reviewService.save(reviewRound);
		EasyMock.verify(reviewRoundDAOMock);
	}

}
