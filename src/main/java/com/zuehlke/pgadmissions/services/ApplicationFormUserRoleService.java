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
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.StateChangeComment;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.Authority;

@Service
@Transactional
public class ApplicationFormUserRoleService extends UserService {
	
	private final ApplicationFormDAO applicationFormDAO;

    private final ApplicationFormUserRoleDAO applicationFormUserRoleDAO;

    private final RoleDAO roleDAO;

    private final Map<ApplicationFormStatus, ApplicationFormAction> initiateStageMap = Maps.newHashMap();

    public ApplicationFormUserRoleService() {
        this(null, null, null);
    }

    @Autowired
    public ApplicationFormUserRoleService(ApplicationFormDAO applicationFormDAO, ApplicationFormUserRoleDAO applicationFormUserRoleDAO, RoleDAO roleDAO) {
        this.applicationFormUserRoleDAO = applicationFormUserRoleDAO;
        this.roleDAO = roleDAO;
        this.applicationFormDAO = applicationFormDAO;

        initiateStageMap.put(ApplicationFormStatus.REVIEW, ApplicationFormAction.ASSIGN_REVIEWERS);
        initiateStageMap.put(ApplicationFormStatus.INTERVIEW, ApplicationFormAction.ASSIGN_INTERVIEWERS);
        initiateStageMap.put(ApplicationFormStatus.APPROVAL, ApplicationFormAction.ASSIGN_SUPERVISORS);
        initiateStageMap.put(ApplicationFormStatus.REJECTED, ApplicationFormAction.CONFIRM_REJECTION);
    }
    
    public void applicationCreated(ApplicationForm applicationForm) {
    	grantUserApplicationRole(applicationForm, applicationForm.getApplicant(), Authority.APPLICANT, false);
    }

    public void applicationSubmitted(ApplicationForm applicationForm) {
        assignToAdministrators(applicationForm, ApplicationFormAction.COMPLETE_VALIDATION_STAGE, applicationForm.getDueDate(), true);

        for (RegisteredUser approver : applicationForm.getProgram().getApprovers()) {
            grantUserApplicationRole(applicationForm, approver, Authority.APPROVER, false);
        }

        for (SuggestedSupervisor suggestedSupervisor : applicationForm.getProgrammeDetails().getSuggestedSupervisors()) {
        	RegisteredUser userToSaveAsSuggestedSupervisor = super.createRegisteredUser(suggestedSupervisor.getFirstname(), suggestedSupervisor.getLastname(), suggestedSupervisor.getEmail());
            grantUserApplicationRole(applicationForm, userToSaveAsSuggestedSupervisor, Authority.SUGGESTEDSUPERVISOR, true);
        }
    }
    
    public void applicationEdited(ApplicationForm applicationForm, RegisteredUser user) {
    	registerApplicationUpdate(applicationForm, user, ApplicationUpdateScope.ALL_USERS);
    }
    
    public void applicationViewed(ApplicationForm applicationForm, RegisteredUser user) {
        for (ApplicationFormUserRole applicationFormUserRole : applicationFormUserRoleDAO.findByApplicationFormAndUser(applicationForm, user)) {
            applicationFormUserRole.setRaisesUpdateFlag(false);
        }
    }
    
    public void commentPosted(ApplicationForm applicationForm, RegisteredUser user) {
    	registerApplicationUpdate(applicationForm, user, ApplicationUpdateScope.INTERNAL);
    }

