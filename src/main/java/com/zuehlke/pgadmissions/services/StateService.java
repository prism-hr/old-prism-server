package com.zuehlke.pgadmissions.services;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.beanutils.MethodUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.dao.StateDAO;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.RoleTransition;
import com.zuehlke.pgadmissions.domain.Scope;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.StateActionAssignment;
import com.zuehlke.pgadmissions.domain.StateActionNotification;
import com.zuehlke.pgadmissions.domain.StateDuration;
import com.zuehlke.pgadmissions.domain.StateGroup;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.StateTransitionPending;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismTransitionEvaluation;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;

@Service
public class StateService {

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

    @Transactional
    public State getById(PrismState id) {
        return entityService.getByProperty(State.class, "id", id);
    }

    @Transactional
    public List<State> getConfigurableStates() {
        return stateDAO.getConfigurableStates();
    }

    @Transactional
    public List<State> getStates() {
        return entityService.list(State.class);
    }

    @Transactional
    public List<StateGroup> getStateGroups() {
        return entityService.list(StateGroup.class);
    }

    @Transactional
    public List<State> getWorkflowStates() {
        return stateDAO.getWorkflowStates();
    }

    @Transactional
    public StateDuration getStateDuration(Resource resource, State state) {
        return stateDAO.getStateDuration(resource, state);
    }

    @Transactional
    public void deleteStateActions() {
        entityService.deleteAll(RoleTransition.class);
        entityService.deleteAll(StateTransition.class);
        entityService.deleteAll(StateActionAssignment.class);
        entityService.deleteAll(StateActionNotification.class);
        entityService.deleteAll(StateAction.class);
    }

    @Transactional
    public void deleteObsoleteStateDurations() {
        stateDAO.deleteObseleteStateDurations(getConfigurableStates());
    }

    @Transactional
    public <T extends Resource> List<State> getDeprecatedStates(Class<T> resourceClass) {
        return stateDAO.getDeprecatedStates(resourceClass);
    }

    @Transactional
    public List<StateAction> getStateActions() {
        return entityService.list(StateAction.class);
    }

    @Transactional
    public List<State> getOrderedTransitionStates(State state, State... excludedTransitionStates) {
        return stateDAO.getOrderedTransitionStates(state, excludedTransitionStates);
    }

    @Transactional
    public List<StateTransitionPending> getStateTransitionsPending() {
        List<StateTransitionPending> pendingStateTransitions = Lists.newArrayList();
        for (Scope scope : scopeService.getScopesDescending()) {
            pendingStateTransitions.addAll(stateDAO.getStateTransitionsPending(scope));
        }
        return pendingStateTransitions;
    }

    @Transactional
    public StateTransition executeStateTransition(Resource resource, Action action, Comment comment) throws WorkflowEngineException {
        comment.setResource(resource);

        if (action.getActionCategory() == PrismActionCategory.CREATE_RESOURCE) {
            resourceService.persistResource(resource);
        }

        commentService.save(comment);
        StateTransition stateTransition = getStateTransition(resource, action, comment);

        State state = resource.getState();
        State transitionState = stateTransition == null ? state : stateTransition.getTransitionState();

        if (state != transitionState) {
            comment.setTransitionState(transitionState);

            resourceService.processResource(resource, comment);
            roleService.executeRoleTransitions(stateTransition, comment);

            if (stateTransition.getPropagatedActions().size() > 0) {
                StateTransitionPending transientTransitionPending = new StateTransitionPending().withResource(resource).withStateTransition(stateTransition);
                entityService.getOrCreate(transientTransitionPending);
            }

            notificationService.sendWorkflowNotifications(resource, comment);
        }

        resourceService.updateResource(resource, comment);
        return stateTransition;
    }

    @Transactional
    public StateTransition getStateTransition(Resource resource, Action action, Comment comment) {
        Resource operative = resourceService.getOperativeResource(resource, action);
        List<StateTransition> potentialStateTransitions = stateDAO.getStateTransitions(operative, action);

        if (potentialStateTransitions.size() > 1) {
            try {
                PrismTransitionEvaluation transitionEvaluation = potentialStateTransitions.get(0).getStateTransitionEvaluation();
                return (StateTransition) MethodUtils.invokeMethod(this, transitionEvaluation.getMethodName(), new Object[] { operative, comment });
            } catch (Exception e) {
                throw new Error(e);
            }
        }

        return potentialStateTransitions.isEmpty() ? null : potentialStateTransitions.get(0);
    }

    @Transactional
    public List<PrismState> getAvailableNextStates(Resource resource, PrismAction actionId) {
        return stateDAO.getAvailableNextStates(resource, actionId);
    }

    @Transactional
    public StateTransition getApplicationEvaluatedOutcome(Resource resource, Comment comment) {
        PrismState transitionStateId = comment.getTransitionState().getId();
        return stateDAO.getStateTransition(resource.getState(), comment.getAction(), transitionStateId);
    }

    @Transactional
    public StateTransition getApplicationReviewedOutcome(Resource resource, Comment comment, PrismTransitionEvaluation evaluation) {
        PrismState transitionState = PrismState.APPLICATION_REVIEW_PENDING_FEEDBACK;
        if (roleService.getRoleUsers(resource, roleService.getById(PrismRole.APPLICATION_REVIEWER)).size() == 1) {
            transitionState = PrismState.APPLICATION_REVIEW_PENDING_COMPLETION;
        }
        return stateDAO.getStateTransition(resource.getState(), comment.getAction(), transitionState);
    }

