package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormUserRoleDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.AdmitterComment;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormActionRequired;
import com.zuehlke.pgadmissions.domain.ApplicationFormUserRole;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.InterviewParticipant;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.StateChangeComment;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.Authority;

@Service
@Transactional
public class ApplicationFormUserRoleService {

    private final ApplicationFormUserRoleDAO applicationFormUserRoleDAO;

    private final RoleDAO roleDAO;

    private final UserDAO userDAO;
    
    protected final ApplicationFormDAO applicationFormDAO;
    
    private final Map<ApplicationFormStatus, String> initiateStageMap = Maps.newHashMap();

    public ApplicationFormUserRoleService() {
        this(null, null, null, null);
    }

    @Autowired
    public ApplicationFormUserRoleService(ApplicationFormUserRoleDAO applicationFormUserRoleDAO, 
    		RoleDAO roleDAO, UserDAO userDAO, ApplicationFormDAO applicationFormDAO) {
        this.applicationFormUserRoleDAO = applicationFormUserRoleDAO;
        this.roleDAO = roleDAO;
        this.userDAO = userDAO;
        this.applicationFormDAO = applicationFormDAO;
        
        initiateStageMap.put(ApplicationFormStatus.REVIEW, "ASSIGN_REVIEWERS");
        initiateStageMap.put(ApplicationFormStatus.INTERVIEW, "ASSIGN_INTERVIEWERS");
        initiateStageMap.put(ApplicationFormStatus.APPROVAL, "ASSIGN_SUPERVISORS");
        initiateStageMap.put(ApplicationFormStatus.REJECTED, "CONFIRM_REJECTION");
        
    }

    public void applicationSubmitted(ApplicationForm applicationForm) {
        createApplicationFormUserRole(applicationForm, applicationForm.getApplicant(), Authority.APPLICANT, true);

        assignToAdministrators(applicationForm, "COMPLETE_VALIDATION_STAGE", applicationForm.getDueDate(), true);

        for (RegisteredUser approver : applicationForm.getProgram().getApprovers()) {
            createApplicationFormUserRole(applicationForm, approver, Authority.APPROVER, false);
        }
    }

    public void validationStageCompleted(ApplicationForm application) {
        deassignFromAdministrators(application);
			
        for (Referee referee : application.getReferees()) {
            createApplicationFormUserRole(application, referee.getUser(), Authority.REFEREE, false, 
            		new ApplicationFormActionRequired("PROVIDE_REFERENCE", new Date(), false, true));
        }

        boolean anyUnsure = application.getValidationComment().isAtLeastOneAnswerUnsure();

        for (RegisteredUser admitter : userDAO.getAdmitters()) {
            if (anyUnsure) {
                createApplicationFormUserRole(application, admitter, Authority.ADMITTER, false, 
                		new ApplicationFormActionRequired("CONFIRM_ELIGIBILITY", new Date(), false, true));
            } else {
                createApplicationFormUserRole(application, admitter, Authority.ADMITTER, false);
            }
        }
    }

    public void stateChanged(StateChangeComment stateChangeComment) {
        ApplicationForm application = stateChangeComment.getApplication();
        deassignFromAdministrators(application);

        ApplicationFormStatus nextStatus = stateChangeComment.getNextStatus();
        if (initiateStageMap.containsKey(nextStatus)) {
        	assignToAdministrators(application, initiateStageMap.get(nextStatus), new Date(), false);
        }
        
        List<RegisteredUser> approvers = application.getProgram().getApprovers();

        if (nextStatus == ApplicationFormStatus.APPROVED) {
            for (RegisteredUser approver : approvers) {
                createApplicationFormUserRole(application, approver, Authority.APPROVER, false, 
                		new ApplicationFormActionRequired("COMPLETE_OFFER_RECOMMENDATION", new Date(), false, true));
            }

            for (RegisteredUser superAdministrator : userDAO.getSuperadministrators()) {
                createApplicationFormUserRole(application, superAdministrator, Authority.SUPERADMINISTRATOR, false, 
                		new ApplicationFormActionRequired("COMPLETE_OFFER_RECOMMENDATION", new Date(), false, true));
            }
        }

        if (application.getStatus() == ApplicationFormStatus.APPROVAL && nextStatus != null) {
            for (RegisteredUser approver : approvers) {
                createApplicationFormUserRole(application, approver, Authority.APPROVER, false, 
                		new ApplicationFormActionRequired(initiateStageMap.get(nextStatus), new Date(), false, true));
            }
        }
    }

