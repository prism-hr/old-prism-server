package com.zuehlke.pgadmissions.services;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.dao.ActionDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.AdmitterComment;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.AssignInterviewersComment;
import com.zuehlke.pgadmissions.domain.AssignReviewersComment;
import com.zuehlke.pgadmissions.domain.AssignSupervisorsComment;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.InterviewScheduleComment;
import com.zuehlke.pgadmissions.domain.InterviewVoteComment;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.SupervisionConfirmationComment;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.domain.enums.SystemAction;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

@Service
@Transactional
public class WorkflowService {

    private static final HashMap<PrismState, SystemAction> INITIATE_STAGE_MAP;
    static {
        INITIATE_STAGE_MAP = new HashMap<PrismState, SystemAction>(4);
        INITIATE_STAGE_MAP.put(PrismState.APPLICATION_REVIEW, SystemAction.APPLICATION_ASSIGN_REVIEWERS);
        INITIATE_STAGE_MAP.put(PrismState.APPLICATION_INTERVIEW, SystemAction.APPLICATION_ASSIGN_INTERVIEWERS);
        INITIATE_STAGE_MAP.put(PrismState.APPLICATION_APPROVAL, SystemAction.APPLICATION_ASSIGN_SUPERVISORS);
        INITIATE_STAGE_MAP.put(PrismState.APPLICATION_REJECTED, SystemAction.APPLICATION_CONFIRM_REJECTION);
    };

    @Autowired
    private RoleService roleService;

    @Autowired
    private ActionDAO actionDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private StateTransitionService stateTransitionService;

    @Autowired
    private EncryptionUtils encryptionUtils;
    
    @Autowired 
    private PermissionsService permissionsService;
    
    @Autowired
    private ApplicationService applicationFormService;

    public void applicationCreated(Application applicationForm) {
//        createApplicationFormUserRole(applicationForm, applicationForm.getApplicant(), Authority.APPLICANT, false,
//                new ApplicationFormActionRequired(actionDAO.getById(ApplicationFormAction.COMPLETE_APPLICATION), applicationForm.getDueDate(), true, false));
    }

    public void applicationSubmitted(Application applicationForm) {
//        assignToAdministrators(applicationForm, ApplicationFormAction.COMPLETE_VALIDATION_STAGE, applicationForm.getDueDate(), true);
//
//        for (RegisteredUser approver : applicationForm.getProgram().getApprovers()) {
//            createApplicationFormUserRole(applicationForm, approver, Authority.APPROVER, false);
//        }
//
//        for (SuggestedSupervisor suggestedSupervisor : applicationForm.getProgramDetails().getSuggestedSupervisors()) {
//            String supervisorEmail = suggestedSupervisor.getEmail();
//            RegisteredUser userToSaveAsSuggestedSupervisor = userDAO.getUserByEmailIncludingDisabledAccounts(supervisorEmail);
//
//            if (userToSaveAsSuggestedSupervisor == null) {
//                userToSaveAsSuggestedSupervisor = new RegisteredUser();
//                userToSaveAsSuggestedSupervisor.setFirstName(suggestedSupervisor.getFirstname());
//                userToSaveAsSuggestedSupervisor.setLastName(suggestedSupervisor.getLastname());
//                userToSaveAsSuggestedSupervisor.setUsername(suggestedSupervisor.getEmail());
//                userToSaveAsSuggestedSupervisor.setEmail(suggestedSupervisor.getEmail());
//                userToSaveAsSuggestedSupervisor.setAccountNonExpired(true);
//                userToSaveAsSuggestedSupervisor.setAccountNonLocked(true);
//                userToSaveAsSuggestedSupervisor.setEnabled(false);
//                userToSaveAsSuggestedSupervisor.setCredentialsNonExpired(true);
//                userToSaveAsSuggestedSupervisor.setActivationCode(encryptionUtils.generateUUID());
//                userToSaveAsSuggestedSupervisor.getRoles().add(roleDAO.getById(Authority.SUGGESTEDSUPERVISOR));
//                userDAO.save(userToSaveAsSuggestedSupervisor);
//            }
//
//            createApplicationFormUserRole(applicationForm, userToSaveAsSuggestedSupervisor, Authority.SUGGESTEDSUPERVISOR, true);
//        }
    }