    public void validationStageCompleted(ApplicationForm application) {
        applicationFormUserRoleDAO.deleteAllStateRoles(application);

        for (Referee referee : application.getReferees()) {
        	RegisteredUser refereeUser = super.createRegisteredUser(referee.getFirstname(), referee.getLastname(), referee.getEmail());
            grantUserApplicationRole(application, refereeUser, Authority.REFEREE, false, 
            		new ApplicationFormActionRequired(ApplicationFormAction.PROVIDE_REFERENCE, new Date(), false, true));
        }

        Boolean anyUnsure = application.getValidationComment().isAtLeastOneAnswerUnsure();
        List<RegisteredUser> admitters = super.getUsersInRole(Authority.ADMITTER);
        List<ApplicationFormUserRole> superadministratorRoles = applicationFormUserRoleDAO.findByApplicationFormAndAuthorities(application, Authority.SUPERADMINISTRATOR);
        
        if (BooleanUtils.isTrue(anyUnsure)) {
        	for (RegisteredUser admitter : admitters) {
                grantUserApplicationRole(application, admitter, Authority.ADMITTER, false, 
                		new ApplicationFormActionRequired(ApplicationFormAction.CONFIRM_ELIGIBILITY, new Date(), false, true));        		
        	}
        	for (ApplicationFormUserRole superadministratorRole : superadministratorRoles) {
        		superadministratorRole.getActions().add(new ApplicationFormActionRequired(ApplicationFormAction.CONFIRM_ELIGIBILITY, new Date(), false, true));
        		superadministratorRole.setRaisesUrgentFlag(true);
        	}
        } else {
        	for (RegisteredUser admitter : admitters) {
                grantUserApplicationRole(application, admitter, Authority.ADMITTER, false);        		
        	}
        }
        
    }

    public void stateChanged(StateChangeComment stateChangeComment) {
        ApplicationForm application = stateChangeComment.getApplication();
        applicationFormUserRoleDAO.deleteAllStateRoles(application);

        ApplicationFormStatus nextStatus = stateChangeComment.getNextStatus();
        if (initiateStageMap.containsKey(nextStatus)) {
            assignToAdministrators(application, initiateStageMap.get(nextStatus), new Date(), false);
        }

        List<RegisteredUser> approvers = application.getProgram().getApprovers();

        if (nextStatus == ApplicationFormStatus.APPROVED) {
            for (RegisteredUser approver : approvers) {
                grantUserApplicationRole(application, approver, Authority.APPROVER, false, 
                		new ApplicationFormActionRequired(ApplicationFormAction.CONFIRM_OFFER_RECOMMENDATION, new Date(), false, true), 
                		new ApplicationFormActionRequired(ApplicationFormAction.MOVE_TO_DIFFERENT_STAGE, new Date(),
                        false, true));
            }

            for (RegisteredUser superAdministrator : super.getUsersInRole(Authority.SUPERADMINISTRATOR)) {
                grantUserApplicationRole(application, superAdministrator, Authority.SUPERADMINISTRATOR, false, 
                		new ApplicationFormActionRequired(ApplicationFormAction.CONFIRM_OFFER_RECOMMENDATION, new Date(), false, true), 
                		new ApplicationFormActionRequired(ApplicationFormAction.MOVE_TO_DIFFERENT_STAGE, new Date(), false, true));
            }
        }

        else if (application.getStatus() == ApplicationFormStatus.APPROVAL && nextStatus != null) {
            for (RegisteredUser approver : approvers) {
                grantUserApplicationRole(application, approver, Authority.APPROVER, false,
                        new ApplicationFormActionRequired(initiateStageMap.get(nextStatus), new Date(), false, true), 
                        new ApplicationFormActionRequired(ApplicationFormAction.MOVE_TO_DIFFERENT_STAGE, new Date(), false, true));
            }
        }
        
        registerApplicationUpdate(application, stateChangeComment.getUser(), ApplicationUpdateScope.INTERNAL);
    }

    public void movedToReviewStage(ReviewRound reviewRound, RegisteredUser mover) {
        ApplicationForm application = reviewRound.getApplication();
        applicationFormUserRoleDAO.deleteAllStateRoles(application);

        for (Reviewer reviewer : reviewRound.getReviewers()) {
            grantUserApplicationRole(reviewRound.getApplication(), reviewer.getUser(), Authority.REVIEWER, false, 
            		new ApplicationFormActionRequired(ApplicationFormAction.PROVIDE_REVIEW, new Date(), false, true));
        }

        assignToAdministrators(application, ApplicationFormAction.COMPLETE_REVIEW_STAGE, application.getDueDate(), true);
        registerApplicationUpdate(application, mover, ApplicationUpdateScope.ALL_USERS);
    }

