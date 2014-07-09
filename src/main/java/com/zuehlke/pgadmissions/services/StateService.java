package com.zuehlke.pgadmissions.services;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.beanutils.MethodUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.HashMultimap;
import com.zuehlke.pgadmissions.dao.StateDAO;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.StateDuration;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.StateTransitionPending;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;

@Service
@Transactional
public class StateService {

    @Autowired
    private StateDAO stateDAO;

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

    @Autowired
    private UserService userService;

    private ThreadPoolExecutor threadedStateTransitionPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(1000);

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

    public List<State> getWorkflowStates() {
        return stateDAO.getWorkflowStates();
    }

    public StateDuration getStateDuration(Resource resource, State state) {
        return stateDAO.getStateDuration(resource, state);
    }

    public StateDuration getCurrentStateDuration(Resource resource) {
        return stateDAO.getStateDuration(resource, resource.getState());
    }

    public List<StateTransitionPending> getPendingStateTransitions() {
        return stateDAO.getPendingStateTransitions();
    }

    public void executeEscalatedStateTransitions() {
        executeThreadedStateTransitions(stateDAO.getEscalatedStateTransitions(), systemService.getSystem().getUser());
    }

    public void deleteStateActions() {
        stateDAO.deleteStateActions();
    }

    public void deleteObseleteStateDurations() {
        stateDAO.deleteObseleteStateDurations();
    }

    public <T extends Resource> List<State> getDeprecatedStates(Class<T> resourceClass) {
        return stateDAO.getDeprecatedStates(resourceClass);
    }

    public State getDegradationState(State state) {
        return stateDAO.getDegradationState(state);
    }

    public ThreadPoolExecutor getThreadedStateTransitionPool() {
        return threadedStateTransitionPool;
    }

    public List<StateAction> getStateActions() {
        return entityService.list(StateAction.class);
    }

    public List<State> getOrderedTransitionStates(State state, State... excludedTransitionStates) {
        return stateDAO.getOrderedTransitionStates(state, excludedTransitionStates);
    }

    public List<State> getRootState() {
        return stateDAO.getRootState();
    }

    public List<State> getUpstreamStates(State state) {
        return stateDAO.getUpstreamStates(state);
    }

    public List<State> getDownstreamStates(State state) {
        return stateDAO.getDownstreamStates(state);
    }

    public List<PrismState> getActionableStates(Collection<PrismAction> actions) {
        return stateDAO.getActionableStates(actions);
    }

    public StateTransition executeStateTransition(Resource resource, Action action, Comment comment) {
        if (action.isCreationAction()) {
            resourceService.commitResourceCreation(resource, action, comment);
        } else {
            resourceService.commitResourceUpdate(resource, action, comment);
        }

        if (action.isSaveComment()) {
            entityService.save(comment);
        }

        StateTransition stateTransition = getStateTransition(resource, action, comment);
        if (stateTransition != null) {
            StateDuration transitionStateDuration = getStateDuration(resource, stateTransition.getTransitionState());
            resourceService.transitionResourceState(resource, comment, stateTransition.getTransitionState(), transitionStateDuration);
            roleService.executeRoleTransitions(resource, comment, stateTransition);
            notificationService.sentUpdateNotifications(resource, comment, stateTransition);
            queuePropagatedStateTransitions(resource, stateTransition);
        }

        return stateTransition;
    }
    
    private StateTransition getStateTransition(Resource resource, Action action, Comment comment) {
        Resource operative = resourceService.getOperativeResource(resource, action);
        List<StateTransition> potentialStateTransitions = stateDAO.getStateTransitions(operative, action);
        
        if (potentialStateTransitions.size() > 1) {
            try {
                String method = potentialStateTransitions.get(0).getStateTransitionEvaluation().getMethodName();
                return (StateTransition) MethodUtils.invokeExactMethod(this, method, new Object[] { operative, comment, potentialStateTransitions });
            } catch (Exception e) {
                throw new Error(e);
            }
        } else {
            return potentialStateTransitions.isEmpty() ? null : potentialStateTransitions.get(0);
        }
    }
    
