package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormUserRoleDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormUserRole;
import com.zuehlke.pgadmissions.domain.ApplicationFormUserRoleId;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewParticipant;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.Authority;

@Service
@Transactional
public class ApplicationFormUserRoleService {

    private final ApplicationFormUserRoleDAO applicationFormUserRoleDAO;

    private final RoleDAO roleDAO;

    private final UserDAO userDAO;

    public ApplicationFormUserRoleService() {
        this(null, null, null);
    }

    @Autowired
    public ApplicationFormUserRoleService(ApplicationFormUserRoleDAO applicationFormUserRoleDAO, RoleDAO roleDAO, UserDAO userDAO) {
        this.applicationFormUserRoleDAO = applicationFormUserRoleDAO;
        this.roleDAO = roleDAO;
        this.userDAO = userDAO;
    }

    public void applicationSubmitted(ApplicationForm applicationForm) {
        createApplicationFormUserRole(applicationForm, applicationForm.getApplicant(), Authority.APPLICANT, true);

        for (RegisteredUser superAdministrator : userDAO.getSuperadministrators()) {
            createApplicationFormUserRole(applicationForm, superAdministrator, Authority.SUPERADMINISTRATOR, false);
        }

        for (RegisteredUser administrator : applicationForm.getProgram().getAdministrators()) {
            createApplicationFormUserRole(applicationForm, administrator, Authority.ADMINISTRATOR, true);
        }

        if (applicationForm.getProject() != null) {
            RegisteredUser projectAdministrator = applicationForm.getProject().getAdministrator();
            if (projectAdministrator != null) {
                createApplicationFormUserRole(applicationForm, projectAdministrator, Authority.PROJECTADMINISTRATOR, true);
            }
        }

        for (RegisteredUser approver : applicationForm.getProgram().getApprovers()) {
            createApplicationFormUserRole(applicationForm, approver, Authority.APPROVER, false);
        }
    }

    public void validationStageCompleted(ApplicationForm application) {
        for (Referee referee : application.getReferees()) {
            createApplicationFormUserRole(application, referee.getUser(), Authority.REFEREE, true);
        }
        for (RegisteredUser admitter : userDAO.getAdmitters()) {
            createApplicationFormUserRole(application, admitter, Authority.ADMITTER, true);
        }
    }

    public void stateChanged(ApplicationForm application) {
        List<ApplicationFormUserRole> roles = applicationFormUserRoleDAO.findByApplicationFormAndAuthorities(application, Authority.REVIEWER,
                Authority.INTERVIEWER, Authority.SUPERVISOR, Authority.INTERVIEWPARTICIPANTAPPLICANT, Authority.INTERVIEWPARTICIPANTINTERVIEWER);
        for (ApplicationFormUserRole role : roles) {
            role.setCurrentRole(false);
        }
    }

    public void movedToReviewStage(ReviewRound reviewRound) {
        for (Reviewer reviewer : reviewRound.getReviewers()) {
            createApplicationFormUserRole(reviewRound.getApplication(), reviewer.getUser(), Authority.REVIEWER, true);
        }
    }

    public void movedToInterviewStage(Interview interview) {
        ApplicationForm application = interview.getApplication();
        if (interview.isScheduling()) {
            for (InterviewParticipant participant : interview.getParticipants()) {
                boolean isApplicant = participant.getUser().getId().equals(application.getApplicant().getId());
                Authority authority = isApplicant ? Authority.INTERVIEWPARTICIPANTAPPLICANT : Authority.INTERVIEWPARTICIPANTINTERVIEWER;
                createApplicationFormUserRole(application, participant.getUser(), authority, true);
            }
        } else {
            for (Interviewer interviewer : interview.getInterviewers()) {
                createApplicationFormUserRole(application, interviewer.getUser(), Authority.INTERVIEWER, true);
            }
        }
    }

    public void movedToApprovalStage(ApprovalRound approvalRound) {
        ApplicationForm applicationForm = approvalRound.getApplication();
        Supervisor primarySupervisor = approvalRound.getPrimarySupervisor();
        createApplicationFormUserRole(approvalRound.getApplication(), primarySupervisor.getUser(), Authority.SUPERVISOR, true);
        for (RegisteredUser approver : applicationForm.getProgram().getApprovers()) {
            createApplicationFormUserRole(applicationForm, approver, Authority.APPROVER, true);
        }
    }

    public void referencePosted(Referee referee) {
        createApplicationFormUserRole(referee.getApplication(), referee.getUser(), Authority.REFEREE, false);
    }

    public void reviewPosted(Reviewer reviewer) {
        createApplicationFormUserRole(reviewer.getReviewRound().getApplication(), reviewer.getUser(), Authority.REVIEWER, false);
    }

    public void interviewParticipantResponded(InterviewParticipant participant) {
        Interview interview = participant.getInterview();
        ApplicationForm application = interview.getApplication();
        boolean isApplicant = participant.getUser().getId() == application.getApplicant().getId();
        Authority authority = isApplicant ? Authority.INTERVIEWPARTICIPANTAPPLICANT : Authority.INTERVIEWPARTICIPANTINTERVIEWER;
        createApplicationFormUserRole(application, participant.getUser(), authority, false);
    }

    public void interviewConfirmed(Interview interview) {
        ApplicationForm application = interview.getApplication();
        for (InterviewParticipant participant : interview.getParticipants()) {
            boolean isApplicant = participant.getUser().getId() == application.getApplicant().getId();
            Authority authority = isApplicant ? Authority.INTERVIEWPARTICIPANTAPPLICANT : Authority.INTERVIEWPARTICIPANTINTERVIEWER;
            createApplicationFormUserRole(application, participant.getUser(), authority, false);
        }
        for (Interviewer interviewer : interview.getInterviewers()) {
            createApplicationFormUserRole(application, interviewer.getUser(), Authority.INTERVIEWER, true);
        }
    }

    public void interviewFeedbackPosted(Interviewer interviewer) {
        createApplicationFormUserRole(interviewer.getInterview().getApplication(), interviewer.getUser(), Authority.INTERVIEWER, false);
    }

    private ApplicationFormUserRole createApplicationFormUserRole(ApplicationForm applicationForm, RegisteredUser user, Authority authority,
            boolean isCurrentRole) {
        ApplicationFormUserRole applicationFormUserRole = new ApplicationFormUserRole();
        applicationFormUserRole.setId(new ApplicationFormUserRoleId(applicationForm, user, roleDAO.getRoleByAuthority(authority)));
        applicationFormUserRole.setCurrentRole(isCurrentRole);
        applicationFormUserRoleDAO.save(applicationFormUserRole);
        return applicationFormUserRole;
    }

    public void processingDelegated(ApplicationForm applicationForm) {
        // TODO Auto-generated method stub
    }

}
