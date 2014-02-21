package com.zuehlke.pgadmissions.services;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.AssignReviewersComment;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.mail.MailSendingService;
import com.zuehlke.pgadmissions.utils.DateUtils;

@Service
@Transactional
public class ReviewService {

    @Autowired
	private ApplicationsService applicationsService;

    @Autowired
	private StageDurationService stageDurationService;

    @Autowired
	private MailSendingService mailService;

    @Autowired
	private ApplicationFormUserRoleService applicationFormUserRoleService;

	public void moveApplicationToReview(ApplicationForm application, AssignReviewersComment assignReviewersComment) {
	    
	    Comment latestAssignReviewersComment = applicationsService.getLatestStateChangeComment(application, ApplicationFormAction.ASSIGN_REVIEWERS);
	    
	    DateTime baseDate;
	    if (application.getBatchDeadline() == null || latestAssignReviewersComment != null) {
	        baseDate = new DateTime();
	    }
	    else {
	        baseDate = new DateTime(application.getBatchDeadline());
	    }
	    
		StageDuration reviewStageDuration = stageDurationService.getByStatus(ApplicationFormStatus.REVIEW);
		DateTime dueDate = DateUtils.addWorkingDaysInMinutes(baseDate, reviewStageDuration.getDurationInMinutes());
        application.setDueDate(dueDate.toDate());
        boolean sendReferenceRequest = application.getStatus() == ApplicationFormStatus.VALIDATION;
        application.setStatus(ApplicationFormStatus.REVIEW);
		
        if (sendReferenceRequest) {
            mailService.sendReferenceRequest(application.getReferees(), application);
            Comment latestStateChangeComment = applicationsService.getLatestStateChangeComment(application, null);
            application.setUseCustomReferenceQuestions(latestStateChangeComment.getUseCustomReferenceQuestions());
            applicationFormUserRoleService.validationStageCompleted(application);
        }
        
        applicationFormUserRoleService.movedToReviewStage(assignReviewersComment);
        applicationFormUserRoleService.registerApplicationUpdate(application, assignReviewersComment.getUser(), ApplicationUpdateScope.ALL_USERS);
		applicationsService.save(application);
	}

}
