package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import com.zuehlke.pgadmissions.domain.StateChangeComment;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;

@Service
@Transactional
public class ApplicationFormUserRoleService {

    private final ApplicationFormUserRoleDAO applicationFormUserRoleDAO;

    private final RoleDAO roleDAO;

    private final UserDAO userDAO;
    
    private final ApplicationFormDAO applicationFormDAO;
    
    private final Map<ApplicationFormStatus, String> initiateStageMap = Maps.newHashMap();
    
    private final Map<ApplicationFormStatus, String> completeStageMap = Maps.newHashMap();

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
        
        completeStageMap.put(ApplicationFormStatus.VALIDATION, "COMPLETE_VALIDATION_STAGE");
        completeStageMap.put(ApplicationFormStatus.REVIEW, "COMPLETE_REVIEW_STAGE");
        completeStageMap.put(ApplicationFormStatus.INTERVIEW, "COMPLETE_INTERVIEW_STAGE");
        completeStageMap.put(ApplicationFormStatus.APPROVAL, "COMPLETE_APPROVAL_STAGE");
        
    }

    public void applicationSubmitted(ApplicationForm applicationForm) {
        createApplicationFormUserRole(applicationForm, applicationForm.getApplicant(), Authority.APPLICANT, true);

        assignToAdministrators(applicationForm, "COMPLETE_VALIDATION_STAGE", applicationForm.getDueDate(), true);

        for (RegisteredUser approver : applicationForm.getProgram().getApprovers()) {
            createApplicationFormUserRole(applicationForm, approver, Authority.APPROVER, false);
        }
    }

    public void validationStageCompleted(ApplicationForm application) {
        for (Referee referee : application.getReferees()) {
            createApplicationFormUserRole(application, referee.getUser(), Authority.REFEREE, false, 
            		new ApplicationFormActionRequired("PROVIDE_REFERENCE", new Date(), false, true));
        }

        ValidationComment validationComment = application.getValidationComment();
        boolean anyUnsure = validationComment.isAtLeastOneAnswerUnsure();

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
        List<RegisteredUser> approvers = application.getProgram().getApprovers();

        assignToAdministrators(application, initiateStageMap.get(nextStatus), new Date(), false);

        if (nextStatus == ApplicationFormStatus.APPROVED) {
            for (RegisteredUser approver : approvers) {
                createApplicationFormUserRole(application, approver, Authority.APPROVER, false, new ApplicationFormActionRequired(
                        "COMPLETE_OFFER_RECOMMENDATION", new Date(), false, true));
            }

            for (RegisteredUser superAdministrator : userDAO.getSuperadministrators()) {
                createApplicationFormUserRole(application, superAdministrator, Authority.SUPERADMINISTRATOR, false, new ApplicationFormActionRequired(
                        "COMPLETE_OFFER_RECOMMENDATION", new Date(), false, true));
            }
        }

        if (application.getStatus() == ApplicationFormStatus.APPROVAL && nextStatus != null) {
            for (RegisteredUser approver : approvers) {
                createApplicationFormUserRole(application, approver, Authority.APPROVER, false, new ApplicationFormActionRequired(initiateStageMap.get(nextStatus),
                        new Date(), false, true));
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
            	boolean raisesUrgentFlag = interview.getInterviewDueDate().after(new Date());
            	
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
        }
    }

    public void referencePosted(Referee referee) {
        ApplicationForm application = referee.getApplication();
        ApplicationFormUserRole role = applicationFormUserRoleDAO.findByApplicationFormAndUserAndAuthority(application, referee.getUser(), Authority.REFEREE);
        role.getActions().clear();
    }

    public void reviewPosted(Reviewer reviewer) {
        ReviewRound reviewRound = reviewer.getReviewRound();
        ApplicationForm application = reviewRound.getApplication();
        ReviewComment review = reviewer.getReview();

        ApplicationFormUserRole role = applicationFormUserRoleDAO.findByApplicationFormAndUserAndAuthority(application, reviewer.getUser(), Authority.REVIEWER);
        role.setInterestedInApplicant(review.getWillingToInterview() || review.getWillingToWorkWithApplicant());
        role.getActions().clear();

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

        if (interview.hasAllParticipantsProvidedAvailability()) {
            resetActionDeadline(application, new Date());
        }
    }

    public void interviewConfirmed(Interview interview) {
        ApplicationForm application = interview.getApplication();
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
        }
        deassignFromAdministrators(application);
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

        resetActionDeadline(application, new Date());
    }

    public void moveToApprovedOrRejectedOrWithdrawn(ApplicationForm application) {
        List<ApplicationFormUserRole> roles = applicationFormUserRoleDAO.findByApplicationForm(application);

        for (ApplicationFormUserRole role : roles) {
            role.getActions().clear();
        }

    }
    
    public void createUserInSuperAdministratorRole (RegisteredUser registeredUser) {
    	List<ApplicationForm> applications = applicationFormDAO.getAllAdministerableApplications();
    	for (ApplicationForm application : applications) {
    		ApplicationFormUserRole applicationFormUserRole = 
    				createApplicationFormUserRole(application, registeredUser, Authority.SUPERADMINISTRATOR, false);	
    		assignActionsToNewAdmitter(application, applicationFormUserRole);
    		assignActionsToNewAdministrator(application, applicationFormUserRole);
    		assignActionsToNewApprover(application, applicationFormUserRole);
    	}
    }
    
    public void createUserInAdmitterRole (RegisteredUser registeredUser) {
    	List<ApplicationForm> applications = applicationFormDAO.getAllAdministerableApplications();
    	for (ApplicationForm application : applications) {
    		assignActionsToNewAdmitter(application,
    				createApplicationFormUserRole(application, registeredUser, Authority.ADMITTER, false));
    	}
    }
    
    public void createUserInProgramAdministratorRole(RegisteredUser registeredUser, Program program) {
    	List<ApplicationForm> applications = applicationFormDAO.getAdministerableApplicationsByProgram(program);
        
    	for (ApplicationForm application : applications) {
    		assignActionsToNewAdministrator(application, 
    				createApplicationFormUserRole(application, registeredUser, Authority.ADMINISTRATOR, false));
    	}
    }
    
    public void createUserInProgramApproveRole(RegisteredUser registeredUser, Program program) {
    	List<ApplicationForm> applications = applicationFormDAO.getAdministerableApplicationsByProgram(program);
        
    	for (ApplicationForm application : applications) {
    		assignActionsToNewApprover(application, 
    				createApplicationFormUserRole(application, registeredUser, Authority.APPROVER, false));
    	}
    }
    
    private void assignActionsToNewAdmitter(ApplicationForm application, ApplicationFormUserRole applicationFormUserRole) {
    	ApplicationFormStatus currentStatus = application.getStatus();
		ApplicationFormStatus nextStatus = application.getNextStatus();
		List<ApplicationFormStatus> admitterCommentScope = Arrays.asList(ApplicationFormStatus.REVIEW, ApplicationFormStatus.INTERVIEW, ApplicationFormStatus.APPROVAL);
		
		if (admitterCommentScope.contains(currentStatus) &&
				(nextStatus == null ||
				admitterCommentScope.contains(nextStatus)) &&
				!application.hasConfirmElegibilityComment()) {
			applicationFormUserRole.getActions().add(new ApplicationFormActionRequired("CONFIRM_ELIGIBILITY", new Date(), false, true));	
		}
    }
    
    public void revokeUserRole (RegisteredUser registeredUser, Authority authority) {
    	for (ApplicationFormUserRole applicationFormUserRole : applicationFormUserRoleDAO.findByUserAndAuthority(registeredUser, authority)) {
    		applicationFormUserRoleDAO.delete(applicationFormUserRole);
    	}
    }
    
    public void revokeUserProgramRole (RegisteredUser registeredUser, Program program, Authority authority) {
    	for (ApplicationFormUserRole applicationFormUserRole : applicationFormUserRoleDAO.findByUserAndProgramAndAuthority(registeredUser, program, authority)) {
    		applicationFormUserRoleDAO.delete(applicationFormUserRole);
    	}
    }
    
    public void registerApplicationUpdate (ApplicationForm applicationForm, Date updateTimestamp, int updateVisibility) {
    	for (ApplicationFormUserRole applicationFormUserRole : applicationFormUserRoleDAO.findByApplicationFormAndAuthorityUpdateVisility(applicationForm, updateVisibility)) {
    		applicationFormUserRole.setUpdateTimestamp(updateTimestamp);
    		applicationFormUserRole.setRaisesUpdateFlag(true);
    	}
    }
    
    public void deregisterApplicationUpdate (ApplicationForm applicationForm, RegisteredUser registeredUser) {
    	for (ApplicationFormUserRole applicationFormUserRole : applicationFormUserRoleDAO.findByApplicationFormAndUser(applicationForm, registeredUser)) {
    		applicationFormUserRole.setRaisesUpdateFlag(false);
    	}
    }
    
    private ApplicationFormUserRole createApplicationFormUserRole(ApplicationForm applicationForm, RegisteredUser user, Authority authority,
            boolean interestedInApplicant, ApplicationFormActionRequired... actions) {

        ApplicationFormUserRole applicationFormUserRole = applicationFormUserRoleDAO.findByApplicationFormAndUserAndAuthority(applicationForm, user, authority);

        if (applicationFormUserRole == null) {
            applicationFormUserRole = new ApplicationFormUserRole();
            applicationFormUserRole.setApplicationForm(applicationForm);
            applicationFormUserRole.setRole(roleDAO.getRoleByAuthority(authority));
            applicationFormUserRole.setUser(user);
        }

        applicationFormUserRole.setInterestedInApplicant(interestedInApplicant);
        
        for (ApplicationFormActionRequired action : actions) {
            applicationFormUserRole.getActions().add(action);
        }

        applicationFormUserRoleDAO.save(applicationFormUserRole);
        return applicationFormUserRole;
    }
    
    private void assignActionsToNewAdministrator(ApplicationForm application, ApplicationFormUserRole applicationFormUserRole) {
    	ApplicationFormStatus currentStatus = application.getStatus();
		ApplicationFormStatus nextStatus = application.getNextStatus();
		
		List<ApplicationFormActionRequired> actionsRequired = new ArrayList<ApplicationFormActionRequired>();
		
		if (nextStatus == null) {
			boolean raisesUrgentFlag = application.getDueDate().after(new Date());
			
			if (currentStatus == ApplicationFormStatus.INTERVIEW &&
	    		application.getLatestInterview().isScheduling()) {
				actionsRequired.add(new ApplicationFormActionRequired("CONFIRM_INTERVIEW_ARRANGEMENTS", application.getDueDate(), true, raisesUrgentFlag));
			}
			actionsRequired.add(new ApplicationFormActionRequired(completeStageMap.get(currentStatus), application.getDueDate(), true, raisesUrgentFlag));
			applicationFormUserRole.setRaisesUrgentFlag(raisesUrgentFlag);
		} else {
			actionsRequired.add(new ApplicationFormActionRequired(initiateStageMap.get(nextStatus), new Date(), false, false));
		}
		applicationFormUserRole.getActions().addAll(actionsRequired);
    }
    
    private void assignActionsToNewApprover(ApplicationForm application, ApplicationFormUserRole applicationFormUserRole) {
    	ApplicationFormStatus currentStatus = application.getStatus();
		ApplicationFormStatus nextStatus = application.getNextStatus();
		
		ApplicationFormActionRequired actionRequired;
		
		if (currentStatus == ApplicationFormStatus.APPROVAL) {
    		if (nextStatus == null) {
    			boolean raisesUrgentFlag = application.getDueDate().after(new Date());
    			actionRequired = new ApplicationFormActionRequired(completeStageMap.get(currentStatus), application.getDueDate(), true, raisesUrgentFlag);
    		} else {
    			actionRequired = new ApplicationFormActionRequired(initiateStageMap.get(nextStatus), new Date(), false, false);
    		}
    		applicationFormUserRole.getActions().add(actionRequired);
		}
    }
    
    private void assignToAdministrators(ApplicationForm applicationForm, String action, Date dueDate, Boolean bindDealineToDueDate) {
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
        	boolean raisesUrgentFlag = dueDate.after(new Date());
            createApplicationFormUserRole(applicationForm, administrator.getKey(), administrator.getValue(), false, 
            		new ApplicationFormActionRequired(action, dueDate, bindDealineToDueDate, raisesUrgentFlag));
        }
    }

    private void deassignFromAdministrators(ApplicationForm applicationForm) {
        List<ApplicationFormUserRole> roles = applicationFormUserRoleDAO.findByApplicationFormAndAuthorities(applicationForm, Authority.REVIEWER,
                Authority.INTERVIEWER, Authority.SUPERVISOR, Authority.APPLICANT, Authority.ADMINISTRATOR, Authority.SUPERADMINISTRATOR,
                Authority.APPROVALADMINISTRATOR, Authority.INTERVIEWADMINISTRATOR, Authority.PROJECTADMINISTRATOR, Authority.REVIEWADMINISTRATOR,
                Authority.APPROVER);
        for (ApplicationFormUserRole role : roles) {
            applicationFormUserRoleDAO.clearActions(role);
            
        }
    }

    private void resetActionDeadline(ApplicationForm applicationForm, Date newDueDate) {
        applicationForm.setDueDate(newDueDate);
        List<ApplicationFormUserRole> applicationFormUserRoles = applicationFormUserRoleDAO.findByApplicationForm(applicationForm);
        boolean raisesUrgentFlag = newDueDate.after(new Date());
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