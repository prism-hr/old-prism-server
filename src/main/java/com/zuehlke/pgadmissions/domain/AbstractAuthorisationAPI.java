package com.zuehlke.pgadmissions.domain;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;

/**
 * A small API which facilitates the writing of complex authorisation rules.
 */
public abstract class AbstractAuthorisationAPI {

    private final Logger log = LoggerFactory.getLogger(AbstractAuthorisationAPI.class);

    protected boolean containsUser(final RegisteredUser user, final List<RegisteredUser> users) {
        for (RegisteredUser entry : users) {
            if (areEqual(entry, user)) {
                return true;
            }
        }
        return false;
    }

    protected boolean containsProgramme(final Program programme, final List<Program> programmes) {
        if (programme == null) {
            return false;
        }

        for (Program entry : programmes) {
            if (programme != null && entry.getId().equals(programme.getId())) {
                return true;
            }
        }
        return false;
    }

    protected boolean containsReferee(final Referee referee, final List<Referee> referees) {
        for (Referee entry : referees) {
            if (referee != null && entry.getId().equals(referee.getId())) {
                return true;
            }
        }
        return false;
    }

    public boolean isInRole(final RegisteredUser user, final String strAuthority) {
        try {
            return isInRole(user, Authority.valueOf(strAuthority));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    public boolean isInRole(final RegisteredUser user, final Authority authority) {
        for (Role role : user.getRoles()) {
            if (role.getId() == authority) {
                return true;
            }
        }
        return false;
    }

    public boolean isNotInRole(final RegisteredUser user, final Authority authority) {
        return !isInRole(user, authority);
    }

    public boolean isNotInRole(final RegisteredUser user, final String authority) {
        return !isInRole(user, authority);
    }

    public boolean isInRoleInProgramme(final Program programme, final RegisteredUser user, final String authority) {
        try {
            return getAuthoritiesForProgramme(programme, user).contains(Authority.valueOf(authority));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    public boolean isInRoleInProgramme(final Program programme, final RegisteredUser user, final Authority authority) {
        if (programme == null) {
            return false;
        }

        if (Authority.SUPERADMINISTRATOR == authority && isInRole(user, Authority.SUPERADMINISTRATOR)) {
            return true;
        }
        return getAuthoritiesForProgramme(programme, user).contains(authority);
    }

    public List<Authority> getAuthoritiesForProgramme(final Program programme, final RegisteredUser user) {
        List<Authority> authorities = new ArrayList<Authority>();
        if (containsProgramme(programme, user.getProgramsOfWhichAdministrator())) {
            authorities.add(Authority.ADMINISTRATOR);
        }
        if (containsProgramme(programme, user.getProgramsOfWhichApprover())) {
            authorities.add(Authority.APPROVER);
        }
        if (containsProgramme(programme, user.getProgramsOfWhichViewer())) {
            authorities.add(Authority.VIEWER);
        }
        return authorities;
    }

    public boolean isNotInRoleInProgramme(final Program programme, final RegisteredUser user, final Authority authority) {
        return !isInRoleInProgramme(programme, user, authority);
    }

    protected boolean areEqual(RegisteredUser u1, RegisteredUser u2) {
        if (u1 == null || u2 == null) {
            return false;
        }
        return u1.getId().equals(u2.getId());
    }

    protected boolean areNotEqual(RegisteredUser u1, RegisteredUser u2) {
        return !areEqual(u1, u2);
    }

    protected boolean isStatus(final ApplicationForm form, final ApplicationFormStatus... stati) {
        for (ApplicationFormStatus status : stati) {
            if (form.getStatus().equals(status)) {
                return true;
            }
        }
        return false;
    }

    public boolean isApplicant(final ApplicationForm form, final RegisteredUser user) {
        return areEqual(user, form.getApplicant());
    }

    public boolean isProjectAdministrator(final ApplicationForm form, final RegisteredUser user) {
        Project project = form.getProject();
        return project != null && (areEqual(user, project.getAdministrator()) || areEqual(user, project.getPrimarySupervisor()));
    }

    public boolean isProgrammeAdministrator(final ApplicationForm form, final RegisteredUser user) {
        return containsUser(user, form.getProgram().getAdministrators());
    }

    public boolean isViewerOfProgramme(final ApplicationForm form, final RegisteredUser user) {
        return containsUser(user, form.getProgram().getViewers());
    }

    public boolean isRefereeOfApplication(final ApplicationForm form, final RegisteredUser user) {
        return isInRole(user, Authority.REFEREE) && user.hasRefereesInApplicationForm(form);
    }

    public boolean isAdminInProgramme(final Program programme, final RegisteredUser user) {
        if (programme == null) {
            return false;
        }

        if (isNotInRole(user, Authority.ADMINISTRATOR)) {
            return false;
        }

        if (containsUser(user, programme.getAdministrators())) {
            return true;
        }
        return false;
    }

    public boolean isApproverInProgramme(final Program programme, final RegisteredUser user) {
        if (programme == null) {
            return false;
        }

        if (isNotInRole(user, Authority.APPROVER)) {
            return false;
        }
        return containsUser(user, programme.getApprovers());
    }

    public boolean isViewerInProgramme(final Program programme, final RegisteredUser user) {
        if (programme == null) {
            return false;
        }

        if (isNotInRole(user, Authority.VIEWER)) {
            return false;
        }
        return containsUser(user, programme.getViewers());
    }
}