    public void movedToReviewStage(ReviewRound reviewRound) {
        ApplicationForm application = reviewRound.getApplication();
        deassignFromAdministrators(application);
        assignToAdministrators(application, "COMPLETE_REVIEW_STAGE", application.getDueDate(), true);

        for (Reviewer reviewer : reviewRound.getReviewers()) {
            createApplicationFormUserRole(reviewRound.getApplication(), reviewer.getUser(), Authority.REVIEWER, true,
            		new ApplicationFormActionRequired("PROVIDE_REVIEW", new Date(), false, true));
        }
    }

    public void movedToInterviewStage(Interview interview) {
        ApplicationForm application = interview.getApplication();
        deassignFromAdministrators(application);

        if (interview.isScheduling()) {
            assignToAdministrators(application, "CONFIRM_INTERVIEW_ARRANGEMENTS", application.getDueDate(), true);

            for (InterviewParticipant participant : interview.getParticipants()) {
                boolean isApplicant = participant.getUser().getId().equals(application.getApplicant().getId());
                Authority authority = isApplicant ? Authority.APPLICANT : Authority.INTERVIEWER;
                createApplicationFormUserRole(application, participant.getUser(), authority, false, 
                		new ApplicationFormActionRequired("PROVIDE_INTERVIEW_AVAILABILITY", new Date(), false, true));
            }
        } else {
            for (Interviewer interviewer : interview.getInterviewers()) {
            	boolean raisesUrgentFlag = interview.getInterviewDueDate().before(new Date());
            	
                createApplicationFormUserRole(application, interviewer.getUser(), Authority.INTERVIEWER, false, new ApplicationFormActionRequired(
                        "PROVIDE_INTERVIEW_FEEDBACK", interview.getInterviewDueDate(), false, raisesUrgentFlag));
            }
        }
        assignToAdministrators(application, "COMPLETE_INTERVIEW_STAGE", application.getDueDate(), true);
    }

    public void movedToApprovalStage(ApprovalRound approvalRound) {
        ApplicationForm applicationForm = approvalRound.getApplication();
        deassignFromAdministrators(applicationForm);

        Supervisor primarySupervisor = approvalRound.getPrimarySupervisor();
        createApplicationFormUserRole(approvalRound.getApplication(), primarySupervisor.getUser(), Authority.SUPERVISOR, false,
                new ApplicationFormActionRequired("CONFIRM_PRIMARY_SUPERVISION", new Date(), false, true));

        assignToAdministrators(applicationForm, "COMPLETE_APPROVAL_STAGE", applicationForm.getDueDate(), true);

        List<RegisteredUser> approvers = applicationForm.getProgram().getApprovers();
        for (RegisteredUser approver : approvers) {
            createApplicationFormUserRole(applicationForm, approver, Authority.APPROVER, false, 
            		new ApplicationFormActionRequired("COMPLETE_APPROVAL_STAGE", applicationForm.getDueDate(), true, false));
        }
    }
    
    public void admitterCommentPosted(AdmitterComment comment) {
        ApplicationForm application = comment.getApplication();
        List<ApplicationFormUserRole> roles = applicationFormUserRoleDAO.findByApplicationFormAndAuthorities(application, Authority.ADMITTER);
        for(ApplicationFormUserRole role : roles) {
            role.getActions().clear();
            role.setRaisesUrgentFlag(false);
        }
    }

