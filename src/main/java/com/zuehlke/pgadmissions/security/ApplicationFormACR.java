package com.zuehlke.pgadmissions.security;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;

// TODO: This is work in progress (ked)

@Component
public class ApplicationFormACR extends AbstractAccessControlRule {

    public static final String ACTION_SEE = "SEE";
    
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
    public boolean hasPermission(final Object object, final String action, final RegisteredUser currentUser) {
        ApplicationForm applicationForm = (ApplicationForm) object;
        
        if (StringUtils.equalsIgnoreCase(ACTION_SEE, action)) {
            return canSee(applicationForm, currentUser);
        }
        
        return false;
    }
    
    public boolean canSee(final ApplicationForm form, final RegisteredUser user) {
        if (isInRole(Authority.SUPERADMINISTRATOR, user)) {
            return true;
        }
        
        if (areEqual(user, form.getApplicant())) {
            return true;
        }

        if (isStatus(ApplicationFormStatus.UNSUBMITTED, form) && isNotInRole(user, Authority.APPLICANT)) {
            return false;
        }

        if (areEqual(user, form.getApplicationAdministrator())) {
            return true;
        }

        if (isInRole(Authority.ADMINISTRATOR, user) && containsUser(user, form.getProgram().getAdministrators())) {
            return true;
        }

        if (isStatus(ApplicationFormStatus.REVIEW, form)) {
            ReviewRound latestReviewRound = form.getLatestReviewRound();
            if (latestReviewRound != null && containsReviewer(user, latestReviewRound.getReviewers())) {
                return true;
            }
        }

        if (isStatus(ApplicationFormStatus.INTERVIEW, form)) {
            Interview latestInterview = form.getLatestInterview();
            if (latestInterview != null && containsInterviewer(user, latestInterview.getInterviewers())) {
                return true;
            }
        }
        
        if (isStatus(Arrays.asList(ApplicationFormStatus.APPROVAL, ApplicationFormStatus.APPROVED), form)) {
            ApprovalRound latestApprovalRound = form.getLatestApprovalRound();
            if (latestApprovalRound != null && containsSupervisor(user, latestApprovalRound.getSupervisors())) {
                return true;
            }
        }
        
        if (isInRole(Authority.APPROVER, user) && isStatus(ApplicationFormStatus.APPROVAL, form)) {
            if (programACR.isApprover(form.getProgram(), user)) {
                return true;
            }
        }
        
        // TODO: Clean this up as well
        if (isInRole(Authority.REFEREE, user)) {
            List<Referee> refereesList = form.getReferees();
            for (Referee referee : refereesList) {
                if (!referee.isDeclined() && referee.getUser() != null) {
                    if (referee.getUser().getId().equals(user.getId()) || (containsReferee(referee, user.getReferees()))) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