    public void validationStageCompleted(Application application) {
//        deleteStateActions(application);
//        Boolean anyUnsure = application.getValidationComment().isAtLeastOneAnswerUnsure();
//        List<RegisteredUser> admitters = userDAO.getAdmitters();
//        List<ApplicationFormUserRole> superadministratorRoles = applicationFormUserRoleDAO.getByApplicationFormAndAuthorities(application,
//                Authority.SUPERADMINISTRATOR);
//
//        if (BooleanUtils.isTrue(anyUnsure)) {
//            for (RegisteredUser admitter : admitters) {
//                createApplicationFormUserRole(application, admitter, Authority.ADMITTER, false,
//                        new ApplicationFormActionRequired(actionDAO.getById(ApplicationFormAction.CONFIRM_ELIGIBILITY), new Date(), false, true));
//            }
//            for (ApplicationFormUserRole superadministratorRole : superadministratorRoles) {
//                superadministratorRole.getActions().add(
//                        new ApplicationFormActionRequired(actionDAO.getById(ApplicationFormAction.CONFIRM_ELIGIBILITY), new Date(), false, true));
//                superadministratorRole.setRaisesUrgentFlag(true);
//            }
//        } else {
//            for (RegisteredUser admitter : admitters) {
//                createApplicationFormUserRole(application, admitter, Authority.ADMITTER, false);
//            }
//        }

    }
    
    public void createRefereeRole(Referee referee) {
//        createApplicationFormUserRole(referee.getApplication(), referee.getUser(), Authority.REFEREE, false,
//                new ApplicationFormActionRequired(actionDAO.getById(ApplicationFormAction.PROVIDE_REFERENCE), new Date(), false, true));
    }

    public void stateChanged(Comment stateChangeComment) {
//        ApplicationForm application = stateChangeComment.getApplication();
//        deleteStateActions(application);
//
//        ApplicationFormStatus nextStatus = stateChangeComment.getNextStatus();
//        if (INITIATE_STAGE_MAP.containsKey(nextStatus)) {
//            assignToAdministrators(application, INITIATE_STAGE_MAP.get(nextStatus), new Date(), false);
//        }
//
//        List<RegisteredUser> approvers = application.getProgram().getApprovers();
//
//        if (nextStatus == ApplicationFormStatus.APPROVED) {
//            for (RegisteredUser approver : approvers) {
//                createApplicationFormUserRole(application, approver, Authority.APPROVER, false,
//                        new ApplicationFormActionRequired(actionDAO.getById(ApplicationFormAction.CONFIRM_OFFER_RECOMMENDATION), new Date(), false, true),
//                        new ApplicationFormActionRequired(actionDAO.getById(ApplicationFormAction.MOVE_TO_DIFFERENT_STAGE), new Date(), false, true));
//            }
//
//            for (RegisteredUser superAdministrator : userDAO.getSuperadministrators()) {
//                createApplicationFormUserRole(application, superAdministrator, Authority.SUPERADMINISTRATOR, false,
//                        new ApplicationFormActionRequired(actionDAO.getById(ApplicationFormAction.CONFIRM_OFFER_RECOMMENDATION), new Date(), false, true),
//                        new ApplicationFormActionRequired(actionDAO.getById(ApplicationFormAction.MOVE_TO_DIFFERENT_STAGE), new Date(), false, true));
//            }
//        }
//
//        else if (application.getStatus().getId() == ApplicationFormStatus.APPROVAL && nextStatus != null) {
//            for (RegisteredUser approver : approvers) {
//                createApplicationFormUserRole(application, approver, Authority.APPROVER, false,
//                        new ApplicationFormActionRequired(actionDAO.getById(INITIATE_STAGE_MAP.get(nextStatus)), new Date(), false, true),
//                        new ApplicationFormActionRequired(actionDAO.getById(ApplicationFormAction.MOVE_TO_DIFFERENT_STAGE), new Date(), false, true));
//            }
//        }
    }

    public void movedToReviewStage(AssignReviewersComment assignReviewersComment) {
//        ApplicationForm application = assignReviewersComment.getApplication();
//
//        deleteStateActions(application);
//
//        for (CommentAssignedUser assignedUser : assignReviewersComment.getAssignedUsers()) {
//            createApplicationFormUserRole(application, assignedUser.getUser(), Authority.REVIEWER, false,
//                    new ApplicationFormActionRequired(actionDAO.getActionById(ApplicationFormAction.PROVIDE_REVIEW), new Date(), false, true));
//        }
//
//        assignToAdministrators(application, ApplicationFormAction.COMPLETE_REVIEW_STAGE, application.getDueDate(), true);
    }