    public void referencePosted(Referee referee) {
        ApplicationForm application = referee.getApplication();
        ApplicationFormUserRole role = applicationFormUserRoleDAO.findByApplicationFormAndUserAndAuthority(application, referee.getUser(), Authority.REFEREE);
        role.getActions().clear();
        role.setRaisesUrgentFlag(false);
    }

    public void reviewPosted(Reviewer reviewer) {
        ReviewRound reviewRound = reviewer.getReviewRound();
        ApplicationForm application = reviewRound.getApplication();
        ReviewComment review = reviewer.getReview();

        ApplicationFormUserRole role = applicationFormUserRoleDAO.findByApplicationFormAndUserAndAuthority(application, reviewer.getUser(), Authority.REVIEWER);
        role.setInterestedInApplicant(review.getWillingToInterview() || review.getWillingToWorkWithApplicant());
        role.getActions().clear();
        role.setRaisesUrgentFlag(false);

        if (reviewRound.hasAllReviewersResponded()) {
            resetActionDeadline(application, new Date());
        }
    }

    public void interviewParticipantResponded(InterviewParticipant participant) {
        Interview interview = participant.getInterview();
        ApplicationForm application = interview.getApplication();
        RegisteredUser user = participant.getUser();
        boolean isApplicant = user.getId() == application.getApplicant().getId();
        ApplicationFormUserRole role;
        if (isApplicant) {
            role = applicationFormUserRoleDAO.findByApplicationFormAndUserAndAuthority(application, user, Authority.APPLICANT);
        } else {
            role = applicationFormUserRoleDAO.findByApplicationFormAndUserAndAuthority(application, user, Authority.INTERVIEWER);
        }
        role.getActions().clear();
        role.setRaisesUrgentFlag(false);

        if (interview.hasAllParticipantsProvidedAvailability()) {
            resetActionDeadline(application, new Date());
        }
    }

    public void interviewConfirmed(Interview interview) {
        ApplicationForm application = interview.getApplication();
        
        deassignFromAdministrators(application);
        
        for (InterviewParticipant participant : interview.getParticipants()) {
            RegisteredUser user = participant.getUser();
            boolean isApplicant = user.getId() == application.getApplicant().getId();

            ApplicationFormUserRole role;
            if (isApplicant) {
                role = applicationFormUserRoleDAO.findByApplicationFormAndUserAndAuthority(application, user, Authority.APPLICANT);
            } else {
                role = applicationFormUserRoleDAO.findByApplicationFormAndUserAndAuthority(application, user, Authority.INTERVIEWER);
            }
            role.getActions().clear();
            if (!isApplicant) {
                role.getActions().add(new ApplicationFormActionRequired("PROVIDE_INTERVIEW_FEEDBACK", new Date(), false, true));
            }
            role.setRaisesUrgentFlag(!isApplicant);
        }
        assignToAdministrators(application, "COMPLETE_INTERVIEW_STAGE", application.getDueDate(), true);
    }

    public void interviewFeedbackPosted(Interviewer interviewer) {
        Interview interview = interviewer.getInterview();
        ApplicationForm application = interview.getApplication();
        InterviewComment interviewComment = interviewer.getInterviewComment();

        ApplicationFormUserRole role = applicationFormUserRoleDAO.findByApplicationFormAndUserAndAuthority(application, interviewer.getUser(),
                Authority.INTERVIEWER);
        role.setInterestedInApplicant(interviewComment.getWillingToSupervise());
        role.getActions().clear();
        role.setRaisesUrgentFlag(false);

        if (interview.hasAllInterviewersProvidedFeedback()) {
            resetActionDeadline(application, new Date());
        }
    }

    public void processingDelegated(ApplicationForm applicationForm) {
        // TODO Auto-generated method stub
    }

    public void supervisionConfirmed(Supervisor supervisor) {
        ApprovalRound approval = supervisor.getApprovalRound();
        ApplicationForm application = approval.getApplication();

        ApplicationFormUserRole role = applicationFormUserRoleDAO.findByApplicationFormAndUserAndAuthority(application, supervisor.getUser(),
                Authority.SUPERVISOR);
        role.setInterestedInApplicant(supervisor.getConfirmedSupervision());
        role.getActions().clear();
        role.setRaisesUrgentFlag(false);

        resetActionDeadline(application, new Date());
    }

