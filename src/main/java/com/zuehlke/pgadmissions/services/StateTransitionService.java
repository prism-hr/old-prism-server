package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismState;

@Service
public class StateTransitionService {

    private static final Integer REJECTION_REASON_WHEN_PROGAMME_EXPIRED = 7;
    private static final String REJECTION_VIEW = "redirect:/rejectApplication?applicationId=";
    private static final String OFFER_RECOMMENDATION_VIEW = "redirect:/offerRecommendation?applicationId=";
    private static final String APPROVAL_VIEW = "redirect:/approval/moveToApproval?action=firstLoad&applicationId=";
    private static final String INTERVIEW_VIEW = "redirect:/interview/moveToInterview?applicationId=";
    private static final String REVIEW_VIEW = "redirect:/review/moveToReview?applicationId=";
    private static final String STATE_TRANSITION_VIEW = "private/staff/admin/state_transition";

    @Autowired
    private StateService stateService;

    @Autowired
    private PermissionsService permissionsService;

    public String resolveView(ApplicationForm applicationForm) {
        return resolveView(applicationForm, null);
    }

    public String resolveView(ApplicationForm application, String action) {

        if (!application.getProgram().isEnabled()) {
            return REJECTION_VIEW + application.getApplicationNumber() + "&rejectionId=" + REJECTION_REASON_WHEN_PROGAMME_EXPIRED.toString()
                    + "&rejectionIdForced=true";
        } else if ("abort".equals(action)) {
            return STATE_TRANSITION_VIEW;
        } else {
            PrismState status = application.getState().getId();
            if (status != null) {
                switch (status) {
                case APPLICATION_REVIEW:
                    return REVIEW_VIEW + application.getApplicationNumber();
                case APPLICATION_INTERVIEW:
                    return INTERVIEW_VIEW + application.getApplicationNumber();
                case APPLICATION_APPROVAL:
                    return APPROVAL_VIEW + application.getApplicationNumber();
                case APPLICATION_REJECTED:
                    return REJECTION_VIEW + application.getApplicationNumber();
                case APPLICATION_APPROVED:
                    return OFFER_RECOMMENDATION_VIEW + application.getApplicationNumber();
                default:
                }
            }
            return STATE_TRANSITION_VIEW;
        }

    }

    public List<PrismState> getAssignableNextStati(final ApplicationForm application, final User user) {
        // TODO write a query
        return null;
    }

}