    public void movedToInterviewStage(AssignInterviewersComment assignInterviewersComment) {
//        ApplicationForm application = interview.getApplication();
//
//        deleteStateActions(application);
//
//        if (interview.isScheduling()) {
//            assignToAdministrators(application, ApplicationFormAction.CONFIRM_INTERVIEW_ARRANGEMENTS, application.getDueDate(), true);
//
//            for (InterviewParticipant participant : interview.getParticipants()) {
//                Boolean isApplicant = participant.getUser().getId().equals(application.getApplicant().getId());
//                Authority authority = isApplicant ? Authority.APPLICANT : Authority.INTERVIEWER;
//                createApplicationFormUserRole(application, participant.getUser(), authority, false,
//                        new ApplicationFormActionRequired(actionDAO.getById(ApplicationFormAction.PROVIDE_INTERVIEW_AVAILABILITY), new Date(), false, true));
//            }
//        } else {
//            for (Interviewer interviewer : interview.getInterviewers()) {
//                Boolean raisesUrgentFlag = interview.getInterviewDueDate().before(new Date());
//
//                createApplicationFormUserRole(application, interviewer.getUser(), Authority.INTERVIEWER, false,
//                        new ApplicationFormActionRequired(actionDAO.getById(ApplicationFormAction.PROVIDE_INTERVIEW_FEEDBACK), interview.getInterviewDueDate(),
//                                false, raisesUrgentFlag));
//            }
//        }
//        assignToAdministrators(application, ApplicationFormAction.COMPLETE_INTERVIEW_STAGE, application.getDueDate(), true);
    }

    public void movedToApprovalStage(AssignSupervisorsComment assignSupervisorsComment) {
//        ApplicationForm applicationForm = approvalRound.getApplication();
//        deleteStateActions(applicationForm);
//
//        Supervisor primarySupervisor = approvalRound.getPrimarySupervisor();
//        createApplicationFormUserRole(approvalRound.getApplication(), primarySupervisor.getUser(), Authority.SUPERVISOR, false,
//                new ApplicationFormActionRequired(actionDAO.getById(ApplicationFormAction.CONFIRM_PRIMARY_SUPERVISION), new Date(), false, true));
//
//        assignToAdministrators(applicationForm, ApplicationFormAction.COMPLETE_APPROVAL_STAGE, applicationForm.getDueDate(), true);
//
//        List<RegisteredUser> approvers = applicationForm.getProgram().getApprovers();
//        for (RegisteredUser approver : approvers) {
//            createApplicationFormUserRole(applicationForm, approver, Authority.APPROVER, false,
//                    new ApplicationFormActionRequired(actionDAO.getById(ApplicationFormAction.COMPLETE_APPROVAL_STAGE), applicationForm.getDueDate(), true,
//                            false));
//        }
    }

    public void admitterCommentPosted(AdmitterComment comment) {
        Application application = comment.getApplication();
//        deleteRoleAction(application, Authority.ADMITTER, ApplicationFormAction.CONFIRM_ELIGIBILITY);
//        deleteRoleAction(application, Authority.SUPERADMINISTRATOR, ApplicationFormAction.CONFIRM_ELIGIBILITY);
    }

    public void referencePosted(ReferenceComment comment) {
//        deleteUserAction(referee.getApplication(), referee.getUser(), Authority.REFEREE, ApplicationFormAction.PROVIDE_REFERENCE);
    }

    public void reviewPosted(ReviewComment reviewComment) {
//        ReviewRound reviewRound = reviewer.getReviewRound();
//        ApplicationForm application = reviewRound.getApplication();
//        RegisteredUser user = reviewer.getUser();
//        ReviewComment review = reviewer.getReview();
//
//        deleteUserAction(application, user, Authority.REVIEWER, ApplicationFormAction.PROVIDE_REVIEW);
//        updateApplicationInterest(application, user, review.getWillingToInterview() || review.getWillingToWorkWithApplicant());
//
//        if (reviewRound.hasAllReviewersResponded()) {
//            updateApplicationDueDate(application);
//        }
    }

