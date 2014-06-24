package com.zuehlke.pgadmissions.services;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.beanutils.MethodUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.zuehlke.pgadmissions.dao.StateDAO;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.ResourceDynamic;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.StateTransitionEvaluation;
import com.zuehlke.pgadmissions.domain.StateTransitionPending;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismActionType;
import com.zuehlke.pgadmissions.domain.enums.PrismRole;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.domain.enums.PrismStateTransitionEvaluation;
import com.zuehlke.pgadmissions.mail.MailDescriptor;
import com.zuehlke.pgadmissions.mail.MailService;

@Service
@Transactional
public class StateService {

    @Autowired
    private StateDAO stateDAO;

    @Autowired
    private EntityService entityService;
    
    @Autowired
    private ResourceService resourceService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private MailService notificationService;

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
        return entityService.getAll(State.class);
    }
    
    public List<StateTransitionEvaluation> getTransitionEvaluations() {
        return entityService.getAll(StateTransitionEvaluation.class);
    }
    
    public Integer getStateDuration(Resource resource, State state) {
        return stateDAO.getStateDuration(resource, state);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public StateTransition executeStateTransition(Resource operativeResource, ResourceDynamic resource, Action action, Comment comment) {
        StateTransition stateTransition = getStateTransition(operativeResource, action, comment);

        postResourceStateChange(resource, stateTransition, comment);
        postResourceUpdate(resource, comment);
        setResourceDueDate(resource, comment);

        if (operativeResource != resource) {
            createResource(operativeResource, resource, action, comment);
        } else {
            updateResource(resource, action, comment);
        }

        roleService.executeUserRoleTransitions(resource, stateTransition, comment);

        for (MailDescriptor notification : userService.getUserStateTransitionNotifications(stateTransition)) {
            notificationService.sendEmailNotification(notification.getRecipient(), resource, notification.getNotificationTemplate(), comment);
        }

        if (stateTransition.getPropagatedActions().size() > 0) {
            entityService.save(new StateTransitionPending().withResource(operativeResource).withStateTransition(stateTransition));
        }
        
        if (stateTransition.isDoPostComment()) {
            entityService.save(comment);
        }

        return stateTransition;
    }

    public void executePropagatedStateTransitions() {
        Resource lastResource = null;
        for (StateTransitionPending pendingStateTransition : stateDAO.getPendingStateTransitions()) {
            Resource thisResource = pendingStateTransition.getResource();

            if (thisResource != lastResource) {
                HashMultimap<Action, ResourceDynamic> propagations = stateDAO.getPropagatedStateTransitions(pendingStateTransition);
                executeThreadedStateTransitions(propagations, systemService.getSystem().getUser());

                if (propagations.isEmpty()) {
                    entityService.delete(pendingStateTransition);
                }
            }

            lastResource = thisResource;
        }
    }

    public void executeEscalatedStateTransitions() {
        executeThreadedStateTransitions(stateDAO.getEscalatedStateTransitions(), systemService.getSystem().getUser());
    }
    
    public PrismStateTransitionEvaluation getTransitionEvaluation(StateAction stateAction) {
        return stateDAO.getStateTransitionEvaluationByStateAction(stateAction);
    }

    private void executeThreadedStateTransitions(final HashMultimap<Action, ResourceDynamic> threadedStateTransitions, final User invoker) {
        for (final Action action : threadedStateTransitions.keySet()) {
            for (final ResourceDynamic resource : threadedStateTransitions.get(action)) {
                threadedStateTransitionPool.submit(new Runnable() {
                    @Override
                    public void run() {
                        Comment comment = new Comment().withResource(resource).withUser(invoker).withAction(action);
                        executeStateTransition(resource, resource, action, comment);
                    }
                });
            }
        }
    }

    public ThreadPoolExecutor getThreadedStateTransitionPool() {
        return threadedStateTransitionPool;
    }
    
    public void disableStateActions() {
        stateDAO.disableStateActions();
    }

    private void postResourceStateChange(ResourceDynamic resource, StateTransition stateTransition, Comment comment) {
        State transitionState = stateTransition.getTransitionState();
        resource.setPreviousState(resource.getState());
        resource.setState(transitionState);
        comment.setTransitionState(transitionState);
    }

    private void postResourceUpdate(ResourceDynamic resource, Comment comment) {
        DateTime transitionTimestamp = new DateTime();
        resource.setUpdatedTimestamp(transitionTimestamp);
        comment.setCreatedTimestamp(transitionTimestamp);
    }

    private void setResourceDueDate(ResourceDynamic resource, Comment comment) {
        LocalDate dueDate = comment.getUserSpecifiedDueDate();
        if (dueDate == null && comment.getAction().getActionType() == PrismActionType.SYSTEM_ESCALATION) {
            LocalDate dueDateBaseline = resource.getDueDateBaseline();
            Integer stateDurationSeconds = stateDAO.getCurrentStateDuration(resource);
            dueDate = dueDateBaseline.plusDays(stateDurationSeconds != null ? stateDurationSeconds : 0);
        }
        resource.setDueDate(dueDate);
    }

    private void createResource(Resource operativeResource, ResourceDynamic resource, Action createAction, Comment comment) {
        resource.setParentResource(operativeResource);
        entityService.save(resource);
        resource.setCode(resource.generateCode());
        comment.setRole(roleService.getResourceCreatorRole(resource, createAction).getAuthority().toString());
    }

    private void updateResource(ResourceDynamic resource, Action action, Comment comment) {
        String role;
        String delegateRole = null;
        if (action.getActionType() != PrismActionType.USER_INVOCATION) {
            role = PrismRole.SYSTEM_ADMINISTRATOR.toString();
        } else {
            role = Joiner.on("|").join(roleService.getActionOwnerRoles(comment.getUser(), resource, action));
            if (comment.getDelegateUser() != null) {
                delegateRole = Joiner.on("|").join(roleService.getDelegateActionOwnerRoles(comment.getDelegateUser(), resource, action));
            }
        }
        comment.setRole(role);
        comment.setDelegateRole(delegateRole);
    }

    private StateTransition getStateTransition(Resource resource, Action action, Comment comment) {
        StateTransition stateTransition = null;

        List<StateTransition> potentialStateTransitions = stateDAO.getStateTransitions(resource, action);
        if (potentialStateTransitions.size() > 1) {
            try {
                String method = potentialStateTransitions.get(0).getStateTransitionEvaluation().getMethodName();
                stateTransition = (StateTransition) MethodUtils.invokeExactMethod(this, method, new Object[] { resource, comment, potentialStateTransitions });
            } catch (Exception e) {
                throw new Error(PrismStateTransitionEvaluation.INCORRECT_PROCESSOR_TYPE, e);
            }
        } else {
            stateTransition = potentialStateTransitions.get(0);
        }

        return stateTransition;
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
