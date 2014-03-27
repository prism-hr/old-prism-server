package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import com.zuehlke.pgadmissions.domain.enums.ActionType;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.ActionDefinition;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

@Service
@Transactional
public class ApplicationFormUserRoleService {

    private static final HashMap<ApplicationFormStatus, ApplicationFormAction> INITIATE_STAGE_MAP;
    static {
        INITIATE_STAGE_MAP = new HashMap<ApplicationFormStatus, ApplicationFormAction>(4);
        INITIATE_STAGE_MAP.put(ApplicationFormStatus.REVIEW, ApplicationFormAction.ASSIGN_REVIEWERS);
        INITIATE_STAGE_MAP.put(ApplicationFormStatus.INTERVIEW, ApplicationFormAction.ASSIGN_INTERVIEWERS);
        INITIATE_STAGE_MAP.put(ApplicationFormStatus.APPROVAL, ApplicationFormAction.ASSIGN_SUPERVISORS);
        INITIATE_STAGE_MAP.put(ApplicationFormStatus.REJECTED, ApplicationFormAction.CONFIRM_REJECTION);
    };

    @Autowired
    private ApplicationFormUserRoleDAO applicationFormUserRoleDAO;

    @Autowired
    private ActionDAO actionDAO;

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private StateTransitionService stateTransitionService;

    @Autowired
    private EncryptionUtils encryptionUtils;
    
    @Autowired 
    private PermissionsService permissionsService;

    public void applicationCreated(ApplicationForm applicationForm) {
        createApplicationFormUserRole(applicationForm, applicationForm.getApplicant(), Authority.APPLICANT, false,
                new ApplicationFormActionRequired(actionDAO.getById(ApplicationFormAction.COMPLETE_APPLICATION), applicationForm.getDueDate(), true, false));
    }

