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
    private static final String OFFER_RECOMMENDATION_VIEW = "redirect:/offerRecommendation?applicationId=";
    private static final String APPROVAL_VIEW = "redirect:/approval/moveToApproval?applicationId=";
    private static final String INTERVIEW_VIEW = "redirect:/interview/moveToInterview?applicationId=";
    private static final String REVIEW_VIEW = "redirect:/review/moveToReview?applicationId=";
    private static final String STATE_TRANSITION_VIEW = "private/staff/admin/state_transition";

    @Autowired
    private ProgramInstanceService programInstanceService;
    

    public String resolveView(ApplicationForm applicationForm) {

        if (!programInstanceService.isProgrammeStillAvailable(applicationForm)) {
            return REJECTION_VIEW + applicationForm.getApplicationNumber() + "&rejectionId=7&rejectionIdForced=true";
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
            case APPROVED:
                return OFFER_RECOMMENDATION_VIEW + applicationForm.getApplicationNumber();
            default:
            }
        }

        return STATE_TRANSITION_VIEW;

    }

}