    public void moveToApprovedOrRejectedOrWithdrawn(ApplicationForm application) {
        List<ApplicationFormUserRole> roles = applicationFormUserRoleDAO.findByApplicationForm(application);

        for (ApplicationFormUserRole role : roles) {
            role.getActions().clear();
            role.setRaisesUrgentFlag(false);
        }

    }
    
    public void createUserInRole(RegisteredUser registeredUser, Authority authority) {
    	applicationFormUserRoleDAO.insertUserinRole(registeredUser, authority);
    }
    
    public void createUserInProgramRole (RegisteredUser registeredUser, Program program, Authority authority) {
    	applicationFormUserRoleDAO.insertUserInProgramRole(registeredUser, program, authority);
    }
    
    public void revokeUserFromRole (RegisteredUser registeredUser, Authority authority) {
    	applicationFormUserRoleDAO.deleteUserFromRole(registeredUser, authority);
    }
    
    public void revokeUserFromProgramRole (RegisteredUser registeredUser, Program program, Authority authority) {
    	applicationFormUserRoleDAO.deleteUserFromProgramRole(registeredUser, program, authority);
    }
    
    public void registerApplicationUpdate (ApplicationForm applicationForm, ApplicationUpdateScope updateVisibility) {
        Date updateTimestamp = new Date();
        applicationForm.setLastUpdated(updateTimestamp);
    	for (ApplicationFormUserRole applicationFormUserRole : applicationFormUserRoleDAO.findByApplicationFormAndAuthorityUpdateVisility(applicationForm, updateVisibility)) {
    		applicationFormUserRole.setUpdateTimestamp(updateTimestamp);
    		applicationFormUserRole.setRaisesUpdateFlag(true);
    	}
    }
    
    public List<RegisteredUser> getUsersInterestedInApplication(ApplicationForm applicationForm) {
    	return applicationFormUserRoleDAO.findUsersInterestedInApplication(applicationForm);
    }
    
    public List<RegisteredUser> getOtherUsersPotentiallyInterestedInApplication(Program program) {
    	return applicationFormUserRoleDAO.findProgramUsers(program);
    }
    
    public void deregisterApplicationUpdate (ApplicationForm applicationForm, RegisteredUser registeredUser) {
    	for (ApplicationFormUserRole applicationFormUserRole : applicationFormUserRoleDAO.findByApplicationFormAndUser(applicationForm, registeredUser)) {
    		applicationFormUserRole.setRaisesUpdateFlag(false);
    	}
    }
    
    private ApplicationFormUserRole createApplicationFormUserRole(ApplicationForm applicationForm, RegisteredUser user, Authority authority,
            boolean interestedInApplicant, ApplicationFormActionRequired... actions) {

        ApplicationFormUserRole applicationFormUserRole = applicationFormUserRoleDAO.findByApplicationFormAndUserAndAuthority(applicationForm, user, authority);
        Role role = roleDAO.getRoleByAuthority(authority);
        
        if (applicationFormUserRole == null) {
            applicationFormUserRole = new ApplicationFormUserRole();
            applicationFormUserRole.setApplicationForm(applicationForm);
            applicationFormUserRole.setRole(role);
            applicationFormUserRole.setUser(user);
            
            Date updateTimestamp = applicationFormUserRoleDAO.findUpdateTimestampByApplicationFormAndAuthorityUpdateVisility(applicationForm, role.getUpdateVisibility());
            if (updateTimestamp != null) {
            	applicationFormUserRole.setUpdateTimestamp(updateTimestamp);
            	applicationFormUserRole.setRaisesUpdateFlag(true);
            }
        }

        applicationFormUserRole.setInterestedInApplicant(interestedInApplicant);
        
        boolean raisesUrgentFlag = false;
        for (ApplicationFormActionRequired action : actions) {
            applicationFormUserRole.getActions().add(action);
            if (!raisesUrgentFlag && BooleanUtils.isTrue(action.getRaisesUrgentFlag())) {
            	raisesUrgentFlag = true;
            }
        }
        
        applicationFormUserRole.setRaisesUrgentFlag(raisesUrgentFlag);
        applicationFormUserRoleDAO.save(applicationFormUserRole);
        return applicationFormUserRole;
    }
    
