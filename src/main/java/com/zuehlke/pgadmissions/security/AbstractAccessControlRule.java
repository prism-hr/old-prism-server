package com.zuehlke.pgadmissions.security;

import java.util.List;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;

//TODO: This is work in progress (ked)

public abstract class AbstractAccessControlRule implements AccessControlRuleSupport {

    protected boolean containsUser(final RegisteredUser user, final List<RegisteredUser> users) {
        for (RegisteredUser entry : users) {
            if (areEqual(entry, user)) {
                return true;
            }
        }
        return false;
    }
    
    protected boolean containsProgramme(final Program program, final List<Program> programmes) {
        for (Program entry : programmes) {
            if (program != null && entry.getId().equals(program.getId())) {
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
    
    protected boolean containsInterviewer(final RegisteredUser user, final List<Interviewer> interviewers) {
        for (Interviewer entry : interviewers) {
            if (user != null && areEqual(entry.getUser(), user)) {
                return true;
            }
        }
        return false;
    }
    
    protected boolean containsReviewer(final RegisteredUser user, final List<Reviewer> reviewers) {
        for (Reviewer entry : reviewers) {
            if (user != null && areEqual(entry.getUser(), user)) {
                return true;
            }
        }
        return false;
    }
    
    protected boolean containsSupervisor(final RegisteredUser user, final List<Supervisor> supervisors) {
        for (Supervisor entry : supervisors) {
            if (user != null && areEqual(entry.getUser(), user)) {
                return true;
            }
        }
        return false;
    }
    
    protected boolean isInRole(final Authority authority, final RegisteredUser user) {
        for (Role role : user.getRoles()) {
            if (role.getAuthorityEnum() == authority) {
                return true;
            }
        }
        return false;
    }
    
    protected boolean isNotInRole(final RegisteredUser user, final Authority authority) {
        return !isInRole(authority, user);
    }
    
    protected boolean areEqual(RegisteredUser u1, RegisteredUser u2) {
        if (u1 == null || u2 == null) {
            return false;
        }
        return u1.getId().equals(u2.getId());
    }
    
    protected boolean isStatus(final ApplicationFormStatus status, final ApplicationForm form) {
        return status.equals(form.getStatus());
    }
    
    protected boolean isStatus(final List<ApplicationFormStatus> stati, final ApplicationForm form) {
        for (ApplicationFormStatus status : stati) {
            if (form.getStatus().equals(status)) {
                return true;
            }
        }
        return false;
    }
}
