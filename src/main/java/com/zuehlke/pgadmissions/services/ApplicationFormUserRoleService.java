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

    private final ApplicationFormUserRoleDAO applicationFormUserRoleDAO;

    private final RoleDAO roleDAO;

    private final Map<ApplicationFormStatus, ApplicationFormAction> initiateStageMap = Maps.newHashMap();

    public ApplicationFormUserRoleService() {
        this(null, null);
    }

    @Autowired
    public ApplicationFormUserRoleService(ApplicationFormUserRoleDAO applicationFormUserRoleDAO, RoleDAO roleDAO) {
        this.applicationFormUserRoleDAO = applicationFormUserRoleDAO;
        this.roleDAO = roleDAO;

        initiateStageMap.put(ApplicationFormStatus.REVIEW, ApplicationFormAction.ASSIGN_REVIEWERS);
        initiateStageMap.put(ApplicationFormStatus.INTERVIEW, ApplicationFormAction.ASSIGN_INTERVIEWERS);
        initiateStageMap.put(ApplicationFormStatus.APPROVAL, ApplicationFormAction.ASSIGN_SUPERVISORS);
        initiateStageMap.put(ApplicationFormStatus.REJECTED, ApplicationFormAction.CONFIRM_REJECTION);
    }
    
    public void applicationCreated(ApplicationForm applicationForm) {
    	createApplicationFormUserRole(applicationForm, applicationForm.getApplicant(), Authority.APPLICANT, false);
    }

    public void applicationSubmitted(ApplicationForm applicationForm) {
        assignToAdministrators(applicationForm, ApplicationFormAction.COMPLETE_VALIDATION_STAGE, applicationForm.getDueDate(), true);

        for (RegisteredUser approver : applicationForm.getProgram().getApprovers()) {
            createApplicationFormUserRole(applicationForm, approver, Authority.APPROVER, false);
        }

        for (SuggestedSupervisor suggestedSupervisor : applicationForm.getProgrammeDetails().getSuggestedSupervisors()) {
        	String userToSaveAsSuggestedSupervisorEmail = suggestedSupervisor.getEmail();
            RegisteredUser userToSaveAsSuggestedSupervisor = super.getUserByEmail(userToSaveAsSuggestedSupervisorEmail);
           
            if (userToSaveAsSuggestedSupervisor == null) {
            	userToSaveAsSuggestedSupervisor = super.createRegisteredUser(suggestedSupervisor.getFirstname(), suggestedSupervisor.getLastname(), userToSaveAsSuggestedSupervisorEmail);
            }
            
            createApplicationFormUserRole(applicationForm, userToSaveAsSuggestedSupervisor, Authority.SUGGESTEDSUPERVISOR, true);
        }
    }

    public void validationStageCompleted(ApplicationForm application) {
        deassignFromStateBoundedWorkers(application);

        for (Referee referee : application.getReferees()) {
            createApplicationFormUserRole(application, referee.getUser(), Authority.REFEREE, false, 
            		new ApplicationFormActionRequired(ApplicationFormAction.PROVIDE_REFERENCE, new Date(), false, true));
        }

        Boolean anyUnsure = application.getValidationComment().isAtLeastOneAnswerUnsure();
        List<RegisteredUser> admitters = super.getUsersInRole(Authority.ADMITTER);
        List<ApplicationFormUserRole> superadministratorRoles = applicationFormUserRoleDAO.findByApplicationFormAndAuthorities(application, Authority.SUPERADMINISTRATOR);
        
        if (BooleanUtils.isTrue(anyUnsure)) {
        	for (RegisteredUser admitter : admitters) {
                createApplicationFormUserRole(application, admitter, Authority.ADMITTER, false, 
                		new ApplicationFormActionRequired(ApplicationFormAction.CONFIRM_ELIGIBILITY, new Date(), false, true));        		
        	}
        	for (ApplicationFormUserRole superadministratorRole : superadministratorRoles) {
        		superadministratorRole.getActions().add(new ApplicationFormActionRequired(ApplicationFormAction.CONFIRM_ELIGIBILITY, new Date(), false, true));
        		superadministratorRole.setRaisesUrgentFlag(true);
        	}
        } else {
        	for (RegisteredUser admitter : admitters) {
                createApplicationFormUserRole(application, admitter, Authority.ADMITTER, false);        		
        	}
        }
        
    }

    public void stateChanged(StateChangeComment stateChangeComment) {
        ApplicationForm application = stateChangeComment.getApplication();
        deassignFromStateBoundedWorkers(application);

        ApplicationFormStatus nextStatus = stateChangeComment.getNextStatus();
        if (initiateStageMap.containsKey(nextStatus)) {
            assignToAdministrators(application, initiateStageMap.get(nextStatus), new Date(), false);
        }

        List<RegisteredUser> approvers = application.getProgram().getApprovers();

        if (nextStatus == ApplicationFormStatus.APPROVED) {
            for (RegisteredUser approver : approvers) {
                createApplicationFormUserRole(application, approver, Authority.APPROVER, false, 
                		new ApplicationFormActionRequired(ApplicationFormAction.CONFIRM_OFFER_RECOMMENDATION, new Date(), false, true), 
                		new ApplicationFormActionRequired(ApplicationFormAction.MOVE_TO_DIFFERENT_STAGE, new Date(),
                        false, true));
            }

            for (RegisteredUser superAdministrator : super.getUsersInRole(Authority.SUPERADMINISTRATOR)) {
                createApplicationFormUserRole(application, superAdministrator, Authority.SUPERADMINISTRATOR, false, 
                		new ApplicationFormActionRequired(ApplicationFormAction.CONFIRM_OFFER_RECOMMENDATION, new Date(), false, true), 
                		new ApplicationFormActionRequired(ApplicationFormAction.MOVE_TO_DIFFERENT_STAGE, new Date(), false, true));
            }
        }

        else if (application.getStatus() == ApplicationFormStatus.APPROVAL && nextStatus != null) {
            for (RegisteredUser approver : approvers) {
                createApplicationFormUserRole(application, approver, Authority.APPROVER, false,
                        new ApplicationFormActionRequired(initiateStageMap.get(nextStatus), new Date(), false, true), 
                        new ApplicationFormActionRequired(ApplicationFormAction.MOVE_TO_DIFFERENT_STAGE, new Date(), false, true));
            }
        }
    }

    public void movedToReviewStage(ReviewRound reviewRound) {
        ApplicationForm application = reviewRound.getApplication();

        deassignFromStateBoundedWorkers(application);

        for (Reviewer reviewer : reviewRound.getReviewers()) {
            createApplicationFormUserRole(reviewRound.getApplication(), reviewer.getUser(), Authority.REVIEWER, false, 
            		new ApplicationFormActionRequired(ApplicationFormAction.PROVIDE_REVIEW, new Date(), false, true));
        }

        assignToAdministrators(application, ApplicationFormAction.COMPLETE_REVIEW_STAGE, application.getDueDate(), true);
    }

    public void movedToInterviewStage(Interview interview) {
        ApplicationForm application = interview.getApplication();

        deassignFromStateBoundedWorkers(application);

        if (interview.isScheduling()) {
            assignToAdministrators(application, ApplicationFormAction.CONFIRM_INTERVIEW_ARRANGEMENTS, application.getDueDate(), true);

            for (InterviewParticipant participant : interview.getParticipants()) {
                Boolean isApplicant = participant.getUser().getId().equals(application.getApplicant().getId());
                Authority authority = isApplicant ? Authority.APPLICANT : Authority.INTERVIEWER;
                createApplicationFormUserRole(application, participant.getUser(), authority, false, 
                		new ApplicationFormActionRequired(ApplicationFormAction.PROVIDE_INTERVIEW_AVAILABILITY, new Date(), false, true));
            }
        } else {
            for (Interviewer interviewer : interview.getInterviewers()) {
                Boolean raisesUrgentFlag = interview.getInterviewDueDate().before(new Date());

                createApplicationFormUserRole(application, interviewer.getUser(), Authority.INTERVIEWER, false, 
                		new ApplicationFormActionRequired(ApplicationFormAction.PROVIDE_INTERVIEW_FEEDBACK, interview.getInterviewDueDate(), false, raisesUrgentFlag));
            }
        }
        assignToAdministrators(application, ApplicationFormAction.COMPLETE_INTERVIEW_STAGE, application.getDueDate(), true);
    }

    public void movedToApprovalStage(ApprovalRound approvalRound) {
        ApplicationForm applicationForm = approvalRound.getApplication();
        deassignFromStateBoundedWorkers(applicationForm);

        Supervisor primarySupervisor = approvalRound.getPrimarySupervisor();
        createApplicationFormUserRole(approvalRound.getApplication(), primarySupervisor.getUser(), Authority.SUPERVISOR, false,
                new ApplicationFormActionRequired(ApplicationFormAction.CONFIRM_PRIMARY_SUPERVISION, new Date(), false, true));

        assignToAdministrators(applicationForm, ApplicationFormAction.COMPLETE_APPROVAL_STAGE, applicationForm.getDueDate(), true);

        List<RegisteredUser> approvers = applicationForm.getProgram().getApprovers();
        for (RegisteredUser approver : approvers) {
            createApplicationFormUserRole(applicationForm, approver, Authority.APPROVER, false, 
            		new ApplicationFormActionRequired(ApplicationFormAction.COMPLETE_APPROVAL_STAGE, applicationForm.getDueDate(), true, false));
        }
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
        ApplicationFormUserRole role = applicationFormUserRoleDAO.findByApplicationFormAndUserAndAuthority(application, referee.getUser(), Authority.REFEREE);     
        if (role != null) {
        	deleteActionsAndFlushToDB(role);
        }
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

    }

    public void interviewConfirmed(Interview interview) {
        ApplicationForm application = interview.getApplication();

        deassignFromStateBoundedWorkers(application);

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
    }

    public void interviewFeedbackPosted(Interviewer interviewer) {
        Interview interview = interviewer.getInterview();
        ApplicationForm application = interview.getApplication();
        InterviewComment interviewComment = interviewer.getInterviewComment();

        ApplicationFormUserRole role = applicationFormUserRoleDAO.findByApplicationFormAndUserAndAuthority(application, interviewer.getUser(),
                Authority.INTERVIEWER);
        setInterestedInApplication(application, interviewer.getUser(), interviewComment.getWillingToSupervise());

        deleteActionsAndFlushToDB(role);

        if (interview.hasAllInterviewersProvidedFeedback()) {
            resetActionDeadline(application, new Date());
        }

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

    public void moveToApprovedOrRejectedOrWithdrawn(ApplicationForm applicationForm) {
        applicationFormUserRoleDAO.deleteAllApplicationFormActions(applicationForm);
    }

    public void createUserInRole(RegisteredUser registeredUser, Authority authority) {
        applicationFormUserRoleDAO.insertUserinRole(registeredUser, authority);
        super.addRoleToUser(registeredUser, authority);
    }

    public void createUserInProgramRoles(RegisteredUser registeredUser, Program program, Authority... authorities) {
    	for (Authority authority : authorities) {
    		applicationFormUserRoleDAO.insertUserInProgramRole(registeredUser, program, authority);
    		super.updateUserProgramRoles(registeredUser, program, authorities);
    	}
    }

    public void revokeUserFromRole(RegisteredUser registeredUser, Authority authority) {
        applicationFormUserRoleDAO.deleteUserFromRole(registeredUser, authority);
        super.revokeRoleFromUser(registeredUser, authority);
    }

    public void revokeUserFromProgramRoles(RegisteredUser registeredUser, Program program, Authority... authorities) {
    	for (Authority authority : authorities) {
    		applicationFormUserRoleDAO.deleteUserFromProgramRole(registeredUser, program, authority);
    		super.deleteUserFromProgramme(registeredUser, program);
    	}
    }

    public void registerApplicationUpdate(ApplicationForm applicationForm, RegisteredUser author, ApplicationUpdateScope updateVisibility) {
        Date updateTimestamp = new Date();
        applicationFormUserRoleDAO.updateApplicationFormUpdateTimestamp(applicationForm, author, updateTimestamp, updateVisibility);
    }

    public List<RegisteredUser> getUsersInterestedInApplication(ApplicationForm applicationForm) {
        return applicationFormUserRoleDAO.findUsersInterestedInApplication(applicationForm);
    }

    public List<RegisteredUser> getUsersPotentiallyInterestedInApplication(ApplicationForm applicationForm) {
        return applicationFormUserRoleDAO.findUsersPotentiallyInterestedInApplication(applicationForm);
    }

    public void deregisterApplicationUpdate(ApplicationForm applicationForm, RegisteredUser registeredUser) {
        for (ApplicationFormUserRole applicationFormUserRole : applicationFormUserRoleDAO.findByApplicationFormAndUser(applicationForm, registeredUser)) {
            applicationFormUserRole.setRaisesUpdateFlag(false);
        }
    }

    private ApplicationFormUserRole createApplicationFormUserRole(ApplicationForm applicationForm, RegisteredUser user, Authority authority,
            Boolean interestedInApplicant, ApplicationFormActionRequired... actions) {
        Role role = roleDAO.getRoleByAuthority(authority); 
        super.addRoleToUser(user, authority);
        
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

            createApplicationFormUserRole(applicationForm, administrator.getKey(), administrator.getValue(), false,
                    requiredActions.toArray(new ApplicationFormActionRequired[0]));
        }
    }

    private void deassignFromStateBoundedWorkers(ApplicationForm applicationForm) {
        applicationFormUserRoleDAO.deleteApplicationFormActionsForStateBoundedWorkers(applicationForm);
    }
    
    private void deleteActionsAndFlushToDB(ApplicationFormUserRole applicationFormUserRole) {
    	applicationFormUserRoleDAO.deleteActionsAndFlushToDB(applicationFormUserRole);
    	
    	RegisteredUser registeredUser = applicationFormUserRole.getUser();
    	Role role = applicationFormUserRole.getRole();
    	
    	if (applicationFormUserRoleDAO.findByUserAndRoleWithOutstandingActions(registeredUser, role).isEmpty()) {
    		super.revokeRoleFromUser(registeredUser, Authority.valueOf(role.getAuthority()));
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