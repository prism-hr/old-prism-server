package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalEvaluationComment;
import com.zuehlke.pgadmissions.domain.InterviewEvaluationComment;
import com.zuehlke.pgadmissions.domain.ReviewEvaluationComment;
import com.zuehlke.pgadmissions.domain.StateChangeComment;
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

@Service
public class StateTransitionViewResolver {

    private static final String REJECTION_VIEW = "redirect:/rejectApplication?applicationId=";
    private static final String APPROVAL_VIEW = "redirect:/approval/moveToApproval?applicationId=";
    private static final String INTERVIEW_VIEW = "redirect:/interview/moveToInterview?applicationId=";
    private static final String REVIEW_VIEW = "redirect:/review/moveToReview?applicationId=";
    private static final String STATE_TRANSITION_VIEW = "private/staff/admin/state_transition";
    private static final String MY_APPLICATIONS_VIEW = "redirect:/applications";

    @Autowired
    private ProgramInstanceService programInstanceService;
    

    public String resolveView(ApplicationForm applicationForm) {

        if (!programInstanceService.isProgrammeStillAvailable(applicationForm)) {
//            StateChangeComment comment = createRejectionComment(applicationForm);
            return REJECTION_VIEW + applicationForm.getApplicationNumber() + "&rejectionId=7&rejectionIdForced=true";
        }

        if (applicationForm.isInState(ApplicationFormStatus.APPROVED)) {
            return MY_APPLICATIONS_VIEW;
        }

        ApplicationFormStatus nextStatus = applicationForm.getNextStatus();
        if (nextStatus != null) {
            switch (nextStatus) {
            case REVIEW:
                return REVIEW_VIEW + applicationForm.getApplicationNumber();
            case INTERVIEW:
                return INTERVIEW_VIEW + applicationForm.getApplicationNumber();
            case APPROVAL:
                return APPROVAL_VIEW + applicationForm.getApplicationNumber();
            case REJECTED:
                return REJECTION_VIEW + applicationForm.getApplicationNumber();
            default:
            }
        }

        return STATE_TRANSITION_VIEW;

    }


//    private StateChangeComment createRejectionComment(ApplicationForm applicationForm) {
//        StateChangeComment stateChangeComment = null;
//        switch (applicationForm.getStatus()) {
//        case APPROVAL:
//            stateChangeComment = new ApprovalEvaluationComment();
//            break;
//        case REVIEW:
//            stateChangeComment = new ReviewEvaluationComment();
//            break;
//        case VALIDATION:
//            stateChangeComment = new ValidationComment();
//            break;
//        case INTERVIEW:
//            stateChangeComment = new InterviewEvaluationComment();
//            break;
//        }
//        
//    }

}
