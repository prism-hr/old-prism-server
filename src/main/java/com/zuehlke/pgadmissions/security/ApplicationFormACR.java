package com.zuehlke.pgadmissions.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;

// TODO: This is work in progress (ked)

@Component
public class ApplicationFormACR extends AbstractAccessControlRule {

    private final ProgramACR programACR;
    
    public ApplicationFormACR() {
        this(null);
    }
    
    @Autowired
    public ApplicationFormACR(final ProgramACR programACR) {
        this.programACR = programACR;
    }
    
    @Override
    public boolean supports(final Class<?> clazz) {
        return ApplicationForm.class.equals(clazz);
    }

    @Override
    public boolean hasPermission(final Object object, final UserAction action, final RegisteredUser currentUser) {
        ApplicationForm form = (ApplicationForm) object;
        switch (action) {
        case CAN_SEE_APPLICATION:
            return canSeeApplication(form, currentUser);
        case ADMIN_RIGHTS_ON_APPLICATION:
            return hasAdminRightsOnApplication(form, currentUser);
        case STAFF_RIGHTS_ON_APPLICATION:
            return hasStaffRightsOnApplication(form, currentUser);
        default:
            return false;
        }
    }
    
    public boolean canSeeApplication(final ApplicationForm form, final RegisteredUser user) {
        if (isInRole(Authority.SUPERADMINISTRATOR, user)) {
            return true;
        }
        
        if (areEqual(user, form.getApplicant())) {
            return true;
        }

        if (isStatus(ApplicationFormStatus.UNSUBMITTED, form) && isNotInRole(Authority.APPLICANT, user)) {
            return false;
        }

        if (isApplicationAdministrator(form, user)) {
            return true;
        }

        if (isInRole(Authority.ADMINISTRATOR, user) && isProgrammeAdministrator(form, user)) {
            return true;
        }

        if (isStatus(ApplicationFormStatus.REVIEW, form)) {
            if (isReviewerInReviewRound(form.getLatestReviewRound(), user)) {
                return true;
            }
        }

        if (isStatus(ApplicationFormStatus.INTERVIEW, form)) {
            if (isInterviewerInInterview(form.getLatestInterview(), user)) {
                return true;
            }
        }
        
        if (isStatusEither(form, ApplicationFormStatus.APPROVAL, ApplicationFormStatus.APPROVED)) {
            if (isSupervisorInApprovalRound(form.getLatestApprovalRound(), user)) {
                return true;
            }
        }
        
        if (isInRole(Authority.APPROVER, user) && isStatus(ApplicationFormStatus.APPROVAL, form)) {
            if (programACR.isApprover(form.getProgram(), user)) {
                return true;
            }
        }
        
        if (isInRole(Authority.REFEREE, user)) {
            for (Referee referee : form.getReferees()) {
                if (!referee.isDeclined()) {
                    if (areEqual(referee.getUser(), user) || containsReferee(referee, user.getReferees())) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    public boolean hasAdminRightsOnApplication(final ApplicationForm form, final RegisteredUser user) {
        if (isStatus(ApplicationFormStatus.UNSUBMITTED, form)) {
            return false;
        }
        
        if (isInRole(Authority.SUPERADMINISTRATOR, user)) {
            return true;
        }
        
        if (isApplicationAdministrator(form, user)) {
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
        
        if (isInRoleInProgramme(Authority.APPROVER, form.getProgram(), user)) {
            return true;
        }
        
        return false;
    }
}
