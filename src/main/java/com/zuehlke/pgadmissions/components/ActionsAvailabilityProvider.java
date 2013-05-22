package com.zuehlke.pgadmissions.components;

import static com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus.APPROVAL;
import static com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus.INTERVIEW;
import static com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus.REVIEW;
import static com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus.VALIDATION;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;

@Component
public class ActionsAvailabilityProvider {

    public boolean canPostComment(final RegisteredUser user, final ApplicationForm application) {
        return user.hasAdminRightsOnApplication(application) || user.isViewerOfProgramme(application) || user.isInRole(Authority.ADMITTER);
    }

    public boolean canAddReview(final RegisteredUser user, final ApplicationForm application) {
        return application.getStatus() == REVIEW && user.isReviewerInLatestReviewRoundOfApplicationForm(application)
                && !user.hasRespondedToProvideReviewForApplicationLatestRound(application);
    }

    public boolean canReviseApprovalAsAdministrator(final RegisteredUser user, final ApplicationForm application) {
        return application.isInState(ApplicationFormStatus.APPROVAL) && !application.isPendingApprovalRestart()
                && user.isInRoleInProgram(Authority.ADMINISTRATOR, application.getProgram())
                && user.isNotInRoleInProgram(Authority.APPROVER, application.getProgram()) && user.isNotInRole(Authority.SUPERADMINISTRATOR);
    }

    public boolean canApproveAsApprover(final RegisteredUser user, final ApplicationForm application) {
        return application.isInState(ApplicationFormStatus.APPROVAL) && user.isInRoleInProgram(Authority.APPROVER, application.getProgram());
    }

    public boolean canApproveAsSuperadministrator(final RegisteredUser user, final ApplicationForm application) {
        return application.isInState(ApplicationFormStatus.APPROVAL) && user.isInRole(Authority.SUPERADMINISTRATOR);
    }

    public boolean canAddReference(final RegisteredUser user, final ApplicationForm application) {
        return application.isSubmitted() && !application.isTerminated() && user.isRefereeOfApplicationForm(application)
                && !user.getRefereeForApplicationForm(application).hasResponded();
    }

    public boolean canAddInterviewFeedback(final RegisteredUser user, final ApplicationForm application) {
        Interview interview = application.getLatestInterview();
        return application.getStatus() == INTERVIEW && user.isInterviewerOfApplicationForm(application) && interview.isScheduled()
                && !user.hasRespondedToProvideInterviewFeedbackForApplicationLatestRound(application);
    }

    public boolean canProvideInterviewAvailability(final RegisteredUser user, final ApplicationForm application, ApplicationFormStatus nextStatus) {
        Interview interview = application.getLatestInterview();
        return application.getStatus() == INTERVIEW && nextStatus == null && interview.isScheduling() && interview.isParticipant(user)
                && !interview.getParticipant(user).getResponded();
    }

    public boolean canConfirmSupervision(final RegisteredUser user, final ApplicationForm application) {
        if (application.getStatus() == APPROVAL) {
            Supervisor primarySupervisor = application.getLatestApprovalRound().getPrimarySupervisor();
            if (primarySupervisor != null && user == primarySupervisor.getUser() && !primarySupervisor.hasResponded()) {
                return true;
            }
        }
        return false;
    }

    public boolean canAdministerInterview(final RegisteredUser user, final ApplicationForm application, ApplicationFormStatus nextStatus) {
        // application not yet in interview stage, interview is next
        return nextStatus == ApplicationFormStatus.INTERVIEW && user.isApplicationAdministrator(application);
    }

    public boolean canReviseApproval(final RegisteredUser user, final ApplicationForm application) {
        return application.isPendingApprovalRestart() && user.hasAdminRightsOnApplication(application);
    }

    public boolean canWithdraw(final RegisteredUser user, final ApplicationForm application) {
        return !application.isTerminated() && user == application.getApplicant();
    }

    public boolean isEligibilityConfirmationAwaiting(final ApplicationForm application) {
        return application.getAdminRequestedRegistry() != null
                && (application.isNotInState(ApplicationFormStatus.WITHDRAWN) && application.isNotInState(ApplicationFormStatus.REJECTED));
    }

    public boolean canConfirmEligibility(final RegisteredUser user, final ApplicationForm application) {
        return user.isInRole(Authority.ADMITTER) && !application.hasConfirmElegibilityComment();
    }

    public boolean canConfirmInterviewTime(final RegisteredUser user, final ApplicationForm application, ApplicationFormStatus nextStatus) {
        Interview interview = application.getLatestInterview();
        return application.isInState(ApplicationFormStatus.INTERVIEW) && nextStatus == null && user.hasAdminRightsOnApplication(application)
                && interview.isScheduling() && (user.isApplicationAdministrator(application) || user.hasAdminRightsOnApplication(application));
    }

    public boolean canEvaluateInterviewFeedback(final RegisteredUser user, final ApplicationForm application, ApplicationFormStatus nextStatus) {
        return application.isInState(ApplicationFormStatus.INTERVIEW) && nextStatus == null && user.hasAdminRightsOnApplication(application);
    }

    public boolean canEvaluateReviews(final RegisteredUser user, final ApplicationForm application) {
        return application.isInState(ApplicationFormStatus.REVIEW) && user.hasAdminRightsOnApplication(application);
    }

    public boolean canValidate(final RegisteredUser user, final ApplicationForm application) {
        return application.getStatus() == VALIDATION && user.hasAdminRightsOnApplication(application);
    }

}