    public void movedToInterviewStage(Interview interview, RegisteredUser mover) {
        ApplicationForm application = interview.getApplication();
        applicationFormUserRoleDAO.deleteAllStateRoles(application);

        if (interview.isScheduling()) {
            assignToAdministrators(application, ApplicationFormAction.CONFIRM_INTERVIEW_ARRANGEMENTS, application.getDueDate(), true);

            for (InterviewParticipant participant : interview.getParticipants()) {
                Boolean isApplicant = participant.getUser().getId().equals(application.getApplicant().getId());
                Authority authority = isApplicant ? Authority.APPLICANT : Authority.INTERVIEWER;
                grantUserApplicationRole(application, participant.getUser(), authority, false, 
                		new ApplicationFormActionRequired(ApplicationFormAction.PROVIDE_INTERVIEW_AVAILABILITY, new Date(), false, true));
            }
        } else {
            for (Interviewer interviewer : interview.getInterviewers()) {
                Boolean raisesUrgentFlag = interview.getInterviewDueDate().before(new Date());

                grantUserApplicationRole(application, interviewer.getUser(), Authority.INTERVIEWER, false, 
                		new ApplicationFormActionRequired(ApplicationFormAction.PROVIDE_INTERVIEW_FEEDBACK, interview.getInterviewDueDate(), false, raisesUrgentFlag));
            }
        }
        assignToAdministrators(application, ApplicationFormAction.COMPLETE_INTERVIEW_STAGE, application.getDueDate(), true);
        registerApplicationUpdate(application, mover, ApplicationUpdateScope.ALL_USERS);
    }

    public void movedToApprovalStage(ApprovalRound approvalRound, RegisteredUser mover) {
        ApplicationForm application = approvalRound.getApplication();
        applicationFormUserRoleDAO.deleteAllStateRoles(application);

        Supervisor primarySupervisor = approvalRound.getPrimarySupervisor();
        grantUserApplicationRole(approvalRound.getApplication(), primarySupervisor.getUser(), Authority.SUPERVISOR, false,
                new ApplicationFormActionRequired(ApplicationFormAction.CONFIRM_PRIMARY_SUPERVISION, new Date(), false, true));

        assignToAdministrators(application, ApplicationFormAction.COMPLETE_APPROVAL_STAGE, application.getDueDate(), true);

        List<RegisteredUser> approvers = application.getProgram().getApprovers();
        for (RegisteredUser approver : approvers) {
            grantUserApplicationRole(application, approver, Authority.APPROVER, false, 
            		new ApplicationFormActionRequired(ApplicationFormAction.COMPLETE_APPROVAL_STAGE, application.getDueDate(), true, false));
        }
        registerApplicationUpdate(application, mover, ApplicationUpdateScope.ALL_USERS);
    }

    public void admitterCommentPosted(AdmitterComment comment) {
        ApplicationForm application = comment.getApplication();
        List<ApplicationFormUserRole> roles = applicationFormUserRoleDAO.findByApplicationFormAndAuthorities(application, Authority.ADMITTER);
        for (ApplicationFormUserRole role : roles) {
            deleteActionsAndFlushToDB(role);
        }
    }

    public void referencePosted(Referee referee) {
        ApplicationForm application = referee.getApplication();
        RegisteredUser user = referee.getUser();
        ApplicationFormUserRole role = applicationFormUserRoleDAO.findByApplicationFormAndUserAndAuthority(application, user, Authority.REFEREE);     
        
        if (role != null) {
        	deleteActionsAndFlushToDB(role);
        }
        
        registerApplicationUpdate(application, user, ApplicationUpdateScope.ALL_USERS);
    }

    public void reviewPosted(Reviewer reviewer) {
        ReviewRound reviewRound = reviewer.getReviewRound();
        ApplicationForm application = reviewRound.getApplication();
        ReviewComment review = reviewer.getReview();

        ApplicationFormUserRole role = applicationFormUserRoleDAO.findByApplicationFormAndUserAndAuthority(application, reviewer.getUser(), Authority.REVIEWER);
        setInterestedInApplication(application, reviewer.getUser(), review.getWillingToInterview() || review.getWillingToWorkWithApplicant());

        deleteActionsAndFlushToDB(role);

        if (reviewRound.hasAllReviewersResponded()) {
            resetActionDeadline(application, new Date());
        }

    }

