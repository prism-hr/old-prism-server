package com.zuehlke.pgadmissions.services;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ReviewRoundDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.mail.MailSendingService;
import com.zuehlke.pgadmissions.utils.DateUtils;

@Service
@Transactional
public class ReviewService {

	private final ApplicationFormDAO applicationDAO;
	private final ReviewRoundDAO reviewRoundDAO;
	private final StageDurationService stageDurationService;
	private final EventFactory eventFactory;
	private final MailSendingService mailService;
	private final ApplicationFormUserRoleService applicationFormUserRoleService;

	public ReviewService() {
		this(null, null, null, null, null, null);
	}

	@Autowired
    public ReviewService(ApplicationFormDAO applicationDAO, ReviewRoundDAO reviewRoundDAO,
            StageDurationService stageDurationService, EventFactory eventFactory, MailSendingService mailService, 
            ApplicationFormUserRoleService applicationFormUserRoleService) {
		this.applicationDAO = applicationDAO;
		this.reviewRoundDAO = reviewRoundDAO;
		this.stageDurationService = stageDurationService;
		this.eventFactory = eventFactory;
        this.mailService = mailService;
        this.applicationFormUserRoleService = applicationFormUserRoleService;
	}

	public void moveApplicationToReview(ApplicationForm application, ReviewRound reviewRound, RegisteredUser initiator) {
	    DateTime baseDate;
	    
	    if (application.getBatchDeadline() == null || application.getLatestReviewRound() != null) {
	        baseDate = new DateTime();
	    }
	    else {
	        baseDate = new DateTime(application.getBatchDeadline());
	    }
		application.setLatestReviewRound(reviewRound);
		reviewRound.setApplication(application);
		reviewRoundDAO.save(reviewRound);
		StageDuration reviewStageDuration = stageDurationService.getByStatus(ApplicationFormStatus.REVIEW);
		DateTime dueDate = DateUtils.addWorkingDaysInMinutes(baseDate, reviewStageDuration.getDurationInMinutes());
        application.setDueDate(dueDate.toDate());
        boolean sendReferenceRequest = application.getStatus() == ApplicationFormStatus.VALIDATION;
        application.setStatus(ApplicationFormStatus.REVIEW);
		application.getEvents().add(eventFactory.createEvent(reviewRound));
		application.removeNotificationRecord(NotificationType.REVIEW_REMINDER);
        if (sendReferenceRequest) {
            mailService.sendReferenceRequest(application.getReferees(), application);
            applicationFormUserRoleService.validationStageCompleted(application);
        }
        applicationFormUserRoleService.movedToReviewStage(reviewRound);
        applicationFormUserRoleService.registerApplicationUpdate(application, initiator, ApplicationUpdateScope.ALL_USERS);
		applicationDAO.save(application);
	}

	public void save(ReviewRound reviewRound) {
		reviewRoundDAO.save(reviewRound);
	}

	public Reviewer newReviewer() {
		return new Reviewer();
	}

	public ReviewRound newReviewRound() {
		return new ReviewRound();
	}
}