    public void interviewParticipantResponded(InterviewVoteComment interviewVoteComment) {
//        Interview interview = participant.getInterview();
//        ApplicationForm application = interview.getApplication();
//        RegisteredUser user = participant.getUser();
//
//        deleteProvideInterviewAvailabilityAction(application, user);
//
//        if (interview.hasAllParticipantsProvidedAvailability()) {
//            updateApplicationDueDate(application);
//        }
    }

    public void interviewConfirmed(InterviewScheduleComment interviewScheduleComment) {
//        ApplicationForm application = interview.getApplication();
//
//        deleteStateActions(application);
//
//        for (InterviewParticipant participant : interview.getParticipants()) {
//            RegisteredUser user = participant.getUser();
//            Boolean isApplicant = user.isApplicant(application, user);
//
//            deleteProvideInterviewAvailabilityAction(application, user);
//
//            if (!isApplicant) {
//                Date dateNow = new Date();
//                ApplicationFormUserRole role = applicationFormUserRoleDAO.getByApplicationFormAndUserAndAuthorities(application, user, Authority.INTERVIEWER)
//                        .get(0);
//                role.getActions().add(
//                        new ApplicationFormActionRequired(actionDAO.getById(ApplicationFormAction.PROVIDE_INTERVIEW_FEEDBACK), dateNow, false, interview
//                                .getInterviewDueDate().before(dateNow)));
//            }
//
//        }
//
//        assignToAdministrators(application, ApplicationFormAction.COMPLETE_INTERVIEW_STAGE, application.getDueDate(), true);
    }

    public void interviewFeedbackPosted(InterviewComment interviewComment) {
//        Interview interview = interviewer.getInterview();
//        ApplicationForm application = interview.getApplication();
//        RegisteredUser user = interviewer.getUser();
//        InterviewComment interviewComment = interviewer.getInterviewComment();
//
//        deleteUserAction(application, user, Authority.INTERVIEWER, ApplicationFormAction.PROVIDE_INTERVIEW_FEEDBACK);
//        updateApplicationInterest(application, user, interviewComment.getWillingToSupervise());
//
//        if (interview.hasAllInterviewersProvidedFeedback()) {
//            updateApplicationDueDate(application);
//        }
    }

    public void supervisionConfirmed(SupervisionConfirmationComment supervisionConfirmationComment) {
//        ApprovalRound approval = supervisor.getApprovalRound();
//        ApplicationForm application = approval.getApplication();
//        RegisteredUser user = supervisor.getUser();
//
//        deleteUserAction(application, user, Authority.SUPERVISOR, ApplicationFormAction.CONFIRM_PRIMARY_SUPERVISION);
//        updateApplicationInterest(application, user, supervisor.getConfirmedSupervision());
//
//        updateApplicationDueDate(application);
    }

    public void applicationExportFailed(Application application) {
//        for (RegisteredUser user : userDAO.getAdmitters()) {
//            createApplicationFormUserRole(application, user, Authority.SUPERADMINISTRATOR, false,
//                    new ApplicationFormActionRequired(actionDAO.getById(ApplicationFormAction.CORRECT_APPLICATION), new Date(), false, true));
//        }
//        for (RegisteredUser user : userDAO.getSuperadministrators()) {
//            createApplicationFormUserRole(application, user, Authority.SUPERADMINISTRATOR, false,
//                    new ApplicationFormActionRequired(actionDAO.getById(ApplicationFormAction.CORRECT_APPLICATION), new Date(), false, true));
//        }
    }

    public void applicationExportResent(Application application) {
//        deleteRoleAction(application, Authority.ADMITTER, ApplicationFormAction.CORRECT_APPLICATION);
//        deleteRoleAction(application, Authority.SUPERADMINISTRATOR, ApplicationFormAction.CORRECT_APPLICATION);
    }
    
    public Boolean getRaisesUrgentFlagByUserAndApplicationForm(User user, Application applicationForm) {
//        return applicationFormUserRoleDAO.getRaisesUrgentFlagByUserAndApplicationForm(user, applicationForm);
        return false;
    }

    public List<User> getUsersInterestedInApplication(Application applicationForm) {
        return userDAO.getUsersInterestedInApplication(applicationForm);
    }

    public List<User> getUsersPotentiallyInterestedInApplication(Application applicationForm) {
        return userDAO.getUsersPotentiallyInterestedInApplication(applicationForm);
    }
    
    public void deleteApplicationUpdate(Application applicationForm, User user) {
//        applicationFormUserRoleDAO.deleteApplicationUpdate(applicationForm, registeredUser);
    }
    