    public void interviewParticipantResponded(InterviewParticipant participant) {
        Interview interview = participant.getInterview();
        ApplicationForm application = interview.getApplication();
        RegisteredUser user = participant.getUser();
        Boolean isApplicant = user.getId() == application.getApplicant().getId();
        ApplicationFormUserRole role;
        if (BooleanUtils.isTrue(isApplicant)) {
            role = applicationFormUserRoleDAO.findByApplicationFormAndUserAndAuthority(application, user, Authority.APPLICANT);
        } else {
            role = applicationFormUserRoleDAO.findByApplicationFormAndUserAndAuthority(application, user, Authority.INTERVIEWER);
        }

        deleteActionsAndFlushToDB(role);

        if (interview.hasAllParticipantsProvidedAvailability()) {
            resetActionDeadline(application, new Date());
        }
        
        registerApplicationUpdate(application, user, ApplicationUpdateScope.INTERNAL);
    }

    public void interviewConfirmed(Interview interview, RegisteredUser confirmer) {
        ApplicationForm application = interview.getApplication();
        applicationFormUserRoleDAO.deleteAllStateRoles(application);

        for (InterviewParticipant participant : interview.getParticipants()) {
            RegisteredUser user = participant.getUser();
            Boolean isApplicant = user.getId() == application.getApplicant().getId();

            ApplicationFormUserRole role;
            if (BooleanUtils.isTrue(isApplicant)) {
                role = applicationFormUserRoleDAO.findByApplicationFormAndUserAndAuthority(application, user, Authority.APPLICANT);
            } else {
                role = applicationFormUserRoleDAO.findByApplicationFormAndUserAndAuthority(application, user, Authority.INTERVIEWER);
            }

            deleteActionsAndFlushToDB(role);

            if (!isApplicant) {
                Date dateNow = new Date();
                role.getActions().add(new ApplicationFormActionRequired(ApplicationFormAction.PROVIDE_INTERVIEW_FEEDBACK, dateNow, false, interview.getInterviewDueDate().before(dateNow)));
            }
        }
        
        assignToAdministrators(application, ApplicationFormAction.COMPLETE_INTERVIEW_STAGE, application.getDueDate(), true);
        registerApplicationUpdate(application, confirmer, ApplicationUpdateScope.ALL_USERS);
    }

    public void interviewFeedbackPosted(Interviewer interviewer) {
        Interview interview = interviewer.getInterview();
        RegisteredUser user = interviewer.getUser();
        ApplicationForm application = interview.getApplication();
        InterviewComment interviewComment = interviewer.getInterviewComment();

        ApplicationFormUserRole role = applicationFormUserRoleDAO.findByApplicationFormAndUserAndAuthority(application, interviewer.getUser(),
                Authority.INTERVIEWER);
        setInterestedInApplication(application, user, interviewComment.getWillingToSupervise());

        deleteActionsAndFlushToDB(role);

        if (interview.hasAllInterviewersProvidedFeedback()) {
            resetActionDeadline(application, new Date());
        }
        
        registerApplicationUpdate(application, user, ApplicationUpdateScope.INTERNAL);
    }

    public void supervisionConfirmed(Supervisor supervisor) {
        ApprovalRound approval = supervisor.getApprovalRound();
        ApplicationForm application = approval.getApplication();

        ApplicationFormUserRole role = applicationFormUserRoleDAO.findByApplicationFormAndUserAndAuthority(application, supervisor.getUser(),
                Authority.SUPERVISOR);
        setInterestedInApplication(application, supervisor.getUser(), supervisor.getConfirmedSupervision());

        deleteActionsAndFlushToDB(role);

        resetActionDeadline(application, new Date());

    }

    public void moveToApprovedOrRejectedOrWithdrawn(ApplicationForm application, RegisteredUser mover) {
        applicationFormUserRoleDAO.deleteAllApplicationRoles(application); 
        Program program = application.getProgram();
        Project project = application.getProject();
        
        if (!program.isEnabled() && applicationFormDAO.getActiveApplicationsByProgram(program).isEmpty()) {
        	applicationFormUserRoleDAO.deleteAllProgramRoles(program);
        } else if (project != null) {
        	if (project.isDisabled() && applicationFormDAO.getActiveApplicationsByProject(project).isEmpty()) {
        		applicationFormUserRoleDAO.deleteAllProjectRoles(project);
        	}
        }
        
        registerApplicationUpdate(application, mover, ApplicationUpdateScope.ALL_USERS);
    }