    public void applicationSubmitted(ApplicationForm applicationForm) {
        assignToAdministrators(applicationForm, ApplicationFormAction.COMPLETE_VALIDATION_STAGE, applicationForm.getDueDate(), true);

        for (RegisteredUser approver : applicationForm.getProgram().getApprovers()) {
            createApplicationFormUserRole(applicationForm, approver, Authority.APPROVER, false);
        }

        for (SuggestedSupervisor suggestedSupervisor : applicationForm.getProgramDetails().getSuggestedSupervisors()) {
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
                userToSaveAsSuggestedSupervisor.getRoles().add(roleDAO.getById(Authority.SUGGESTEDSUPERVISOR));
                userDAO.save(userToSaveAsSuggestedSupervisor);
            }

            createApplicationFormUserRole(applicationForm, userToSaveAsSuggestedSupervisor, Authority.SUGGESTEDSUPERVISOR, true);
        }
    }

    public void validationStageCompleted(ApplicationForm application) {
        deleteStateActions(application);
        Boolean anyUnsure = application.getValidationComment().isAtLeastOneAnswerUnsure();
        List<RegisteredUser> admitters = userDAO.getAdmitters();
        List<ApplicationFormUserRole> superadministratorRoles = applicationFormUserRoleDAO.getByApplicationFormAndAuthorities(application,
                Authority.SUPERADMINISTRATOR);

        if (BooleanUtils.isTrue(anyUnsure)) {
            for (RegisteredUser admitter : admitters) {
                createApplicationFormUserRole(application, admitter, Authority.ADMITTER, false,
                        new ApplicationFormActionRequired(actionDAO.getById(ApplicationFormAction.CONFIRM_ELIGIBILITY), new Date(), false, true));
            }
            for (ApplicationFormUserRole superadministratorRole : superadministratorRoles) {
                superadministratorRole.getActions().add(
                        new ApplicationFormActionRequired(actionDAO.getById(ApplicationFormAction.CONFIRM_ELIGIBILITY), new Date(), false, true));
                superadministratorRole.setRaisesUrgentFlag(true);
            }
        } else {
            for (RegisteredUser admitter : admitters) {
                createApplicationFormUserRole(application, admitter, Authority.ADMITTER, false);
            }
        }

    }
    
    public void createRefereeRole(Referee referee) {
        createApplicationFormUserRole(referee.getApplication(), referee.getUser(), Authority.REFEREE, false,
                new ApplicationFormActionRequired(actionDAO.getById(ApplicationFormAction.PROVIDE_REFERENCE), new Date(), false, true));
    }

    public void stateChanged(StateChangeComment stateChangeComment) {
        ApplicationForm application = stateChangeComment.getApplication();
        deleteStateActions(application);

        ApplicationFormStatus nextStatus = stateChangeComment.getNextStatus();
        if (INITIATE_STAGE_MAP.containsKey(nextStatus)) {
            assignToAdministrators(application, INITIATE_STAGE_MAP.get(nextStatus), new Date(), false);
        }

        List<RegisteredUser> approvers = application.getProgram().getApprovers();

        if (nextStatus == ApplicationFormStatus.APPROVED) {
            for (RegisteredUser approver : approvers) {
                createApplicationFormUserRole(application, approver, Authority.APPROVER, false,
                        new ApplicationFormActionRequired(actionDAO.getById(ApplicationFormAction.CONFIRM_OFFER_RECOMMENDATION), new Date(), false, true),
                        new ApplicationFormActionRequired(actionDAO.getById(ApplicationFormAction.MOVE_TO_DIFFERENT_STAGE), new Date(), false, true));
            }

            for (RegisteredUser superAdministrator : userDAO.getSuperadministrators()) {
                createApplicationFormUserRole(application, superAdministrator, Authority.SUPERADMINISTRATOR, false,
                        new ApplicationFormActionRequired(actionDAO.getById(ApplicationFormAction.CONFIRM_OFFER_RECOMMENDATION), new Date(), false, true),
                        new ApplicationFormActionRequired(actionDAO.getById(ApplicationFormAction.MOVE_TO_DIFFERENT_STAGE), new Date(), false, true));
            }
        }

        else if (application.getStatus().getId() == ApplicationFormStatus.APPROVAL && nextStatus != null) {
            for (RegisteredUser approver : approvers) {
                createApplicationFormUserRole(application, approver, Authority.APPROVER, false,
                        new ApplicationFormActionRequired(actionDAO.getById(INITIATE_STAGE_MAP.get(nextStatus)), new Date(), false, true),
                        new ApplicationFormActionRequired(actionDAO.getById(ApplicationFormAction.MOVE_TO_DIFFERENT_STAGE), new Date(), false, true));
            }
        }
    }

    public void movedToReviewStage(ReviewRound reviewRound) {
        ApplicationForm application = reviewRound.getApplication();

        deleteStateActions(application);

        for (Reviewer reviewer : reviewRound.getReviewers()) {
            createApplicationFormUserRole(reviewRound.getApplication(), reviewer.getUser(), Authority.REVIEWER, false, new ApplicationFormActionRequired(
                    actionDAO.getById(ApplicationFormAction.PROVIDE_REVIEW), new Date(), false, true));
        }

        assignToAdministrators(application, ApplicationFormAction.COMPLETE_REVIEW_STAGE, application.getDueDate(), true);
    }

    public void movedToInterviewStage(Interview interview) {
        ApplicationForm application = interview.getApplication();

        deleteStateActions(application);

        if (interview.isScheduling()) {
            assignToAdministrators(application, ApplicationFormAction.CONFIRM_INTERVIEW_ARRANGEMENTS, application.getDueDate(), true);

            for (InterviewParticipant participant : interview.getParticipants()) {
                Boolean isApplicant = participant.getUser().getId().equals(application.getApplicant().getId());
                Authority authority = isApplicant ? Authority.APPLICANT : Authority.INTERVIEWER;
                createApplicationFormUserRole(application, participant.getUser(), authority, false,
                        new ApplicationFormActionRequired(actionDAO.getById(ApplicationFormAction.PROVIDE_INTERVIEW_AVAILABILITY), new Date(), false, true));
            }
        } else {
            for (Interviewer interviewer : interview.getInterviewers()) {
                Boolean raisesUrgentFlag = interview.getInterviewDueDate().before(new Date());

                createApplicationFormUserRole(application, interviewer.getUser(), Authority.INTERVIEWER, false,
                        new ApplicationFormActionRequired(actionDAO.getById(ApplicationFormAction.PROVIDE_INTERVIEW_FEEDBACK), interview.getInterviewDueDate(),
                                false, raisesUrgentFlag));
            }
        }
        assignToAdministrators(application, ApplicationFormAction.COMPLETE_INTERVIEW_STAGE, application.getDueDate(), true);
    }

    public void movedToApprovalStage(ApprovalRound approvalRound) {
        ApplicationForm applicationForm = approvalRound.getApplication();
        deleteStateActions(applicationForm);

        Supervisor primarySupervisor = approvalRound.getPrimarySupervisor();
        createApplicationFormUserRole(approvalRound.getApplication(), primarySupervisor.getUser(), Authority.SUPERVISOR, false,
                new ApplicationFormActionRequired(actionDAO.getById(ApplicationFormAction.CONFIRM_PRIMARY_SUPERVISION), new Date(), false, true));

        assignToAdministrators(applicationForm, ApplicationFormAction.COMPLETE_APPROVAL_STAGE, applicationForm.getDueDate(), true);

        List<RegisteredUser> approvers = applicationForm.getProgram().getApprovers();
        for (RegisteredUser approver : approvers) {
            createApplicationFormUserRole(applicationForm, approver, Authority.APPROVER, false,
                    new ApplicationFormActionRequired(actionDAO.getById(ApplicationFormAction.COMPLETE_APPROVAL_STAGE), applicationForm.getDueDate(), true,
                            false));
        }
    }

    public void admitterCommentPosted(AdmitterComment comment) {
        ApplicationForm application = comment.getApplication();
        deleteRoleAction(application, Authority.ADMITTER, ApplicationFormAction.CONFIRM_ELIGIBILITY);
        deleteRoleAction(application, Authority.SUPERADMINISTRATOR, ApplicationFormAction.CONFIRM_ELIGIBILITY);
    }

    public void referencePosted(Referee referee) {
        deleteUserAction(referee.getApplication(), referee.getUser(), Authority.REFEREE, ApplicationFormAction.PROVIDE_REFERENCE);
    }

    public void reviewPosted(Reviewer reviewer) {
        ReviewRound reviewRound = reviewer.getReviewRound();
        ApplicationForm application = reviewRound.getApplication();
        RegisteredUser user = reviewer.getUser();
        ReviewComment review = reviewer.getReview();

        deleteUserAction(application, user, Authority.REVIEWER, ApplicationFormAction.PROVIDE_REVIEW);
        updateApplicationInterest(application, user, review.getWillingToInterview() || review.getWillingToWorkWithApplicant());

        if (reviewRound.hasAllReviewersResponded()) {
            updateApplicationDueDate(application);
        }

    }

    public void interviewParticipantResponded(InterviewParticipant participant) {
        Interview interview = participant.getInterview();
        ApplicationForm application = interview.getApplication();
        RegisteredUser user = participant.getUser();

        deleteProvideInterviewAvailabilityAction(application, user);

        if (interview.hasAllParticipantsProvidedAvailability()) {
            updateApplicationDueDate(application);
        }

    }

    public void interviewConfirmed(Interview interview) {
        ApplicationForm application = interview.getApplication();

        deleteStateActions(application);

        for (InterviewParticipant participant : interview.getParticipants()) {
            RegisteredUser user = participant.getUser();
            Boolean isApplicant = user.isApplicant(application, user);

            deleteProvideInterviewAvailabilityAction(application, user);

            if (!isApplicant) {
                Date dateNow = new Date();
                ApplicationFormUserRole role = applicationFormUserRoleDAO.getByApplicationFormAndUserAndAuthorities(application, user, Authority.INTERVIEWER)
                        .get(0);
                role.getActions().add(
                        new ApplicationFormActionRequired(actionDAO.getById(ApplicationFormAction.PROVIDE_INTERVIEW_FEEDBACK), dateNow, false, interview
                                .getInterviewDueDate().before(dateNow)));
            }

        }

        assignToAdministrators(application, ApplicationFormAction.COMPLETE_INTERVIEW_STAGE, application.getDueDate(), true);
    }

    public void interviewFeedbackPosted(Interviewer interviewer) {
        Interview interview = interviewer.getInterview();
        ApplicationForm application = interview.getApplication();
        RegisteredUser user = interviewer.getUser();
        InterviewComment interviewComment = interviewer.getInterviewComment();

        deleteUserAction(application, user, Authority.INTERVIEWER, ApplicationFormAction.PROVIDE_INTERVIEW_FEEDBACK);
        updateApplicationInterest(application, user, interviewComment.getWillingToSupervise());

        if (interview.hasAllInterviewersProvidedFeedback()) {
            updateApplicationDueDate(application);
        }

    }

    public void supervisionConfirmed(Supervisor supervisor) {
        ApprovalRound approval = supervisor.getApprovalRound();
        ApplicationForm application = approval.getApplication();
        RegisteredUser user = supervisor.getUser();

        deleteUserAction(application, user, Authority.SUPERVISOR, ApplicationFormAction.CONFIRM_PRIMARY_SUPERVISION);
        updateApplicationInterest(application, user, supervisor.getConfirmedSupervision());

        updateApplicationDueDate(application);

    }

    public void applicationExportFailed(ApplicationForm application) {
        for (RegisteredUser user : userDAO.getAdmitters()) {
            createApplicationFormUserRole(application, user, Authority.SUPERADMINISTRATOR, false,
                    new ApplicationFormActionRequired(actionDAO.getById(ApplicationFormAction.CORRECT_APPLICATION), new Date(), false, true));
        }
        for (RegisteredUser user : userDAO.getSuperadministrators()) {
            createApplicationFormUserRole(application, user, Authority.SUPERADMINISTRATOR, false,
                    new ApplicationFormActionRequired(actionDAO.getById(ApplicationFormAction.CORRECT_APPLICATION), new Date(), false, true));
        }
    }

    public void applicationExportResent(ApplicationForm application) {
        deleteRoleAction(application, Authority.ADMITTER, ApplicationFormAction.CORRECT_APPLICATION);
        deleteRoleAction(application, Authority.SUPERADMINISTRATOR, ApplicationFormAction.CORRECT_APPLICATION);
    }

    public List<RegisteredUser> getUsersInterestedInApplication(ApplicationForm applicationForm) {
        return userDAO.getUsersInterestedInApplication(applicationForm);
    }

    public List<RegisteredUser> getUsersPotentiallyInterestedInApplication(ApplicationForm applicationForm) {
        return userDAO.getUsersPotentiallyInterestedInApplication(applicationForm);
    }

    public void deleteApplicationActions(ApplicationForm applicationForm) {
        applicationFormUserRoleDAO.deleteApplicationActions(applicationForm);
    }

    public void deleteApplicationUpdate(ApplicationForm applicationForm, RegisteredUser registeredUser) {
        applicationFormUserRoleDAO.deleteApplicationUpdate(applicationForm, registeredUser);
    }
    
    public void deleteApplicationRole(ApplicationForm application, RegisteredUser user, Authority authority) {
        applicationFormUserRoleDAO.deleteApplicationRole(application, user, authority);
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
        applicationFormUserRoleDAO.deleteUserAction(applicationForm, registeredUser, authority, action);
    }

    public void deleteUserRole(RegisteredUser registeredUser, Authority authority) {
        applicationFormUserRoleDAO.deleteUserRole(registeredUser, authority);
    }

    public void insertApplicationUpdate(ApplicationForm applicationForm, RegisteredUser author, ApplicationUpdateScope updateVisibility) {
        Date updateTimestamp = new Date();
        applicationFormUserRoleDAO.insertApplicationUpdate(applicationForm, author, updateTimestamp, updateVisibility);
        applicationForm.setLastUpdated(updateTimestamp);
    }

    public void insertProgramRole(RegisteredUser registeredUser, Program program, Authority authority) {
        applicationFormUserRoleDAO.insertProgramRole(registeredUser, program, authority);
    }

    public void insertUserRole(RegisteredUser registeredUser, Authority authority) {
        applicationFormUserRoleDAO.insertUserRole(registeredUser, authority);
    }

    public List<ActionDefinition> selectUserActions(Integer registeredUserId, Integer applicationFormId) {
        return applicationFormUserRoleDAO.selectUserActions(registeredUserId, applicationFormId);
    }

    public List<ActionDefinition> selectUserActionById(Integer registeredUserId, Integer applicationFormId, ApplicationFormAction action) {
        return applicationFormUserRoleDAO.selectUserActionById(registeredUserId, applicationFormId, action);
    }

    public List<ActionDefinition> selectUserActionByActionType(Integer registeredUserId, Integer applicationFormId, ActionType actionType) {
        return applicationFormUserRoleDAO.selectUserActionByActionType(registeredUserId, applicationFormId, actionType);
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
            requiredActions.add(new ApplicationFormActionRequired(actionDAO.getById(action), dueDate, bindDeadlineToDueDate, raisesUrgentFlag));

            if (INITIATE_STAGE_MAP.containsValue(action)) {
                requiredActions.add(new ApplicationFormActionRequired(actionDAO.getById(ApplicationFormAction.MOVE_TO_DIFFERENT_STAGE), dueDate,
                        bindDeadlineToDueDate, raisesUrgentFlag));
            }

            createApplicationFormUserRole(applicationForm, administrator.getKey(), administrator.getValue(), false,
                    requiredActions.toArray(new ApplicationFormActionRequired[0]));
        }
    }

}
