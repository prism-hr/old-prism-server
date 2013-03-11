package com.zuehlke.pgadmissions.security;

import java.util.ArrayList;
import java.util.List;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewRound;
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
            if (areEqual(entry.getUser(), user)) {
                return true;
            }
        }
        return false;
    }
    
    protected boolean containsReviewer(final RegisteredUser user, final List<Reviewer> reviewers) {
        for (Reviewer entry : reviewers) {
            if (areEqual(entry.getUser(), user)) {
                return true;
            }
        }
        return false;
    }
    
    protected boolean containsSupervisor(final RegisteredUser user, final List<Supervisor> supervisors) {
        for (Supervisor entry : supervisors) {
            if (areEqual(entry.getUser(), user)) {
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
    
    protected boolean isNotInRole(final Authority authority, final RegisteredUser user) {
        return !isInRole(authority, user);
    }
    
    protected boolean isInRoleInProgramme(final Authority authority, final Program programme, final RegisteredUser user) {
        if (Authority.SUPERADMINISTRATOR == authority && isInRole(Authority.SUPERADMINISTRATOR, user)) {
            return true;
        }
        return getAuthoritiesForProgramme(programme, user).contains(authority);
    }
    
    protected List<Authority> getAuthoritiesForProgramme(final Program program, final RegisteredUser user) {
        List<Authority> authorities = new ArrayList<Authority>();
        if (containsProgramme(program, user.getProgramsOfWhichAdministrator())) {
            authorities.add(Authority.ADMINISTRATOR);
        }
        if (containsProgramme(program, user.getProgramsOfWhichReviewer())) {
            authorities.add(Authority.REVIEWER);
        }
        if (containsProgramme(program, user.getProgramsOfWhichInterviewer())) {
            authorities.add(Authority.INTERVIEWER);
        }
        if (containsProgramme(program, user.getProgramsOfWhichApprover())) {
            authorities.add(Authority.APPROVER);
        }
        if (containsProgramme(program, user.getProgramsOfWhichSupervisor())) {
            authorities.add(Authority.SUPERVISOR);
        }
        return authorities;
    }

    protected boolean isNotInRoleInProgramme(final Authority authority, final Program programme, final RegisteredUser user) {
        return !isInRoleInProgramme(authority, programme, user);
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
    
    protected boolean isStatusEither(final ApplicationForm form, final ApplicationFormStatus... stati) {
        for (ApplicationFormStatus status : stati) {
            if (form.getStatus().equals(status)) {
                return true;
            }
        }
        return false;
    }
    
    protected boolean isReviewerInReviewRound(final ReviewRound reviewRound, final RegisteredUser user) {
        if (reviewRound == null) {
            return false;
        }
        return containsReviewer(user, reviewRound.getReviewers());
    }

    protected boolean isInterviewerInInterview(final Interview interview, final RegisteredUser user) {
        if (interview == null) {
            return false;
        }
        return containsInterviewer(user, interview.getInterviewers());
    }

    protected boolean isSupervisorInApprovalRound(final ApprovalRound approvalRound, final RegisteredUser user) {
        if (approvalRound == null) {
            return false;
        }
        return containsSupervisor(user, approvalRound.getSupervisors());
    }
    
    protected boolean isApplicationAdministrator(final ApplicationForm form, final RegisteredUser user) {
        return areEqual(user, form.getApplicationAdministrator());
    }
    
    protected boolean isProgrammeAdministrator(final ApplicationForm form, final RegisteredUser user) {
        return containsUser(user, form.getProgram().getAdministrators());
    }
    
    protected boolean isPastOrPresentReviewerOfApplication(final ApplicationForm form, final RegisteredUser user) {
        for (ReviewRound reviewRound : form.getReviewRounds()) {
            for (Reviewer reviewer : reviewRound.getReviewers()) {
                if (areEqual(user,  reviewer.getUser())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    protected boolean isPastOrPresentInterviewerOfApplication(final ApplicationForm form, final RegisteredUser user) {
        for (Interview interview : form.getInterviews()) {
            for (Interviewer interviewer : interview.getInterviewers()) {
                if (areEqual(user, interviewer.getUser())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    protected boolean isPastOrPresentSupervisorOfApplication(final ApplicationForm form, final RegisteredUser user) {
        for (ApprovalRound approvalRound : form.getApprovalRounds()) {
            for (Supervisor supervisor : approvalRound.getSupervisors()) {
                if (areEqual(user, supervisor.getUser())) {
                    return true;
                }
            }
        }
        return false;
    }
}
