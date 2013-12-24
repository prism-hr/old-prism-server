package com.zuehlke.pgadmissions.domain;

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public abstract class Authorisable extends AbstractAuthorisationAPI {

    public Authorisable() {
    }

    public boolean canNotSeeApplication(final ApplicationForm form, final RegisteredUser user) {
        return !canSeeApplication(form, user);
    }

    public boolean canSeeApplication(final ApplicationForm form, final RegisteredUser user) {
        return ActionsProvider.checkActionAvailable(form, user, ApplicationFormAction.VIEW_EDIT) ||
        		ActionsProvider.checkActionAvailable(form, user, ApplicationFormAction.VIEW);
    }

    public boolean canEditApplicationAsApplicant(final ApplicationForm form, final RegisteredUser user) {
        return user.getId() == form.getApplicant().getId() && 
        		ActionsProvider.checkActionAvailable(form, user, ApplicationFormAction.VIEW_EDIT);
    }

    public boolean canEditApplicationAsAdministrator(final ApplicationForm form, final RegisteredUser user) {
        return (user.isInRole(Authority.SUPERADMINISTRATOR) || 
        		user.isApplicationAdministrator(form) || 
        		user.isAdminInProgramme(form.getProgram())) &&
        		ActionsProvider.checkActionAvailable(form, user, ApplicationFormAction.VIEW_EDIT);
    }

    public boolean hasAdminRightsOnApplication(final ApplicationForm form, final RegisteredUser user) {        
        return !isStatus(form, ApplicationFormStatus.UNSUBMITTED) &&
    			(isInRole(user, Authority.SUPERADMINISTRATOR) ||
    			isProjectAdministrator(form, user) ||
    			isProgrammeAdministrator(form, user));
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