    public void deleteApplicationRole(Application application, User user, Authority authority) {
//        applicationFormUserRoleDAO.deleteApplicationRole(application, user, authority);
    }
    
    public void deleteProgramRole(User user, Program program, Authority authority) {
//        applicationFormUserRoleDAO.deleteProgramRole(registeredUser, program, authority);
    }

    public void deleteUserRole(User user, Authority authority) {
//        applicationFormUserRoleDAO.deleteUserRole(registeredUser, authority);
    }

    public void insertProgramRole(User user, Program program, Authority authority) {
//        applicationFormUserRoleDAO.insertProgramRole(registeredUser, program, authority);
    }

    public void insertUserRole(User user, Authority authority) {
//        applicationFormUserRoleDAO.insertUserRole(registeredUser, authority);
    }

    public void updateUrgentApplications() {
//        applicationFormUserRoleDAO.updateUrgentApplications();
    }

    private void updateApplicationDueDate(Application applicationForm, LocalDate dueDate) {
//        applicationFormUserRoleDAO.updateApplicationDueDate(applicationForm, deadlineTimestamp);
    }

    private void updateApplicationInterest(Application applicationForm, User user, Boolean interested) {
//        applicationFormUserRoleDAO.updateApplicationInterest(applicationForm, registeredUser, interested);
    }

    private void deleteProvideInterviewAvailabilityAction(Application applicationForm, User user) {
//        Authority authority = Authority.INTERVIEWER;
//        if (registeredUser.isApplicant(applicationForm)) {
//            authority = Authority.APPLICANT;
//        }
//        actionDAO.deleteUserAction(applicationForm, registeredUser, authority, ApplicationFormAction.PROVIDE_INTERVIEW_AVAILABILITY);
    }

    private void updateApplicationDueDate(Application applicationForm) {
        LocalDate newDueDate = new LocalDate();
        applicationForm.setDueDate(newDueDate);
        updateApplicationDueDate(applicationForm, newDueDate);
    }

    private void assignToAdministrators(Application applicationForm, SystemAction action, Date dueDate, Boolean bindDeadlineToDueDate) {
        Map<User, Authority> administrators = Maps.newHashMap();

        for (User superAdministrator : userDAO.getSuperadministrators()) {
            administrators.put(superAdministrator, Authority.SYSTEM_ADMINISTRATOR);
        }

//        for (RegisteredUser administrator : applicationForm.getProgram().getAdministrators()) {
//            administrators.put(administrator, Authority.ADMINISTRATOR);
//        }

        Project project = applicationForm.getProject();
        if (project != null) {
            User primarySupervisor = roleService.getUserInRole(project, Authority.PROJECT_PRIMARY_SUPERVISOR);
            administrators.put(primarySupervisor, Authority.PROJECT_ADMINISTRATOR);

            User projectAdministrator = project.getUser();
            if (projectAdministrator != null) {
                administrators.put(projectAdministrator, Authority.PROJECT_ADMINISTRATOR);
            }
        }

        Comment latestStateChangeComment = applicationFormService.getLatestStateChangeComment(applicationForm, null);
        if (latestStateChangeComment != null) {
            User stateAdministrator = latestStateChangeComment.getDelegateAdministrator();
            if (stateAdministrator != null) {
                administrators.put(stateAdministrator, Authority.APPLICATION_ADMINISTRATOR);
            }
        }

        for (Entry<User, Authority> administrator : administrators.entrySet()) {
            Boolean raisesUrgentFlag = dueDate.before(new Date());

//            List<ActionRequired> requiredActions = new ArrayList<ActionRequired>();
//            requiredActions.add(new ApplicationFormActionRequired(actionDAO.getById(action), dueDate, bindDeadlineToDueDate, raisesUrgentFlag));
//
//            if (INITIATE_STAGE_MAP.containsValue(action)) {
//                requiredActions.add(new ApplicationFormActionRequired(actionDAO.getById(ApplicationFormAction.MOVE_TO_DIFFERENT_STAGE), dueDate,
//                        bindDeadlineToDueDate, raisesUrgentFlag));
//            }
//
//            createApplicationFormUserRole(applicationForm, administrator.getKey(), administrator.getValue(), false,
//                    requiredActions.toArray(new ApplicationFormActionRequired[0]));
        }
    }

    public void applicationUpdated(Application applicationForm, User user) {
        // TODO Auto-generated method stub
        
    }

}
