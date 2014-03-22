package com.zuehlke.pgadmissions.domain;

import java.util.Arrays;

import org.apache.commons.lang.BooleanUtils;

import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;

/**
 * Methods used by the domain objects to do authorisation/security decisions.
 */
public abstract class Authorisable extends AbstractAuthorisationAPI {

    public Authorisable() {
    }

    public boolean canNotSeeApplication(final ApplicationForm form, final RegisteredUser user) {
        return !canSeeApplication(form, user);
    }

    public boolean canSeeApplication(final ApplicationForm form, final RegisteredUser user) {
        if (isStatus(form, ApplicationFormStatus.UNSUBMITTED) && isNotInRole(user, Authority.APPLICANT)) {
            return false;
        }

        if (isInRole(user, Authority.SUPERADMINISTRATOR)) {
            return true;
        }

        if (isInRole(user, Authority.ADMITTER)) {
            return true;
        }

        if (isApplicationAdministrator(form, user)) {
            return true;
        }

        if (isInRole(user, Authority.ADMINISTRATOR) && isProgrammeAdministrator(form, user)) {
            return true;
        }

        if (isProjectAdministrator(form, user)) {
            return true;
        }

        if (isStatus(form, ApplicationFormStatus.REVIEW)) {
            if (isReviewerInReviewRound(form.getLatestReviewRound(), user)) {
                return true;
            }
        }

        if (isStatus(form, ApplicationFormStatus.INTERVIEW)) {
            if (isInterviewerInInterview(form.getLatestInterview(), user)) {
                return true;
            }
        }

        if (isStatus(form, ApplicationFormStatus.APPROVAL, ApplicationFormStatus.APPROVED)) {
            if (isSupervisorInApprovalRound(form.getLatestApprovalRound(), user)) {
                return true;
            }
        }

        if (isInRole(user, Authority.APPROVER) && isStatus(form, ApplicationFormStatus.APPROVAL)) {
            if (form.getProgram().isApprover(user)) {
                return true;
            }
        }

        if (isInRole(user, Authority.REFEREE)) {
            for (Referee referee : form.getReferees()) {
                if (!referee.isDeclined()) {
                    if (areEqual(referee.getUser(), user) || containsReferee(referee, user.getReferees())) {
                        return true;
                    }
                }
            }
        }

        if (isInRole(user, Authority.VIEWER) && isViewerOfProgramme(form, user)) {
            return true;
        }

        if (areEqual(user, form.getApplicant())) {
            return true;
        }

        return false;
    }

    public boolean canSubmitApplicationAsApplicant(final ApplicationForm form, final RegisteredUser user) {
        return user.getId() == form.getApplicant().getId() && !form.getStatus().isSubmitted();
    }

    public boolean canUpdateApplicationAsApplicant(final ApplicationForm form, final RegisteredUser user) {
        return user.getId() == form.getApplicant().getId()
                && Arrays.asList(ApplicationFormStatus.VALIDATION, ApplicationFormStatus.REVIEW, ApplicationFormStatus.INTERVIEW).contains(form.getStatus());
    }

    public boolean canUpdateApplicationAsSuperadministrator(final ApplicationForm form, final RegisteredUser user) {
        return user.isInRole(Authority.SUPERADMINISTRATOR)
                && Arrays.asList(ApplicationFormStatus.APPROVED, ApplicationFormStatus.REJECTED, ApplicationFormStatus.WITHDRAWN).contains(form.getStatus())
                && BooleanUtils.isFalse(form.isExported());
    }

    public boolean canUpdateApplicationAsAdministrator(final ApplicationForm form, final RegisteredUser user) {
        return (user.isInRole(Authority.SUPERADMINISTRATOR) || user.isAdminInProgramme(form.getProgram()))
                && Arrays.asList(ApplicationFormStatus.REVIEW, ApplicationFormStatus.INTERVIEW).contains(form.getStatus());
    }

    public boolean hasAdminRightsOnApplication(final ApplicationForm form, final RegisteredUser user) {
        if (isStatus(form, ApplicationFormStatus.UNSUBMITTED)) {
            return false;
        }

        if (isInRole(user, Authority.SUPERADMINISTRATOR)) {
            return true;
        }

        if (isProjectAdministrator(form, user)) {
            return true;
        }

        if (isProgrammeAdministrator(form, user)) {
            return true;
        }

        return false;
    }

    public boolean hasStaffRightsOnApplication(final ApplicationForm form, final RegisteredUser user) {
        if (hasAdminRightsOnApplication(form, user)) {
            return true;
        }

        if (isPastOrPresentReviewerOfApplication(form, user)) {
            return true;
        }

        if (isPastOrPresentInterviewerOfApplication(form, user)) {
            return true;
        }

        if (isPastOrPresentSupervisorOfApplication(form, user)) {
            return true;
        }

        if (isInRoleInProgramme(form.getProgram(), user, Authority.APPROVER)) {
            return true;
        }

        if (isInRoleInProgramme(form.getProgram(), user, Authority.VIEWER)) {
            return true;
        }

        return false;
    }

    public boolean canSeeReference(final ReferenceComment comment, final RegisteredUser user) {
        if (isInRole(user, Authority.APPLICANT)) {
            return false;
        }

        if (canNotSeeApplication(comment.getReferee().getApplication(), user)) {
            return false;
        }

        if (user.isRefereeOfApplicationForm(comment.getReferee().getApplication()) && areNotEqual(user, comment.getReferee().getUser())) {
            return false;
        }

        return true;
    }

    public boolean canSeeRestrictedInformation(final ApplicationForm form, final RegisteredUser user) {
        if (user.isApplicant(form)) {
            return true;
        }
        if (user.hasAdminRightsOnApplication(form)) {
            return true;
        }
        if (isInRole(user, Authority.APPROVER)) {
            return true;
        }
        if (isInRole(user, Authority.ADMITTER)) {
            return true;
        }
        return false;
    }
}