    public void grantUserSystemRoles(RegisteredUser registeredUser, Authority... authorities) {
    	for (Authority authority : authorities) {
            applicationFormUserRoleDAO.insertUserinRole(registeredUser, authority);
    	}
        super.grantRolesToUser(registeredUser, authorities);
    }

    public void revokeUserFromSystemRoles(RegisteredUser registeredUser, Authority... authorities) {
    	for (Authority authority : authorities) {
            applicationFormUserRoleDAO.deleteUserFromRole(registeredUser, authority);
    	}
    	super.revokeRolesFromUser(registeredUser, authorities);
    }
    
    public void grantUserProgramRoles(RegisteredUser registeredUser, Program program, Authority... authorities) {
    	for (Authority authority : authorities) {
    		applicationFormUserRoleDAO.insertUserInProgramRole(registeredUser, program, authority);
    	}
		super.updateUserProgramRoles(registeredUser, program, authorities);
    }

    public void revokeUserFromProgramRoles(RegisteredUser registeredUser, Program program, Authority... authorities) {
    	for (Authority authority : authorities) {
    		applicationFormUserRoleDAO.deleteUserFromProgramRole(registeredUser, program, authority);
    	}
		super.deleteUserFromProgramme(registeredUser, program);
    }
    
    public void grantUserProjectRoles(RegisteredUser oldUser, RegisteredUser newUser, final Project project, Authority... authorities) {
    	if (oldUser != newUser) {
    		for (Authority authority : authorities) {
    	    	if (oldUser != null) {
    	    		applicationFormUserRoleDAO.deleteUserFromProjectRole(oldUser, project, authority);
    	    		if (applicationFormUserRoleDAO.findByUserAndAuthority(oldUser, authority).isEmpty()) {
    	    			super.revokeRolesFromUser(oldUser, authority);
    	    		}
    	    	}
    	    	if (newUser != null) {
    	    		applicationFormUserRoleDAO.insertUserInProjectRole(newUser, project, authority);
    	    	}
        	}
    		if (newUser != null) {
    			super.grantRolesToUser(newUser, authorities);
    		}
    	}
    }

    public List<RegisteredUser> getUsersInterestedInApplication(ApplicationForm applicationForm) {
        return applicationFormUserRoleDAO.findUsersInterestedInApplication(applicationForm);
    }

    public List<RegisteredUser> getUsersPotentiallyInterestedInApplication(ApplicationForm applicationForm) {
        return applicationFormUserRoleDAO.findUsersPotentiallyInterestedInApplication(applicationForm);
    }
    
    private void registerApplicationUpdate(ApplicationForm applicationForm, RegisteredUser author, ApplicationUpdateScope updateVisibility) {
        Date updateTimestamp = new Date();
        applicationFormUserRoleDAO.updateApplicationFormUpdateTimestamp(applicationForm, author, updateTimestamp, updateVisibility);
    }

    private ApplicationFormUserRole grantUserApplicationRole(ApplicationForm applicationForm, RegisteredUser user, Authority authority,
            Boolean interestedInApplicant, ApplicationFormActionRequired... actions) {
        Role role = roleDAO.getRoleByAuthority(authority); 
        super.grantRolesToUser(user, authority);
        
        ApplicationFormUserRole applicationFormUserRole = applicationFormUserRoleDAO.findByApplicationFormAndUserAndAuthority(applicationForm, user, authority);

        if (applicationFormUserRole == null) {
            applicationFormUserRole = new ApplicationFormUserRole();
            applicationFormUserRole.setApplicationForm(applicationForm);
            applicationFormUserRole.setRole(role);
            applicationFormUserRole.setUser(user);

            Date updateTimestamp = applicationFormUserRoleDAO.findUpdateTimestampByApplicationFormAndAuthorityUpdateVisility(applicationForm,
                    role.getUpdateVisibility());
            if (updateTimestamp != null) {
                applicationFormUserRole.setUpdateTimestamp(updateTimestamp);
                applicationFormUserRole.setRaisesUpdateFlag(true);
            }
            
        }

        applicationFormUserRole.setInterestedInApplicant(interestedInApplicant);

        Boolean raisesUrgentFlag = false;
        for (ApplicationFormActionRequired action : actions) {
            applicationFormUserRole.getActions().add(action);
            if (BooleanUtils.isFalse(raisesUrgentFlag) && BooleanUtils.isTrue(action.getRaisesUrgentFlag())) {
                raisesUrgentFlag = true;
            }
        }

        applicationFormUserRole.setRaisesUrgentFlag(raisesUrgentFlag);
        applicationFormUserRoleDAO.save(applicationFormUserRole);
        return applicationFormUserRole;
    }

