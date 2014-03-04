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
import com.zuehlke.pgadmissions.dao.ActionDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormUserRoleDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.AdmitterComment;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormActionRequired;
import com.zuehlke.pgadmissions.domain.ApplicationFormUserRole;
import com.zuehlke.pgadmissions.domain.AssignInterviewersComment;
import com.zuehlke.pgadmissions.domain.AssignReviewersComment;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.InterviewScheduleComment;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.SupervisionConfirmationComment;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.ActionDefinition;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

@Service
@Transactional
public class ApplicationFormUserRoleService {

    private final ApplicationFormUserRoleDAO applicationFormUserRoleDAO;

    private final ApplicationsService applicationsService;

    private final ActionDAO actionDAO;

    private final RoleDAO roleDAO;

    private final UserDAO userDAO;

    private final EncryptionUtils encryptionUtils;

    private final Map<ApplicationFormStatus, ApplicationFormAction> initiateStageMap = Maps.newHashMap();

    public ApplicationFormUserRoleService() {
        this(null, null, null, null, null, null);
    }

    @Autowired
    public ApplicationFormUserRoleService(ApplicationFormUserRoleDAO applicationFormUserRoleDAO, ApplicationsService applicationsService, ActionDAO actionDAO,
            RoleDAO roleDAO, UserDAO userDAO, EncryptionUtils encryptionUtils) {
        this.applicationFormUserRoleDAO = applicationFormUserRoleDAO;
        this.applicationsService = applicationsService;
        this.actionDAO = actionDAO;
        this.roleDAO = roleDAO;
        this.userDAO = userDAO;
        this.encryptionUtils = encryptionUtils;

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
            String supervisorEmail = suggestedSupervisor.getEmail();
            RegisteredUser userToSaveAsSuggestedSupervisor = userDAO.getUserByEmailIncludingDisabledAccounts(supervisorEmail);

            if (userToSaveAsSuggestedSupervisor == null) {
                userToSaveAsSuggestedSupervisor = new RegisteredUser();
                userToSaveAsSuggestedSupervisor.setFirstName(suggestedSupervisor.getFirstname());
                userToSaveAsSuggestedSupervisor.setLastName(suggestedSupervisor.getLastname());
                userToSaveAsSuggestedSupervisor.setUsername(suggestedSupervisor.getEmail());
                userToSaveAsSuggestedSupervisor.setEmail(suggestedSupervisor.getEmail());
                userToSaveAsSuggestedSupervisor.setAccountNonExpired(true);
                userToSaveAsSuggestedSupervisor.setAccountNonLocked(true);
                userToSaveAsSuggestedSupervisor.setEnabled(false);
                userToSaveAsSuggestedSupervisor.setCredentialsNonExpired(true);
                userToSaveAsSuggestedSupervisor.setActivationCode(encryptionUtils.generateUUID());
                userToSaveAsSuggestedSupervisor.getRoles().add(roleDAO.getRoleByAuthority(Authority.SUGGESTEDSUPERVISOR));
                userDAO.save(userToSaveAsSuggestedSupervisor);
            }

            createApplicationFormUserRole(applicationForm, userToSaveAsSuggestedSupervisor, Authority.SUGGESTEDSUPERVISOR, true);
        }
    }

    public void validationStageCompleted(ApplicationForm application) {
        deleteStateActions(application);
        
        for (Referee referee : application.getReferees()) {
            createApplicationFormUserRole(application, referee.getUser(), Authority.REFEREE, false,
                    new ApplicationFormActionRequired(actionDAO.getActionById(ApplicationFormAction.PROVIDE_REFERENCE), new Date(), false, true));
        }

        Boolean anyUnsure = application.getValidationComment().isAtLeastOneAnswerUnsure();
        List<RegisteredUser> admitters = userDAO.getAdmitters();
        List<ApplicationFormUserRole> superadministratorRoles = applicationFormUserRoleDAO.findByApplicationFormAndAuthorities(application,
                Authority.SUPERADMINISTRATOR);

        if (BooleanUtils.isTrue(anyUnsure)) {
            for (RegisteredUser admitter : admitters) {
                createApplicationFormUserRole(application, admitter, Authority.ADMITTER, false,
                        new ApplicationFormActionRequired(actionDAO.getActionById(ApplicationFormAction.CONFIRM_ELIGIBILITY), new Date(), false, true));
            }
            for (ApplicationFormUserRole superadministratorRole : superadministratorRoles) {
                superadministratorRole.getActions().add(
                        new ApplicationFormActionRequired(actionDAO.getActionById(ApplicationFormAction.CONFIRM_ELIGIBILITY), new Date(), false, true));
                superadministratorRole.setRaisesUrgentFlag(true);
            }
        } else {
            for (RegisteredUser admitter : admitters) {
                createApplicationFormUserRole(application, admitter, Authority.ADMITTER, false);
            }
        }

    }

    public void stateChanged(Comment stateChangeComment) {
        ApplicationForm application = stateChangeComment.getApplication();

        deleteStateActions(application);

        ApplicationFormStatus nextStatus = stateChangeComment.getNextStatus();
        if (initiateStageMap.containsKey(nextStatus)) {
            assignToAdministrators(application, initiateStageMap.get(nextStatus), new Date(), false);
        }

        List<RegisteredUser> approvers = application.getProgram().getApprovers();

        if (nextStatus == ApplicationFormStatus.APPROVED) {
            for (RegisteredUser approver : approvers) {
                createApplicationFormUserRole(
                        application,
                        approver,
                        Authority.APPROVER,
                        false,
                        new ApplicationFormActionRequired(actionDAO.getActionById(ApplicationFormAction.CONFIRM_OFFER_RECOMMENDATION), new Date(), false, true),
                        new ApplicationFormActionRequired(actionDAO.getActionById(ApplicationFormAction.MOVE_TO_DIFFERENT_STAGE), new Date(), false, true));
            }

            for (RegisteredUser superAdministrator : userDAO.getSuperadministrators()) {
                createApplicationFormUserRole(
                        application,
                        superAdministrator,
                        Authority.SUPERADMINISTRATOR,
                        false,
                        new ApplicationFormActionRequired(actionDAO.getActionById(ApplicationFormAction.CONFIRM_OFFER_RECOMMENDATION), new Date(), false, true),
                        new ApplicationFormActionRequired(actionDAO.getActionById(ApplicationFormAction.MOVE_TO_DIFFERENT_STAGE), new Date(), false, true));
            }
        }

        else if (application.getStatus() == ApplicationFormStatus.APPROVAL && nextStatus != null) {
            for (RegisteredUser approver : approvers) {
                createApplicationFormUserRole(application, approver, Authority.APPROVER, false,
                        new ApplicationFormActionRequired(actionDAO.getActionById(initiateStageMap.get(nextStatus)), new Date(), false, true),
                        new ApplicationFormActionRequired(actionDAO.getActionById(ApplicationFormAction.MOVE_TO_DIFFERENT_STAGE), new Date(), false, true));
            }
        }
    }

    public void movedToReviewStage(AssignReviewersComment assignReviewersComment) {
        ApplicationForm application = assignReviewersComment.getApplication();

        deleteStateActions(application);

        for (CommentAssignedUser assignedUser : assignReviewersComment.getAssignedUsers()) {
            createApplicationFormUserRole(application, assignedUser.getUser(), Authority.REVIEWER, false,
                    new ApplicationFormActionRequired(actionDAO.getActionById(ApplicationFormAction.PROVIDE_REVIEW), new Date(), false, true));
        }

        assignToAdministrators(application, ApplicationFormAction.COMPLETE_REVIEW_STAGE, application.getDueDate(), true);
    }

    public void movedToInterviewStage(AssignInterviewersComment comment) {
        ApplicationForm application = comment.getApplication();

        deleteStateActions(application);

        // FIXME uncomment and fix
        // if (comment.isScheduling()) {
        // assignToAdministrators(application, ApplicationFormAction.CONFIRM_INTERVIEW_ARRANGEMENTS, application.getDueDate(), true);
        //
        // for (InterviewParticipant participant : comment.getParticipants()) {
        // Boolean isApplicant = participant.getUser().getId().equals(application.getApplicant().getId());
        // Authority authority = isApplicant ? Authority.APPLICANT : Authority.INTERVIEWER;
        // createApplicationFormUserRole(application, participant.getUser(), authority, false,
        // new ApplicationFormActionRequired(actionDAO.getActionById(ApplicationFormAction.PROVIDE_INTERVIEW_AVAILABILITY), new Date(), false,
        // true));
        // }
        // } else {
        // for (CommentAssignedUser interviewer : comment.getAssignedUsers()) {
        // Boolean raisesUrgentFlag = comment.getInterviewDate().before(new Date());
        //
        // createApplicationFormUserRole(
        // application,
        // interviewer.getUser(),
        // Authority.INTERVIEWER,
        // false,
        // new ApplicationFormActionRequired(actionDAO.getActionById(ApplicationFormAction.PROVIDE_INTERVIEW_FEEDBACK), comment
        // .getInterviewDate(), false, raisesUrgentFlag));
        // }
        // }
        assignToAdministrators(application, ApplicationFormAction.COMPLETE_INTERVIEW_STAGE, application.getDueDate(), true);
    }

    public void movedToApprovalStage(Comment approvalComment) {
        ApplicationForm applicationForm = approvalComment.getApplication();
        deleteStateActions(applicationForm);

        CommentAssignedUser primarySupervisor = approvalComment.getPrimaryAssignedUser();
        createApplicationFormUserRole(applicationForm, primarySupervisor.getUser(), Authority.SUPERVISOR, false,
                new ApplicationFormActionRequired(actionDAO.getActionById(ApplicationFormAction.CONFIRM_PRIMARY_SUPERVISION), new Date(), false, true));

        assignToAdministrators(applicationForm, ApplicationFormAction.COMPLETE_APPROVAL_STAGE, applicationForm.getDueDate(), true);

        List<RegisteredUser> approvers = applicationForm.getProgram().getApprovers();
        for (RegisteredUser approver : approvers) {
            createApplicationFormUserRole(applicationForm, approver, Authority.APPROVER, false,
                    new ApplicationFormActionRequired(actionDAO.getActionById(ApplicationFormAction.COMPLETE_APPROVAL_STAGE), applicationForm.getDueDate(),
                            true, false));
        }
    }

    public void admitterCommentPosted(AdmitterComment comment) {
        ApplicationForm application = comment.getApplication();
        deleteRoleAction(application, Authority.ADMITTER, ApplicationFormAction.CONFIRM_ELIGIBILITY);
        deleteRoleAction(application, Authority.SUPERADMINISTRATOR, ApplicationFormAction.CONFIRM_ELIGIBILITY);
    }

    public void referencePosted(ReferenceComment referenceComment) {
        deleteUserAction(referenceComment.getApplication(), referenceComment.getUser(), Authority.REFEREE, ApplicationFormAction.PROVIDE_REFERENCE);  
    }

    public void reviewPosted(ReviewComment reviewComment) {
        ApplicationForm application = reviewComment.getApplication();
        RegisteredUser user = reviewComment.getUser();
        
        deleteUserAction(application, user, Authority.REVIEWER, ApplicationFormAction.PROVIDE_REVIEW);
        updateApplicationInterest(application, user, reviewComment.getWillingToInterview() || reviewComment.getWillingToSupervise());

        // TODO check if all reviewers provided feedback
        // if (reviewRound.hasAllReviewersResponded()) {
        //     updateApplicationDueDate(application);
        // }

    }

    public void interviewParticipantResponded(AssignInterviewersComment assignInterviewersComment, RegisteredUser participant) {
        // TODO fix the method by using new arguments (participant may be assignedUser or applicant)
        // Interview interview = participant.getInterview();
        // ApplicationForm application = interview.getApplication();
        // RegisteredUser user = participant.getUser();
        // 
        // deleteProvideInterviewAvailabilityAction(application, user);
        // 
        // if (interview.hasAllParticipantsProvidedAvailability()) {
        //     updateApplicationDueDate(application);
        // }

    }

    public void interviewConfirmed(InterviewScheduleComment interviewScheduleComment) {
        // TODO change interview to interviewScheduleComment
        // ApplicationForm application = interview.getApplication();
        // 
        // deleteStateActions(application);
        // 
        // for (InterviewParticipant participant : interview.getParticipants()) {
        //     RegisteredUser user = participant.getUser();
        //     Boolean isApplicant = user.isApplicant(application, user);
        //     
        //     deleteProvideInterviewAvailabilityAction(application, user);
        // 
        //     if (!isApplicant) {
        //         Date dateNow = new Date();
        //         ApplicationFormUserRole role = applicationFormUserRoleDAO.findByApplicationFormAndUserAndAuthority(application, user, Authority.INTERVIEWER);
        //         role.getActions().add(new ApplicationFormActionRequired(actionDAO.getActionById(ApplicationFormAction.PROVIDE_INTERVIEW_FEEDBACK), dateNow, false, interview.getInterviewDueDate().before(dateNow)));
        //     }
        //     
        // }
        // assignToAdministrators(application, ApplicationFormAction.COMPLETE_INTERVIEW_STAGE, application.getDueDate(), true);
    }

    public void interviewFeedbackPosted(InterviewComment interviewComment) {
        ApplicationForm application = interviewComment.getApplication();
        RegisteredUser user = interviewComment.getUser();

        deleteUserAction(application, user, Authority.INTERVIEWER, ApplicationFormAction.PROVIDE_INTERVIEW_FEEDBACK);
        updateApplicationInterest(application, user, interviewComment.getWillingToSupervise());

        // TODO check if all interviewers provided feedback
        // if (interview.hasAllInterviewersProvidedFeedback()) {
        //     updateApplicationDueDate(application);
        // }

    }

    public void supervisionConfirmed(SupervisionConfirmationComment comment) {
        // TODO use comment instead of supervisor

        // ApprovalRound approval = supervisor.getApprovalRound();
        // ApplicationForm application = approval.getApplication();
        // RegisteredUser user = supervisor.getUser();
        // 
        // deleteUserAction(application, user, Authority.SUPERVISOR, ApplicationFormAction.CONFIRM_PRIMARY_SUPERVISION);
        // updateApplicationInterest(application, user, supervisor.getConfirmedSupervision());
        // 
        // updateApplicationDueDate(application);

    }
    
    public List<RegisteredUser> getUsersInterestedInApplication(ApplicationForm applicationForm) {
        return applicationFormUserRoleDAO.findUsersInterestedInApplication(applicationForm);
    }
    public List<RegisteredUser> getUsersPotentiallyInterestedInApplication(ApplicationForm applicationForm) {
        return applicationFormUserRoleDAO.findUsersPotentiallyInterestedInApplication(applicationForm);
    }
    
    public void deleteApplicationActions(ApplicationForm applicationForm) {
        applicationFormUserRoleDAO.deleteApplicationActions(applicationForm);
    }
    
    public void deleteApplicationUpdate(ApplicationForm applicationForm, RegisteredUser registeredUser) {
        applicationFormUserRoleDAO.deleteApplicationUpdate(applicationForm, registeredUser);
    }
    
    public void deleteProgramRole(RegisteredUser registeredUser, Program program, Authority authority) {
        applicationFormUserRoleDAO.deleteProgramRole(registeredUser, program, authority);
    }
 
    private void deleteRoleAction(ApplicationForm applicationForm, Authority authority, ApplicationFormAction action) {
        applicationFormUserRoleDAO.deleteRoleAction(applicationForm, authority, action);
    }
     
    private void deleteStateActions(ApplicationForm applicationForm) {
        applicationFormUserRoleDAO.deleteStateActions(applicationForm);
    }
    
    private void deleteUserAction(ApplicationForm applicationForm, RegisteredUser registeredUser, Authority authority, ApplicationFormAction action) {
        deleteUserAction(applicationForm, registeredUser, authority, action);
    }
    
    public void deleteUserRole(RegisteredUser registeredUser, Authority authority) {
        applicationFormUserRoleDAO.deleteUserRole(registeredUser, authority);
    }
    
    public void insertApplicationUpdate(ApplicationForm applicationForm, RegisteredUser author, ApplicationUpdateScope updateVisibility) {
        Date updateTimestamp = new Date();
        applicationFormUserRoleDAO.insertApplicationUpdate(applicationForm, author, updateTimestamp, updateVisibility);
    }
    
    public void insertProgramRole(RegisteredUser registeredUser, Program program, Authority authority) {
        applicationFormUserRoleDAO.insertProgramRole(registeredUser, program, authority);
    }
    
    public void insertUserRole(RegisteredUser registeredUser, Authority authority) {
        applicationFormUserRoleDAO.insertUserRole(registeredUser, authority);
    }
    
    public List<ActionDefinition> selectUserActions(Integer registeredUserId, Integer applicationFormId, ApplicationFormStatus status) {
        return applicationFormUserRoleDAO.selectUserActions(registeredUserId, applicationFormId, status);
    }
    
    public void updateUrgentApplications() {
        applicationFormUserRoleDAO.updateUrgentApplications();
    }

    private void updateApplicationDueDate(ApplicationForm applicationForm, Date deadlineTimestamp) {
        applicationFormUserRoleDAO.updateApplicationDueDate(applicationForm, deadlineTimestamp);
    }
    
    private void updateApplicationInterest(ApplicationForm applicationForm, RegisteredUser registeredUser, Boolean interested) {
        applicationFormUserRoleDAO.updateApplicationInterest(applicationForm, registeredUser, interested);
    }
    
    private void deleteProvideInterviewAvailabilityAction(ApplicationForm applicationForm, RegisteredUser registeredUser) {
        Authority authority = Authority.INTERVIEWER;
        if (registeredUser.isApplicant(applicationForm)) {
            authority = Authority.APPLICANT;
        }
        deleteUserAction(applicationForm, registeredUser, authority, ApplicationFormAction.PROVIDE_INTERVIEW_AVAILABILITY);
    }

    private void updateApplicationDueDate(ApplicationForm applicationForm) {
        Date newDueDate = new Date();
        applicationForm.setDueDate(newDueDate);
        updateApplicationDueDate(applicationForm, newDueDate);
    }
    
    private ApplicationFormUserRole createApplicationFormUserRole(ApplicationForm applicationForm, RegisteredUser user, Authority authority,
            Boolean interestedInApplicant, ApplicationFormActionRequired... actions) {

        ApplicationFormUserRole applicationFormUserRole = applicationFormUserRoleDAO.findByApplicationFormAndUserAndAuthority(applicationForm, user, authority);
        Role role = roleDAO.getRoleByAuthority(authority);

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

        for (RegisteredUser superAdministrator : userDAO.getSuperadministrators()) {
            administrators.put(superAdministrator, Authority.SUPERADMINISTRATOR);
        }

        for (RegisteredUser administrator : applicationForm.getProgram().getAdministrators()) {
            administrators.put(administrator, Authority.ADMINISTRATOR);
        }

        Project project = applicationForm.getProject();
        if (project != null) {
        	administrators.put(project.getPrimarySupervisor(), Authority.PROJECTADMINISTRATOR);
        	
        	RegisteredUser projectAdministrator = project.getContactUser();
            if (projectAdministrator != null) {
                administrators.put(projectAdministrator, Authority.PROJECTADMINISTRATOR);
            }
        }

        Comment latestStateChangeComment = applicationsService.getLatestStateChangeComment(applicationForm, null);
        if (latestStateChangeComment != null) {
            RegisteredUser stateAdministrator = latestStateChangeComment.getDelegateAdministrator();
            if (stateAdministrator != null) {
                administrators.put(stateAdministrator, Authority.STATEADMINISTRATOR);
            }
        }

        for (Entry<RegisteredUser, Authority> administrator : administrators.entrySet()) {
            Boolean raisesUrgentFlag = dueDate.before(new Date());

            List<ApplicationFormActionRequired> requiredActions = new ArrayList<ApplicationFormActionRequired>();
            requiredActions.add(new ApplicationFormActionRequired(actionDAO.getActionById(action), dueDate, bindDeadlineToDueDate, raisesUrgentFlag));

            if (initiateStageMap.containsValue(action)) {
                requiredActions.add(new ApplicationFormActionRequired(actionDAO.getActionById(ApplicationFormAction.MOVE_TO_DIFFERENT_STAGE), dueDate,
                        bindDeadlineToDueDate, raisesUrgentFlag));
            }

            createApplicationFormUserRole(applicationForm, administrator.getKey(), administrator.getValue(), false,
                    requiredActions.toArray(new ApplicationFormActionRequired[0]));
        }
    }

}