    private void assignToAdministrators(ApplicationForm applicationForm, String action, Date dueDate, Boolean bindDeadlineToDueDate) {
        Map<RegisteredUser, Authority> administrators = Maps.newHashMap();

        for (RegisteredUser superAdministrator : userDAO.getSuperadministrators()) {
            administrators.put(superAdministrator, Authority.SUPERADMINISTRATOR);
        }

        for (RegisteredUser administrator : applicationForm.getProgram().getAdministrators()) {
            administrators.put(administrator, Authority.ADMINISTRATOR);
        }

        if (applicationForm.getProject() != null) {
            RegisteredUser projectAdministrator = applicationForm.getProject().getAdministrator();
            if (projectAdministrator != null) {
                administrators.put(projectAdministrator, Authority.PROJECTADMINISTRATOR);
            }
        }
        for (Entry<RegisteredUser, Authority> administrator : administrators.entrySet()) {
        	boolean raisesUrgentFlag = dueDate.before(new Date());
        	
        	List<ApplicationFormActionRequired> requiredActions = new ArrayList<ApplicationFormActionRequired>();
        	requiredActions.add(new ApplicationFormActionRequired(action, dueDate, bindDeadlineToDueDate, raisesUrgentFlag));
        	
        	if (initiateStageMap.containsKey(action)) {
        		requiredActions.add(new ApplicationFormActionRequired("MOVE_TO_DIFFERENT_STAGE", dueDate, bindDeadlineToDueDate, raisesUrgentFlag));
        	}
        	
            createApplicationFormUserRole(applicationForm, administrator.getKey(), administrator.getValue(), false, 
            		requiredActions.toArray(new ApplicationFormActionRequired[0]));
        }
    }

    private void deassignFromAdministrators(ApplicationForm applicationForm) {
        List<ApplicationFormUserRole> roles = applicationFormUserRoleDAO.findByApplicationFormAndAuthorities(applicationForm, Authority.REVIEWER,
                Authority.INTERVIEWER, Authority.SUPERVISOR, Authority.APPLICANT, Authority.ADMINISTRATOR, Authority.SUPERADMINISTRATOR,
                Authority.APPROVALADMINISTRATOR, Authority.INTERVIEWADMINISTRATOR, Authority.PROJECTADMINISTRATOR, Authority.REVIEWADMINISTRATOR,
                Authority.APPROVER);
        for (ApplicationFormUserRole role : roles) {
            applicationFormUserRoleDAO.clearActions(role);
            role.setRaisesUrgentFlag(false);
        }
    }

    private void resetActionDeadline(ApplicationForm applicationForm, Date newDueDate) {
        applicationForm.setDueDate(newDueDate);
        List<ApplicationFormUserRole> applicationFormUserRoles = applicationFormUserRoleDAO.findByApplicationForm(applicationForm);
        boolean raisesUrgentFlag = newDueDate.before(new Date());
        for (ApplicationFormUserRole applicationFormUserRole : applicationFormUserRoles) {
            List<ApplicationFormActionRequired> actions = applicationFormUserRole.getActions();
            for (ApplicationFormActionRequired action : actions) {
                if (action.getBindDeadlineToDueDate()) {
                    action.setDeadlineTimestamp(newDueDate);
                    action.setRaisesUrgentFlag(raisesUrgentFlag);
                }
            }
        	applicationFormUserRole.setRaisesUrgentFlag(raisesUrgentFlag);
        }
    }
    
}