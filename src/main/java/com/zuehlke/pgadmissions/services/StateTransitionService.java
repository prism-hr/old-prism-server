package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

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
            ApplicationFormStatus nextStatus = application.getNextStatus().getId();
            if (nextStatus != null) {
                switch (nextStatus) {
                case REVIEW:
                    return REVIEW_VIEW + application.getApplicationNumber();
                case INTERVIEW:
                    return INTERVIEW_VIEW + application.getApplicationNumber();
                case APPROVAL:
                    return APPROVAL_VIEW + application.getApplicationNumber();
                case REJECTED:
                    return REJECTION_VIEW + application.getApplicationNumber();
                case APPROVED:
                    return OFFER_RECOMMENDATION_VIEW + application.getApplicationNumber();
                default:
                }
            }
            return STATE_TRANSITION_VIEW;
        }

    }

    public List<ApplicationFormStatus> getAssignableNextStati(final ApplicationForm application, final User user) {
        ApplicationFormStatus status = application.getStatus().getId();
        // FIXME check permissions
        boolean canAdministerApplication = true; // permissionsService.canAdministerApplication(application, user);
        boolean canApproveApplication = true; // permissionsService.canApproveApplication(application, user);
        List<ApplicationFormStatus> nextStati = new ArrayList<ApplicationFormStatus>();

        if (stateService.getAllStatesThatApplicationsCanBeAssignedFrom().contains(status) && (canAdministerApplication || canApproveApplication)) {
            List<ApplicationFormStatus> assignableNextStati = stateService.getAllStatesThatApplicationsCanBeAssignedTo();

            for (ApplicationFormStatus assignableNextStatus : assignableNextStati) {
                if ((canApproveApplication || (assignableNextStatus != ApplicationFormStatus.APPROVAL && canAdministerApplication))
                        && assignableNextStatus != application.getNextStatus().getId()) {
                    nextStati.add(assignableNextStatus);
                }
            }
        }

        return nextStati;
    }

}
