package com.zuehlke.pgadmissions.domain;

import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;

/**
 * Methods used by the domain objects to do authorisation/security decisions.
 */
public abstract class Authorisable extends AbstractAuthorisationAPI {

    public boolean canEditApplicationAsApplicant(final ApplicationForm form, final RegisteredUser user) {
        return user.getId() == form.getApplicant().getId() && !form.isTerminated() && form.getIsEditableByApplicant();
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

    public boolean canSeeReference(final ReferenceComment comment, final RegisteredUser user) {
        // TODO this method is used only in FileDownloadController, move it to a service
        if (isInRole(user, Authority.APPLICANT)) {
            return false;
        }

//        if (canNotSeeApplication(comment.getReferee().getApplication(), user)) {
//            return false;
//        }
//
//        if (user.isRefereeOfApplicationForm(comment.getReferee().getApplication()) && areNotEqual(user, comment.getReferee().getUser())) {
//            return false;
//        }

        return true;
    }

    public boolean canSeeRestrictedInformation(final ApplicationForm form, final RegisteredUser user) {
        // TODO this method is used only in main_application_page.ftl, move it into service
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
