package com.zuehlke.pgadmissions.services;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ReviewRoundDAO;
import com.zuehlke.pgadmissions.dao.ReviewerDAO;
import com.zuehlke.pgadmissions.dao.StageDurationDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.utils.EventFactory;

@Service
public class ReviewService {

	private final ApplicationFormDAO applicationDAO;
	private final ReviewRoundDAO reviewRoundDAO;
	private final StageDurationDAO stageDurationDAO;
	private final EventFactory eventFactory;
	private final ReviewerDAO reviewerDAO;

	ReviewService() {
		this(null, null, null, null, null);
	}

	@Autowired
	public ReviewService(ApplicationFormDAO applicationDAO, ReviewRoundDAO reviewRoundDAO, StageDurationDAO stageDurationDAO, EventFactory eventFactory,
			ReviewerDAO reviewerDAO) {

		this.applicationDAO = applicationDAO;
		this.reviewRoundDAO = reviewRoundDAO;
		this.stageDurationDAO = stageDurationDAO;
		this.eventFactory = eventFactory;
		this.reviewerDAO = reviewerDAO;

	}

	@Transactional
	public void moveApplicationToReview(ApplicationForm application, ReviewRound reviewRound) {
		checkApplicationStatus(application);
		application.setLatestReviewRound(reviewRound);
		reviewRound.setApplication(application);
		reviewRoundDAO.save(reviewRound);
		StageDuration reviewStageDuration = stageDurationDAO.getByStatus(ApplicationFormStatus.REVIEW);
		application.setDueDate(DateUtils.addMinutes(new Date(), reviewStageDuration.getDurationInMinutes()));
		application.setStatus(ApplicationFormStatus.REVIEW);
		application.getEvents().add(eventFactory.createEvent(reviewRound));
		NotificationRecord reviewReminderNotificationRevord = application.getNotificationForType(NotificationType.REVIEW_REMINDER);
		if(reviewReminderNotificationRevord !=null){
			application.removeNotificationRecord(reviewReminderNotificationRevord);
		}
			
		applicationDAO.save(application);
	}

	private void checkApplicationStatus(ApplicationForm application) {
		ApplicationFormStatus status = application.getStatus();
		switch (status) {
		case VALIDATION:
		case REVIEW:
			break;
		default:
			throw new IllegalStateException(String.format("Application in invalid status: '%s'!", status));
		}
	}

	@Transactional
	public void save(ReviewRound reviewRound) {
		reviewRoundDAO.save(reviewRound);
	}

	@Transactional
	public void createReviewerInNewReviewRound(ApplicationForm applicationForm, RegisteredUser newUser) {
		Reviewer reviewer = newReviewer();
		reviewer.setUser(newUser);
		reviewerDAO.save(reviewer);
		ReviewRound latestReviewRound = applicationForm.getLatestReviewRound();
		if (latestReviewRound == null) {
			ReviewRound reviewRound = newReviewRound();
			reviewRound.getReviewers().add(reviewer);
			reviewRound.setApplication(applicationForm);
			save(reviewRound);
			applicationForm.setLatestReviewRound(reviewRound);
		} else {
			latestReviewRound.getReviewers().add(reviewer);
			save(latestReviewRound);
		}

	}

	public Reviewer newReviewer() {
		return new Reviewer();
	}

	public ReviewRound newReviewRound() {
		return new ReviewRound();
	}
}
