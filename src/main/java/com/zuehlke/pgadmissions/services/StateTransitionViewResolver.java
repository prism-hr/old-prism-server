package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

@Service
public class StateTransitionViewResolver {
	
	private static final Integer REJECTION_REASON_WHEN_PROGAMME_EXPIRED = 7;
    private static final String REJECTION_VIEW = "redirect:/rejectApplication?applicationId=";
    private static final String OFFER_RECOMMENDATION_VIEW = "redirect:/offerRecommendation?applicationId=";
    private static final String APPROVAL_VIEW = "redirect:/approval/moveToApproval?action=firstLoad&applicationId=";
    private static final String INTERVIEW_VIEW = "redirect:/interview/moveToInterview?applicationId=";
    private static final String REVIEW_VIEW = "redirect:/review/moveToReview?applicationId=";
    private static final String STATE_TRANSITION_VIEW = "private/staff/admin/state_transition";

    @Autowired
    private ProgramInstanceService programInstanceService;
    
    public String resolveView(ApplicationForm applicationForm) {
    	return resolveView(applicationForm, null);
    }

    public String resolveView(ApplicationForm applicationForm, String action) {

        if (!programInstanceService.isProgrammeStillAvailable(applicationForm)) {
            return REJECTION_VIEW + applicationForm.getApplicationNumber() + 
            		"&rejectionId=" + REJECTION_REASON_WHEN_PROGAMME_EXPIRED.toString() + "&rejectionIdForced=true";
        } else if ("abort".equals(action)) {
        	return STATE_TRANSITION_VIEW;
        } else {
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
}