    private void assignToAdministrators(ApplicationForm applicationForm, ApplicationFormAction action, Date dueDate, Boolean bindDeadlineToDueDate) {
        Map<RegisteredUser, Authority> administrators = Maps.newHashMap();

        for (RegisteredUser superAdministrator : super.getUsersInRole(Authority.SUPERADMINISTRATOR)) {
            administrators.put(superAdministrator, Authority.SUPERADMINISTRATOR);
        }

        for (RegisteredUser administrator : applicationForm.getProgram().getAdministrators()) {
            administrators.put(administrator, Authority.ADMINISTRATOR);
        }
        
        Project project = applicationForm.getProject();
        if (project != null) {
        	administrators.put(project.getPrimarySupervisor(), Authority.PROJECTADMINISTRATOR);    	
        	RegisteredUser projectAdministrator = project.getAdministrator();
            if (projectAdministrator != null) {
                administrators.put(projectAdministrator, Authority.PROJECTADMINISTRATOR);
            }
        }

        StateChangeComment latestStateChangeComment = applicationForm.getLatestStateChangeComment();    
        if (latestStateChangeComment != null) {
            RegisteredUser stateAdministrator = latestStateChangeComment.getDelegateAdministrator();
            if (stateAdministrator != null) {
                administrators.put(stateAdministrator, Authority.STATEADMINISTRATOR);
            }
        }

        for (Entry<RegisteredUser, Authority> administrator : administrators.entrySet()) {
            Boolean raisesUrgentFlag = dueDate.before(new Date());

            List<ApplicationFormActionRequired> requiredActions = new ArrayList<ApplicationFormActionRequired>();
            requiredActions.add(new ApplicationFormActionRequired(action, dueDate, bindDeadlineToDueDate, raisesUrgentFlag));

            if (initiateStageMap.containsValue(action)) {
                requiredActions.add(new ApplicationFormActionRequired(ApplicationFormAction.MOVE_TO_DIFFERENT_STAGE, dueDate, bindDeadlineToDueDate, raisesUrgentFlag));
            }

            grantUserApplicationRole(applicationForm, administrator.getKey(), administrator.getValue(), false,
                    requiredActions.toArray(new ApplicationFormActionRequired[0]));
        }
    }
    
    private void deleteActionsAndFlushToDB(ApplicationFormUserRole applicationFormUserRole) {
    	applicationFormUserRoleDAO.deleteActionsAndFlushToDB(applicationFormUserRole);
    	RegisteredUser registeredUser = applicationFormUserRole.getUser();
    	Role role = applicationFormUserRole.getRole();
    	
    	if (applicationFormUserRoleDAO.findByUserAndRoleWithOutstandingActions(registeredUser, role).isEmpty()) {
    		super.revokeRolesFromUser(registeredUser, Authority.valueOf(role.getAuthority()));
    	}
    }

    private void resetActionDeadline(ApplicationForm applicationForm, Date deadlineTimestamp) {
        applicationFormUserRoleDAO.updateApplicationFormActionRequiredDeadline(applicationForm, deadlineTimestamp);
    }
    
    private void setInterestedInApplication(ApplicationForm applicationForm, RegisteredUser registeredUser, Boolean interested) {
    	for (ApplicationFormUserRole applicationFormUserRole : applicationFormUserRoleDAO.findByApplicationFormAndUser(applicationForm, registeredUser)) {
    		applicationFormUserRole.setInterestedInApplicant(interested);
    	}
    }

}