    @Transactional
    public StateTransition getApplicationInterviewRsvpedOutcome(Resource resource, Comment comment) {
        PrismState transitionStateId = PrismState.APPLICATION_INTERVIEW_PENDING_AVAILABILITY;
        List<User> interviewees = roleService.getRoleUsers(resource, roleService.getById(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE));
        List<User> interviewers = roleService.getRoleUsers(resource, roleService.getById(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER));
        if ((interviewees.size() + interviewers.size()) == 1) {
            transitionStateId = PrismState.APPLICATION_INTERVIEW_PENDING_SCHEDULING;
        }
        return stateDAO.getStateTransition(resource.getState(), comment.getAction(), transitionStateId);
    }

    @Transactional
    public StateTransition getApplicationSupervisionConfirmedOutcome(Resource resource, Comment comment) {
        PrismState transitionStateId = PrismState.APPLICATION_APPROVAL_PENDING_FEEDBACK;
        List<User> primarySupervisors = roleService.getRoleUsers(resource, roleService.getById(PrismRole.APPLICATION_PRIMARY_SUPERVISOR));
        List<User> secondarySupervisors = roleService.getRoleUsers(resource, roleService.getById(PrismRole.APPLICATION_SECONDARY_SUPERVISOR));
        if ((primarySupervisors.size() + secondarySupervisors.size()) == 1) {
            transitionStateId = PrismState.APPLICATION_APPROVAL_PENDING_COMPLETION;
        }
        return stateDAO.getStateTransition(resource.getState(), comment.getAction(), transitionStateId);
    }

    @Transactional
    public StateTransition getApplicationInterviewScheduledOutcome(Resource resource, Comment comment) {
        PrismState transitionStateId;
        DateTime interviewDateTime = comment.getInterviewDateTime();
        if (interviewDateTime != null) {
            if (new DateTime().isAfter(interviewDateTime)) {
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

    @Transactional
    public StateTransition getApplicationInterviewedOutcome(Resource resource, Comment comment) {
        PrismState transitionStateId = PrismState.APPLICATION_INTERVIEW_PENDING_FEEDBACK;
        if (roleService.getRoleUsers(resource, roleService.getById(PrismRole.APPLICATION_INTERVIEWER)).size() == 1) {
            transitionStateId = PrismState.APPLICATION_INTERVIEW_PENDING_COMPLETION;
        }
        return stateDAO.getStateTransition(resource.getState(), comment.getAction(), transitionStateId);
    }

    @Transactional
    public StateTransition getInstitutionCreatedOutcome(Resource resource, Comment comment) {
        PrismState transitionStateId = PrismState.INSTITUTION_APPROVAL;
        if (roleService.hasUserRole(resource, comment.getUser(), PrismRole.SYSTEM_ADMINISTRATOR)) {
            transitionStateId = PrismState.INSTITUTION_APPROVED;
        }
        return stateDAO.getStateTransition(resource.getState(), comment.getAction(), transitionStateId);
    }

    @Transactional
    public StateTransition getApplicationEligibilityAssessedOutcome(Resource resource, Comment comment) {
        PrismState transitionStateId = PrismState.APPLICATION_VALIDATION_PENDING_COMPLETION;
        if (comment.isApplicationCreatorEligibilityUncertain()) {
            transitionStateId = PrismState.APPLICATION_VALIDATION_PENDING_FEEDBACK;
        }
        return stateDAO.getStateTransition(resource.getState(), comment.getAction(), transitionStateId);
    }

    // FIXME: completed the integration with the exporter
    @Transactional
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

    @Transactional
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

    @Transactional
    public StateTransition getProgramCreatedOutcome(Resource resource, Comment comment) {
        PrismState transitionStateId = PrismState.PROGRAM_APPROVAL;
        if (roleService.hasUserRole(resource, comment.getUser(), PrismRole.INSTITUTION_ADMINISTRATOR)) {
            transitionStateId = PrismState.PROGRAM_APPROVED;
        }
        return stateDAO.getStateTransition(resource.getState(), comment.getAction(), transitionStateId);
    }

    @Transactional
    public List<State> getActiveProgramStates() {
        return stateDAO.getActiveProgramStates();
    }

    @Transactional
    public List<State> getActiveProjectStates() {
        return stateDAO.getActiveProjectStates();
    }

    @Transactional
    public StateTransition getInstitutionApprovedOutcome(Resource resource, Comment comment) {
        PrismState transitionStateId = comment.getTransitionState().getId();
        return stateDAO.getStateTransition(resource.getState(), comment.getAction(), transitionStateId);
    }

    @Transactional
    public StateTransition getProgramApprovedOutcome(Resource resource, Comment comment) {
        PrismState transitionStateId = comment.getTransitionState().getId();
        return stateDAO.getStateTransition(resource.getState(), comment.getAction(), transitionStateId);
    }

    @Transactional
    public StateTransition getProgramConfiguredOutcome(Resource resource, Comment comment) {
        // TODO implement
        return null;
    }

    public void executePendingStateTransitions() {
        HashMap<Resource, Action> transitions = Maps.newHashMap();
        transitions.putAll(resourceService.getResourcePropagations());
        transitions.putAll(resourceService.getResourceEscalations());

        for (Resource resource : transitions.keySet()) {
            executePendingStateTransition(resource, transitions.get(resource));
        }
    }

    @Transactional
    private void executePendingStateTransition(Resource resource, Action action) {
        resource = resourceService.getById(resource.getClass(), resource.getId());
        action = actionService.getById(action.getId());
        Comment comment = new Comment().withResource(resource).withUser(systemService.getSystem().getUser()).withAction(action).withDeclinedResponse(false);
        executeStateTransition(resource, action, comment);
    }

}