    public void executePropagatedStateTransitions() {
        Resource lastResource = null;
        for (StateTransitionPending pendingStateTransition : getPendingStateTransitions()) {
            Resource thisResource = pendingStateTransition.getResource();

            if (thisResource != lastResource) {
                HashMultimap<Action, Resource> propagations = stateDAO.getPropagatedStateTransitions(pendingStateTransition);
                executeThreadedStateTransitions(propagations, systemService.getSystem().getUser());

                if (propagations.isEmpty()) {
                    entityService.delete(pendingStateTransition);
                }
            }

            lastResource = thisResource;
        }
    }
    
    private void queuePropagatedStateTransitions(Resource resource, StateTransition stateTransition) {
        if (stateTransition.getPropagatedActions().size() > 0) {
            entityService.save(new StateTransitionPending().withResource(resource).withStateTransition(stateTransition));
        }
    }
     
    private void executeThreadedStateTransitions(final HashMultimap<Action, Resource> threadedStateTransitions, final User invoker) {
        for (final Action action : threadedStateTransitions.keySet()) {
            for (final Resource resource : threadedStateTransitions.get(action)) {
                threadedStateTransitionPool.submit(new Runnable() {
                    @Override
                    public void run() {
                        Comment comment = new Comment().withResource(resource).withUser(invoker).withAction(action);
                        executeStateTransition(resource, action, comment);
                    }
                });
            }
        }
    }

    @SuppressWarnings("unused")
    private StateTransition getApplicationCompletedOutcome(Resource resource, Comment comment, List<StateTransition> stateTransitions) {
        State transitionState = resource.getState();
        Application application = (Application) resource;
        if (application.getSubmittedTimestamp() != null) {
            transitionState = getById(PrismState.APPLICATION_VALIDATION_PENDING_FEEDBACK);
        }
        return stateDAO.getStateTransition(stateTransitions, transitionState);
    }

    @SuppressWarnings("unused")
    private StateTransition getApplicationEligibilityAssessedOutcome(Resource resource, Comment comment, List<StateTransition> stateTransitions) {
        PrismState transitionState = PrismState.APPLICATION_VALIDATION_PENDING_COMPLETION;
        if (comment.isAtLeastOneAnswerUnsure()) {
            transitionState = PrismState.APPLICATION_VALIDATION_PENDING_FEEDBACK;
        }
        return stateDAO.getStateTransition(stateTransitions, getById(transitionState));
    }

    @SuppressWarnings("unused")
    private StateTransition getApplicationExportedOutcome(Resource resource, Comment comment, List<StateTransition> stateTransitions) {
        State transitionState = resource.getState();
        State parentState = transitionState.getParentState();
        if (comment.getExportError() != null) {
            transitionState = getById(PrismState.valueOf(parentState.toString() + "_PENDING_CORRECTION"));
        } else if (comment.getExportResponse() != null && comment.getExportError() == null) {
            transitionState = getById(PrismState.valueOf(parentState.toString() + "_COMPLETED"));
        }
        return stateDAO.getStateTransition(stateTransitions, transitionState);
    }

    @SuppressWarnings("unused")
    private StateTransition getInterviewScheduledOutcome(Resource resource, Comment comment, List<StateTransition> stateTransitions) {
        State transitionState;
        DateTime baselineDateTime = new DateTime();
        DateTime interviewDateTime = comment.getInterviewDateTime();
        if (interviewDateTime != null) {
            if (interviewDateTime.isEqual(baselineDateTime) || interviewDateTime.isBefore(baselineDateTime)) {
                transitionState = getById(PrismState.APPLICATION_INTERVIEW_PENDING_FEEDBACK);
            } else {
                transitionState = getById(PrismState.APPLICATION_INTERVIEW_PENDING_INTERVIEW);
            }
        } else {
            if (resource.getState().getId() == PrismState.APPLICATION_INTERVIEW) {
                transitionState = getById(PrismState.APPLICATION_INTERVIEW_PENDING_AVAILABILITY);
            } else {
                transitionState = getById(PrismState.APPLICATION_INTERVIEW);
            }
        }
        return stateDAO.getStateTransition(stateTransitions, transitionState);
    }

}
