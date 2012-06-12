package com.zuehlke.pgadmissions.services;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ReviewRoundDAO;
import com.zuehlke.pgadmissions.dao.StageDurationDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.utils.EventFactory;

@Service
public class ReviewService {

	private final ApplicationFormDAO applicationDAO;
	private final ReviewRoundDAO reviewRoundDAO;
	private final StageDurationDAO stageDurationDAO;
	private final EventFactory eventFactory;

	ReviewService() {
		this(null, null, null, null);
	}

	@Autowired
	public ReviewService(ApplicationFormDAO applicationDAO, ReviewRoundDAO reviewRoundDAO, StageDurationDAO stageDurationDAO, EventFactory eventFactory) {

		this.applicationDAO = applicationDAO;
		this.reviewRoundDAO = reviewRoundDAO;
		this.stageDurationDAO = stageDurationDAO;
		this.eventFactory = eventFactory;

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

}
