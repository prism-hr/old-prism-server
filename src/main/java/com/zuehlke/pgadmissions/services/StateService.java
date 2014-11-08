package com.zuehlke.pgadmissions.services;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.StateDAO;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismDuration;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.RoleTransition;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.domain.workflow.StateAction;
import com.zuehlke.pgadmissions.domain.workflow.StateActionAssignment;
import com.zuehlke.pgadmissions.domain.workflow.StateActionNotification;
import com.zuehlke.pgadmissions.domain.workflow.StateDurationConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.StateDurationDefinition;
import com.zuehlke.pgadmissions.domain.workflow.StateGroup;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.domain.workflow.StateTransitionEvaluation;
import com.zuehlke.pgadmissions.domain.workflow.StateTransitionPending;
import com.zuehlke.pgadmissions.dto.StateTransitionPendingDTO;
import com.zuehlke.pgadmissions.exceptions.CustomizationException;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.rest.representation.resource.ActionRepresentation;
import com.zuehlke.pgadmissions.utils.ReflectionUtils;

@Service
@Transactional
public class StateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StateService.class);

    @Autowired
    private StateDAO stateDAO;

    @Autowired
    private ActionService actionService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CustomizationService customizationService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private SystemService systemService;

    public State getById(PrismState id) {
        return entityService.getById(State.class, id);
    }

    public StateGroup getStateGroupById(PrismStateGroup stateGroupId) {
        return entityService.getById(StateGroup.class, stateGroupId);
    }

    public StateDurationDefinition getStateDurationDefinitionById(PrismDuration stateDurationDefinitionId) {
        return entityService.getById(StateDurationDefinition.class, stateDurationDefinitionId);
    }

    public StateTransitionEvaluation getStateTransitionEvaluationById(PrismStateTransitionEvaluation stateTransitionEvaluationId) {
        return entityService.getById(StateTransitionEvaluation.class, stateTransitionEvaluationId);
    }

    public List<State> getConfigurableStates() {
        return stateDAO.getConfigurableStates();
    }

    public List<State> getStates() {
        return entityService.list(State.class);
    }

    public List<StateGroup> getStateGroups() {
        return entityService.list(StateGroup.class);
    }

    public List<StateTransitionEvaluation> getStateTransitionEvaluations() {
        return entityService.list(StateTransitionEvaluation.class);
    }

    public StateDurationConfiguration getStateDurationConfiguration(Resource resource, User user, State state) {
        return customizationService.getConfiguration(StateDurationConfiguration.class, resource, user, "state", state);
    }

    public StateDurationConfiguration getStateDurationConfiguration(Resource resource, PrismLocale locale, PrismProgramType programType, State state) {
        return customizationService.getConfiguration(StateDurationConfiguration.class, resource, locale, programType, "state", state);
    }

    public void createOrUpdateStateDurationConfiguration(Resource resource, PrismLocale locale, PrismProgramType programType,
            StateDurationDefinition stateDurationDefinition, Integer duration) throws DeduplicationException, CustomizationException {
        customizationService.validateConfiguration(resource, stateDurationDefinition, locale, programType);
        StateDurationConfiguration transientStateDuration = new StateDurationConfiguration().withResource(resource).withLocale(locale)
                .withProgramType(programType).withStateDurationDefinition(stateDurationDefinition).withDuration(duration)
                .withSystemDefault(customizationService.isSystemDefault(stateDurationDefinition, locale, programType));
        entityService.createOrUpdate(transientStateDuration);
    }

    public void deleteStateActions() {
        entityService.deleteAll(RoleTransition.class);
        entityService.deleteAll(StateTransition.class);
        entityService.deleteAll(StateActionAssignment.class);
        entityService.deleteAll(StateActionNotification.class);
        entityService.deleteAll(StateAction.class);
    }

    public void deleteObsoleteStateDurations() {
        stateDAO.deleteObseleteStateDurations(getConfigurableStates());
    }

    public List<StateAction> getStateActions() {
        return entityService.list(StateAction.class);
    }

    public List<State> getOrderedTransitionStates(State state, State... excludedTransitionStates) {
        return stateDAO.getOrderedTransitionStates(state, excludedTransitionStates);
    }

    public List<StateTransitionPendingDTO> getStateTransitionsPending(PrismScope scopeId) {
        return stateDAO.getStateTransitionsPending(scopeId);
    }

    public StateTransition executeStateTransition(Resource resource, Action action, Comment comment) throws DeduplicationException {
        comment.setResource(resource);

        if (action.getActionCategory() == PrismActionCategory.CREATE_RESOURCE) {
            resourceService.persistResource(resource, action);
        }

        commentService.create(comment);
        resource.addComment(comment);

        entityService.flush();

        State state = resource.getState();
        StateTransition stateTransition = getStateTransition(resource, action, comment);

        if (stateTransition == null) {
            commentService.recordStateTransition(comment, state, state);
        } else {
            State transitionState = stateTransition.getTransitionState();
            state = state == null ? transitionState : state;

            resourceService.recordStateTransition(resource, state, transitionState);
            commentService.recordStateTransition(comment, state, transitionState);

            resourceService.processResource(resource, comment);
            commentService.processComment(comment);

            roleService.executeRoleTransitions(stateTransition, comment);

            entityService.flush();

            if (stateTransition.getPropagatedActions().size() > 0) {
                StateTransitionPending transientTransitionPending = new StateTransitionPending().withResource(resource).withStateTransition(stateTransition);
                entityService.getOrCreate(transientTransitionPending);
            }

            notificationService.sendWorkflowNotifications(resource, comment);
        }

        resourceService.postProcessResource(resource, comment);
        commentService.postProcessComment(comment);

        return stateTransition;
    }

    public StateTransition getStateTransition(Resource resource, Action action, Comment comment) {
        Resource operative = resourceService.getOperativeResource(resource, action);
        List<StateTransition> potentialStateTransitions = stateDAO.getStateTransitions(operative, action);

        if (potentialStateTransitions.size() > 1) {
            PrismStateTransitionEvaluation transitionEvaluation = potentialStateTransitions.get(0).getStateTransitionEvaluation().getId();
            return (StateTransition) ReflectionUtils.invokeMethod(this, transitionEvaluation.getMethodName(), operative, comment);
        }

        return potentialStateTransitions.isEmpty() ? null : potentialStateTransitions.get(0);
    }

    public StateTransition getApplicationStateCompletedOutcome(Resource resource, Comment comment) {
        PrismState transitionStateId = comment.getTransitionState().getId();
        return stateDAO.getStateTransition(resource.getState(), comment.getAction(), transitionStateId);
    }

    public StateTransition getApplicationReviewedOutcome(Resource resource, Comment comment) {
        PrismState transitionState = PrismState.APPLICATION_REVIEW_PENDING_FEEDBACK;
        if (roleService.getRoleUsers(resource, roleService.getById(PrismRole.APPLICATION_REVIEWER)).size() == 1) {
            transitionState = PrismState.APPLICATION_REVIEW_PENDING_COMPLETION;
        }
        return stateDAO.getStateTransition(resource.getState(), comment.getAction(), transitionState);
    }

    public StateTransition getApplicationInterviewRsvpedOutcome(Resource resource, Comment comment) {
        PrismState transitionStateId = PrismState.APPLICATION_INTERVIEW_PENDING_AVAILABILITY;
        List<User> interviewees = roleService.getRoleUsers(resource, roleService.getById(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE));
        List<User> interviewers = roleService.getRoleUsers(resource, roleService.getById(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER));
        if ((interviewees.size() + interviewers.size()) == 1) {
            transitionStateId = PrismState.APPLICATION_INTERVIEW_PENDING_SCHEDULING;
        }
        return stateDAO.getStateTransition(resource.getState(), comment.getAction(), transitionStateId);
    }

    public StateTransition getApplicationSupervisionConfirmedOutcome(Resource resource, Comment comment) {
        PrismState transitionStateId = PrismState.APPLICATION_APPROVAL_PENDING_FEEDBACK;
        List<User> primarySupervisors = roleService.getRoleUsers(resource, roleService.getById(PrismRole.APPLICATION_PRIMARY_SUPERVISOR));
        List<User> secondarySupervisors = roleService.getRoleUsers(resource, roleService.getById(PrismRole.APPLICATION_SECONDARY_SUPERVISOR));
        if ((primarySupervisors.size() + secondarySupervisors.size()) == 1) {
            transitionStateId = PrismState.APPLICATION_APPROVAL_PENDING_COMPLETION;
        }
        return stateDAO.getStateTransition(resource.getState(), comment.getAction(), transitionStateId);
    }

    public StateTransition getApplicationAssignedRecruiterOutcome(Resource resource, Comment comment) {
        PrismState transitionStateId;
        PrismState stateId = resource.getState().getId();
        CommentAssignedUser firstAssignee = comment.getAssignedUsers().iterator().next();
        if (Arrays.asList(PrismState.APPLICATION_REVIEW, PrismState.APPLICATION_INTERVIEW, PrismState.APPLICATION_APPROVAL).contains(stateId)
                && firstAssignee.getRole().getId() == PrismRole.APPLICATION_ADMINISTRATOR
                && firstAssignee.getRoleTransitionType() == PrismRoleTransitionType.CREATE) {
            transitionStateId = stateId;
        } else if (Arrays.asList(PrismState.APPLICATION_REVIEW, PrismState.APPLICATION_APPROVAL).contains(stateId)) {
            transitionStateId = PrismState.valueOf(stateId.name() + "_PENDING_FEEDBACK");
        } else {
            LocalDateTime interviewDateTime = comment.getInterviewDateTime();
            if (interviewDateTime != null) {
                DateTime interviewZonedDateTime = interviewDateTime.toDateTime(DateTimeZone.forTimeZone(comment.getInterviewTimeZone()));
                if (new DateTime().isAfter(interviewZonedDateTime)) {
                    transitionStateId = PrismState.APPLICATION_INTERVIEW_PENDING_FEEDBACK;
                } else {
                    transitionStateId = PrismState.APPLICATION_INTERVIEW_PENDING_INTERVIEW;
                }
            } else {
                if (resource.getState().getId() == PrismState.APPLICATION_INTERVIEW) {
                    transitionStateId = PrismState.APPLICATION_INTERVIEW_PENDING_AVAILABILITY;
                } else {
                    transitionStateId = PrismState.APPLICATION_INTERVIEW;
                }
            }
        }
        return stateDAO.getStateTransition(resource.getState(), comment.getAction(), transitionStateId);
    }

    public StateTransition getApplicationInterviewedOutcome(Resource resource, Comment comment) {
        PrismState transitionStateId = PrismState.APPLICATION_INTERVIEW_PENDING_FEEDBACK;
        if (roleService.getRoleUsers(resource, roleService.getById(PrismRole.APPLICATION_INTERVIEWER)).size() == 1) {
            transitionStateId = PrismState.APPLICATION_INTERVIEW_PENDING_COMPLETION;
        }
        return stateDAO.getStateTransition(resource.getState(), comment.getAction(), transitionStateId);
    }

    public StateTransition getInstitutionCreatedOutcome(Resource resource, Comment comment) {
        PrismState transitionStateId = PrismState.INSTITUTION_APPROVAL;
        if (roleService.hasUserRole(resource, comment.getUser(), PrismRole.SYSTEM_ADMINISTRATOR)) {
            transitionStateId = PrismState.INSTITUTION_APPROVED;
        }
        return stateDAO.getStateTransition(resource.getState(), comment.getAction(), transitionStateId);
    }

    public StateTransition getApplicationExportedOutcome(Resource resource, Comment comment) {
        State currentState = resource.getState();
        PrismState transitionStateId = currentState.getId();
        PrismStateGroup stateGroupId = currentState.getStateGroup().getId();
        if (comment.getExportException() == null) {
            transitionStateId = PrismState.valueOf(stateGroupId.name() + "_COMPLETED");
        } else {
            transitionStateId = PrismState.valueOf(stateGroupId.name() + "_PENDING_CORRECTION");
        }
        return stateDAO.getStateTransition(resource.getState(), comment.getAction(), transitionStateId);
    }

    public StateTransition getApplicationProcessedOutcome(Resource resource, Comment comment) {
        PrismState transitionStateId;
        PrismAction actionId = comment.getAction().getId();
        if (actionId == PrismAction.APPLICATION_TERMINATE || actionId == PrismAction.APPLICATION_WITHDRAW) {
            if (BooleanUtils.isTrue(resource.getInstitution().getUclInstitution())) {
                transitionStateId = PrismState.APPLICATION_REJECTED_PENDING_EXPORT;
            } else {
                transitionStateId = PrismState.APPLICATION_REJECTED_COMPLETED;
            }
        } else {
            PrismState stateId = resource.getState().getId();
            if (BooleanUtils.isTrue(resource.getInstitution().getUclInstitution())) {
                transitionStateId = PrismState.valueOf(stateId.name() + "_PENDING_EXPORT");
            } else {
                transitionStateId = PrismState.valueOf(stateId.name() + "_COMPLETED");
            }
        }
        return stateDAO.getStateTransition(resource.getState(), comment.getAction(), transitionStateId);
    }

    public StateTransition getProgramCreatedOutcome(Resource resource, Comment comment) {
        PrismState transitionStateId = PrismState.PROGRAM_APPROVAL;
        if (roleService.hasUserRole(resource, comment.getUser(), PrismRole.INSTITUTION_ADMINISTRATOR)) {
            transitionStateId = PrismState.PROGRAM_APPROVED;
        }
        return stateDAO.getStateTransition(resource.getState(), comment.getAction(), transitionStateId);
    }

    public StateTransition getProjectCreatedOutcome(Resource resource, Comment comment) {
        PrismState transitionStateId = PrismState.PROJECT_APPROVAL;
        if (roleService.hasAnyUserRole(resource, comment.getUser(), PrismRole.INSTITUTION_ADMINISTRATOR, PrismRole.PROGRAM_ADMINISTRATOR)) {
            transitionStateId = PrismState.PROJECT_APPROVED;
        }
        return stateDAO.getStateTransition(resource.getState(), comment.getAction(), transitionStateId);
    }

    public StateTransition getApplicationRecruitedOutcome(Resource resource, Comment comment) {
        Comment recruitedComment = commentService.getEarliestComment((ResourceParent) resource, Application.class,
                PrismAction.APPLICATION_CONFIRM_OFFER_RECOMMENDATION);
        return stateDAO.getStateTransition(resource.getState(), comment.getAction(), recruitedComment.getParentResourceTransitionState().getId());
    }

    public StateTransition getInstitutionApprovedOutcome(Resource resource, Comment comment) {
        return getUserDefinedNextState(resource, comment);
    }

    public StateTransition getProgramApprovedOutcome(Resource resource, Comment comment) {
        return getUserDefinedNextState(resource, comment);
    }

    public StateTransition getProjectApprovedOutcome(Resource resource, Comment comment) {
        return getUserDefinedNextState(resource, comment);
    }

    public StateTransition getProgramExpiredOutcome(Resource resource, Comment comment) {
        PrismState transitionStateId = BooleanUtils.isTrue(resource.getProgram().getImported()) ? PrismState.PROGRAM_DISABLED_PENDING_IMPORT_REACTIVATION
                : PrismState.PROGRAM_DISABLED_PENDING_REACTIVATION;
        return stateDAO.getStateTransition(resource.getState(), comment.getAction(), transitionStateId);
    }

    public StateTransition getInstitutionViewEditOutcome(Resource resource, Comment comment) {
        return getViewEditNextState(resource, comment);
    }

    public StateTransition getProgramViewEditOutcome(Resource resource, Comment comment) {
        return getViewEditNextState(resource, comment);
    }

    public StateTransition getProjectViewEditOutcome(Resource resource, Comment comment) {
        return getViewEditNextState(resource, comment);
    }

    private StateTransition getViewEditNextState(Resource resource, Comment comment) {
        if (comment.getTransitionState() == null) {
            PrismState transitionStateId = resource.getState().getId();
            return stateDAO.getStateTransition(resource.getState(), comment.getAction(), transitionStateId);
        }
        return getUserDefinedNextState(resource, comment);
    }

    private StateTransition getUserDefinedNextState(Resource resource, Comment comment) {
        if (comment.getTransitionState() == null) {
            return null;
        }
        PrismState transitionStateId = comment.getTransitionState().getId();
        return stateDAO.getStateTransition(resource.getState(), comment.getAction(), transitionStateId);
    }

    public <T extends Resource> void executeDeferredStateTransition(Class<T> resourceClass, Integer resourceId, PrismAction actionId)
            throws DeduplicationException {
        Resource resource = resourceService.getById(resourceClass, resourceId);
        Action action = actionService.getById(actionId);

        LOGGER.info("Calling " + action.getId() + " on " + resource.getCode());

        Comment comment = new Comment().withResource(resource).withUser(systemService.getSystem().getUser()).withAction(action).withDeclinedResponse(false)
                .withCreatedTimestamp(new DateTime());
        executeStateTransition(resource, action, comment);
    }

    public void deleteStateTransitionPending(Integer stateTransitionPendingId) {
        StateTransitionPending pending = entityService.getById(StateTransitionPending.class, stateTransitionPendingId);
        entityService.delete(pending);
    }

    public List<PrismState> getStatesByStateGroup(PrismStateGroup stateGroupId) {
        return stateDAO.getStatesByStateGroup(stateGroupId);
    }

    public List<PrismState> getActiveProgramStates() {
        return stateDAO.getActiveProgramStates();
    }

    public List<PrismState> getActiveProjectStates() {
        return stateDAO.getActiveProjectStates();
    }

    public List<PrismState> getAvailableNextStates(Resource resource, Set<ActionRepresentation> permittedActions) {
        return stateDAO.getAvailableNextStates(resource, permittedActions);
    }

}
