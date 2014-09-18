package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.StateDAO;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.ParentResource;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.RoleTransition;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.StateActionAssignment;
import com.zuehlke.pgadmissions.domain.StateActionNotification;
import com.zuehlke.pgadmissions.domain.StateDuration;
import com.zuehlke.pgadmissions.domain.StateGroup;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.StateTransitionEvaluation;
import com.zuehlke.pgadmissions.domain.StateTransitionPending;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismTransitionEvaluation;
import com.zuehlke.pgadmissions.dto.StateTransitionPendingDTO;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ActionRepresentation;
import com.zuehlke.pgadmissions.utils.IntrospectionUtils;

@Service
@Transactional
public class StateService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private StateDAO stateDAO;

    @Autowired
    private ActionService actionService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ScopeService scopeService;

    @Autowired
    private SystemService systemService;

    public State getById(PrismState id) {
        return entityService.getById(State.class, id);
    }

    public StateGroup getStateGroupById(PrismStateGroup stateGroupId) {
        return entityService.getById(StateGroup.class, stateGroupId);
    }

    public StateTransitionEvaluation getStateTransitionEvaluationById(PrismTransitionEvaluation stateTransitionEvaluationId) {
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

    public List<State> getWorkflowStates() {
        return stateDAO.getWorkflowStates();
    }

    public StateDuration getStateDuration(Resource resource) {
        return stateDAO.getStateDuration(resource, resource.getState());
    }

    public StateDuration getStateDuration(Resource resource, State state) {
        return stateDAO.getStateDuration(resource, state);
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

    public <T extends Resource> List<State> getDeprecatedStates(Class<T> resourceClass) {
        return stateDAO.getDeprecatedStates(resourceClass);
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

        commentService.save(comment);
        resource.addComment(comment);

        StateTransition stateTransition = getStateTransition(resource, action, comment);

        if (stateTransition != null) {
            State transitionState = stateTransition.getTransitionState();
            State state = resource.getState();
            state = state == null ? transitionState : state;

            resourceService.recordStateTransition(resource, state, transitionState);
            commentService.recordStateTransition(comment, state, transitionState);

            resourceService.processResource(resource, comment);
            roleService.executeRoleTransitions(stateTransition, comment);

            if (stateTransition.getPropagatedActions().size() > 0) {
                StateTransitionPending transientTransitionPending = new StateTransitionPending().withResource(resource).withStateTransition(stateTransition);
                entityService.getOrCreate(transientTransitionPending);
            }

            notificationService.sendWorkflowNotifications(resource, comment);
        } else {
            commentService.recordStateTransition(comment, resource.getState(), resource.getState());
        }

        resourceService.updateResource(resource, comment);
        return stateTransition;
    }

    public StateTransition getStateTransition(Resource resource, Action action, Comment comment) {
        Resource operative = resourceService.getOperativeResource(resource, action);
        List<StateTransition> potentialStateTransitions = stateDAO.getStateTransitions(operative, action);

        if (potentialStateTransitions.size() > 1) {
            PrismTransitionEvaluation transitionEvaluation = potentialStateTransitions.get(0).getStateTransitionEvaluation().getId();
            return (StateTransition) IntrospectionUtils.invokeMethod(this, transitionEvaluation.getMethodName(), operative, comment);
        }

        return potentialStateTransitions.isEmpty() ? null : potentialStateTransitions.get(0);
    }

    public List<PrismState> getAvailableNextStates(Resource resource, List<ActionRepresentation> permittedActions) {
        return stateDAO.getAvailableNextStates(resource, permittedActions);
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

    public StateTransition getApplicationInterviewScheduledOutcome(Resource resource, Comment comment) {
        PrismState transitionStateId;
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

    public StateTransition getApplicationEligibilityAssessedOutcome(Resource resource, Comment comment) {
        PrismState transitionStateId = PrismState.APPLICATION_VALIDATION_PENDING_COMPLETION;
        if (comment.isApplicationCreatorEligibilityUncertain()) {
            transitionStateId = PrismState.APPLICATION_VALIDATION_PENDING_FEEDBACK;
        }
        return stateDAO.getStateTransition(resource.getState(), comment.getAction(), transitionStateId);
    }

    // FIXME: completed the integration with the exporter
    public StateTransition getApplicationExportedOutcome(Resource resource, Comment comment) {
        State currentState = resource.getState();
        PrismState transitionStateId = currentState.getId();
        StateGroup stateGroup = currentState.getStateGroup();
        if (comment.getExportError() != null) {
            transitionStateId = PrismState.valueOf(stateGroup.toString() + "_PENDING_CORRECTION");
        } else if (comment.getExportResponse() != null && comment.getExportError() == null) {
            transitionStateId = PrismState.valueOf(stateGroup.toString() + "_COMPLETED");
        }
        return stateDAO.getStateTransition(resource.getState(), comment.getAction(), transitionStateId);
    }

    public StateTransition getApplicationProcessedOutcome(Resource resource, Comment comment) {
        PrismState transitionStateId = PrismState.valueOf(resource.getState().getId().toString() + "_COMPLETED");
        if (comment.getAction().getId() == PrismAction.APPLICATION_WITHDRAW) {
            transitionStateId = PrismState.APPLICATION_WITHDRAWN_COMPLETED;
        }
        if (resource.getInstitution().isUclInstitution() && resource.getState().getStateGroup().getId() != PrismStateGroup.APPLICATION_UNSUBMITTED) {
            transitionStateId = PrismState.valueOf(transitionStateId.toString().replace("COMPLETED", "PENDING_EXPORT"));
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

    public StateTransition getApplicationRecruitedOutcome(Resource resource, Comment comment) {
        Comment recruitedComment = commentService.getEarliestComment((ParentResource) resource, Resource.class,
                PrismAction.APPLICATION_CONFIRM_OFFER_RECOMMENDATION);
        return stateDAO.getStateTransition(resource.getState(), comment.getAction(), recruitedComment.getParentResourceTransitionState().getId());
    }

    public List<PrismState> getActiveProgramStates() {
        return stateDAO.getActiveProgramStates();
    }

    public List<PrismState> getActiveProjectStates() {
        return stateDAO.getActiveProjectStates();
    }

    public StateTransition getInstitutionApprovedOutcome(Resource resource, Comment comment) {
        PrismState transitionStateId = comment.getTransitionState().getId();
        return stateDAO.getStateTransition(resource.getState(), comment.getAction(), transitionStateId);
    }

    public StateTransition getProgramApprovedOutcome(Resource resource, Comment comment) {
        PrismState transitionStateId = comment.getTransitionState().getId();
        return stateDAO.getStateTransition(resource.getState(), comment.getAction(), transitionStateId);
    }

    public StateTransition getProgramViewEditOutcome(Resource resource, Comment comment) {
        // TODO implement
        return null;
    }

    public StateTransition getProjectViewEditOutcome(Resource resource, Comment comment) {
        // TODO implement
        return null;
    }

    public synchronized <T extends Resource> void executeDeferredStateTransition(Class<T> resourceClass, Integer resourceId, PrismAction actionId)
            throws DeduplicationException {
        Resource resource = resourceService.getById(resourceClass, resourceId);
        Action action = actionService.getById(actionId);

        logger.info("Calling " + action.getId() + " on " + resource.getCode());

        Comment comment = new Comment().withResource(resource).withUser(systemService.getSystem().getUser()).withAction(action).withDeclinedResponse(false)
                .withCreatedTimestamp(new DateTime());
        executeStateTransition(resource, action, comment);

        entityService.flushAndEvict(resource, action, comment);
    }

    public void deleteStateTransitionPending(Integer stateTransitionPendingId) {
        StateTransitionPending pending = entityService.getById(StateTransitionPending.class, stateTransitionPendingId);

        entityService.delete(pending);
        entityService.flushAndEvict(pending);
    }

    public List<PrismState> getStatesByStateGroup(PrismStateGroup stateGroupId) {
        return stateDAO.getStatesByStateGroup(stateGroupId);
    }
    
}
