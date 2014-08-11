package com.zuehlke.pgadmissions.services;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

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
@Transactional
public class StateService {
    
    private final HashMap<Resource, Action> escalationsPending = Maps.newLinkedHashMap();
    
    private final HashMap<Resource, Action> propagationsPending = Maps.newLinkedHashMap();
    
    private final ThreadPoolExecutor transitionRunner = (ThreadPoolExecutor) Executors.newFixedThreadPool(1000);
    
    @Autowired
    private StateDAO stateDAO;

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

    @Autowired
    private UserService userService;

    public State getById(PrismState id) {
        return entityService.getByProperty(State.class, "id", id);
    }

    public void save(State state) {
        entityService.save(state);
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

    public List<State> getWorkflowStates() {
        return stateDAO.getWorkflowStates();
    }

    public StateDuration getStateDuration(Resource resource, State state) {
        return stateDAO.getStateDuration(resource, state);
    }

    public StateDuration getCurrentStateDuration(Resource resource) {
        return stateDAO.getStateDuration(resource, resource.getState());
    }

    public void deleteStateActions() {
        entityService.deleteAll(RoleTransition.class);
        entityService.deleteAll(StateTransition.class);
        entityService.deleteAll(StateActionAssignment.class);
        entityService.deleteAll(StateActionNotification.class);
        entityService.deleteAll(StateAction.class);
    }

    public void deleteObseleteStateDurations() {
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
    
    public List<StateTransitionPending> getStateTransitionsPending() {
        List<StateTransitionPending> pendingStateTransitions = Lists.newArrayList();
        for (Scope scope : scopeService.getScopesDescending()) {
            pendingStateTransitions.addAll(stateDAO.getStateTransitionsPending(scope));
        }
        return pendingStateTransitions;
    }
    
    public StateTransition executeStateTransition(Resource resource, Action action, Comment comment) throws WorkflowEngineException {
        comment.setResource(resource);
        
        if (action.getActionCategory() == PrismActionCategory.CREATE_RESOURCE) {
            resourceService.persistResource(resource, action, comment);
        } else {
            resourceService.updateResource(resource, action, comment);
        }

        entityService.save(comment);

        StateTransition stateTransition = getStateTransition(resource, action, comment);
        if (stateTransition != null) {
            State transitionState = stateTransition.getTransitionState();
            StateDuration transitionStateDuration = getStateDuration(resource, transitionState);
            resourceService.transitionResource(resource, comment, transitionState, transitionStateDuration);
            
            try {
                roleService.executeRoleTransitions(stateTransition, comment);
            } catch (WorkflowEngineException e) {
                throw new Error(e);
            }
            
            notificationService.sendUpdateNotifications(stateTransition.getStateAction(), resource, comment);

            if (stateTransition.getPropagatedActions().size() > 0) {
                entityService.save(new StateTransitionPending().withResource(resource).withStateTransition(stateTransition));
            }
        }

        return stateTransition;
    }
    
    public StateTransition getStateTransition(Resource resource, Action action, Comment comment) {
        Resource operative = resourceService.getOperativeResource(resource, action);
        List<StateTransition> potentialStateTransitions = stateDAO.getStateTransitions(operative, action);
        
        if (potentialStateTransitions.size() > 1) {
            try {
                PrismTransitionEvaluation transitionEvaluation = potentialStateTransitions.get(0).getStateTransitionEvaluation();
                return (StateTransition) MethodUtils.invokeMethod(this, transitionEvaluation.getMethodName(), new Object[]{operative, comment});
            } catch (Exception e) {
                throw new Error(e);
            }
        }
        
        return potentialStateTransitions.isEmpty() ? null : potentialStateTransitions.get(0);
    }
    
    public List<PrismState> getAvailableNextStates(Resource resource, PrismAction actionId) {
        return stateDAO.getAvailableNextStates(resource, actionId);
    }

    public StateTransition getApplicationEvaluatedOutcome(Resource resource, Comment comment) {
        return stateDAO.getStateTransition(resource.getState(), comment.getAction(), comment.getTransitionState().getId());
	}

    public StateTransition getApplicationReviewedOutcome(Resource resource, Comment comment, PrismTransitionEvaluation evaluation) {
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
    
    public boolean isDeferredStateTransitions() {
        return !escalationsPending.isEmpty() || !propagationsPending.isEmpty();
    }
    
    public void executeDeferredStateTransitions() {
        marshalDeferredStateTransitions(resourceService.getResourceEscalations());
        marshalDeferredStateTransitions(resourceService.getResourcePropagations());
        
        if (propagationsPending.isEmpty()) {
            flushDeferredStateTransitions(escalationsPending);
        } else if (escalationsPending.isEmpty()) {
            flushDeferredStateTransitions(propagationsPending); 
        }
    }

    private void marshalDeferredStateTransitions(HashMap<Resource, Action> transitions) {
        for (Resource resource : transitions.keySet()) {
            if (!escalationsPending.containsKey(resource)) {
                escalationsPending.put(resource, transitions.get(resource));
            }
        }
    }

    private void flushDeferredStateTransitions(final HashMap<Resource, Action> transitions) {
        final User user = systemService.getSystem().getUser();
        for (final Resource resource : transitions.keySet()) {
            final Action action = transitions.get(resource);
            transitionRunner.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        Comment comment = new Comment().withResource(resource).withUser(user).withAction(action);
                        executeStateTransition(resource, action, comment);
                        transitions.remove(resource);
                    } catch (WorkflowEngineException e) {
                        throw new Error(e);
                    }
                }
            });
        }
    }
    
}
