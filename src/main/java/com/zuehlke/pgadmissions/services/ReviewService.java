package com.zuehlke.pgadmissions.services;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.StateDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.AssignReviewersComment;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.mail.MailSendingService;
import com.zuehlke.pgadmissions.utils.DateUtils;

@Service
@Transactional
public class ReviewService {

    @Autowired
	private ApplicationFormService applicationsService;

    @Autowired
	private MailSendingService mailService;

    @Autowired
	private WorkflowService applicationFormUserRoleService;
    
    @Autowired
    private StateDAO stateDAO;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CommentService commentService;

	public void moveApplicationToReview(int applicationId, AssignReviewersComment comment) {
	    ApplicationForm application = applicationsService.getById(applicationId);
	    User user = userService.getCurrentUser();
	    Comment latestAssignReviewersComment = applicationsService.getLatestStateChangeComment(application, ApplicationFormAction.ASSIGN_REVIEWERS);
	    
	    DateTime baseDate;
	    if (application.getClosingDate() == null || latestAssignReviewersComment != null) {
	        baseDate = new DateTime();
	    }
	    else {
	        baseDate = new DateTime(application.getClosingDate());
	    }
	    
		State state = stateDAO.getById(ApplicationFormStatus.REVIEW);
        Integer durationInMinutes = state.getDurationInMinutes();
		DateTime dueDate = DateUtils.addWorkingDaysInMinutes(baseDate, durationInMinutes);
        application.setDueDate(dueDate.toDate());
        boolean sendReferenceRequest = application.getState().getId() == ApplicationFormStatus.VALIDATION;
        application.setState(state);
		
        if (sendReferenceRequest) {
            mailService.sendReferenceRequest(application.getReferees(), application);
            Comment latestStateChangeComment = applicationsService.getLatestStateChangeComment(application, null);
            application.setUseCustomReferenceQuestions(latestStateChangeComment.getUseCustomReferenceQuestions());
            applicationFormUserRoleService.validationStageCompleted(application);
        }
        
        comment.setUser(user);
        comment.setApplication(application);
        commentService.save(comment);
        
        applicationFormUserRoleService.movedToReviewStage(comment);
        applicationFormUserRoleService.applicationUpdated(application, comment.getUser());
	}

}
