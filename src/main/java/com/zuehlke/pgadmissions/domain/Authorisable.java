package com.zuehlke.pgadmissions.domain;

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
    
    public boolean hasAdminRightsOnApplication(final ApplicationForm form, final RegisteredUser user) {
        if (isStatus(form, ApplicationFormStatus.UNSUBMITTED)) {
            return false;
        }
        
        if (isInRole(user, Authority.SUPERADMINISTRATOR)) {
            return true;
        }
        
        if (isApplicationAdministrator(form, user)) {
            return true;
        }
        
        if (isProgrammeAdministrator(form, user)) {
            return true;
        }
        
        if (isApplicationAdmitter(form, user)) {
            return true;
        }

        return false;
    }
    
    public boolean isApplicationAdmitter(ApplicationForm form, RegisteredUser user) {
        return form.isRegistryUsersDueNotification() && user.isInRole(Authority.ADMITTER);
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
}
