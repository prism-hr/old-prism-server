package com.zuehlke.pgadmissions.services;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.beanutils.MethodUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.zuehlke.pgadmissions.dao.ActionDAO;
import com.zuehlke.pgadmissions.dao.StateDAO;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.PrismResource;
import com.zuehlke.pgadmissions.domain.PrismResourceDynamic;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.StateTransitionPending;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;
import com.zuehlke.pgadmissions.domain.enums.PrismActionType;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.domain.enums.StateTransitionEvaluation;
import com.zuehlke.pgadmissions.mail.NotificationDescriptor;
import com.zuehlke.pgadmissions.mail.NotificationService;

@Service
@Transactional
public class StateService {

    @Autowired
    private ActionDAO actionDAO;

    @Autowired
    private StateDAO stateDAO;

    @Autowired
    private EntityService entityService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private UserService userService;

    private ThreadPoolExecutor threadedStateTransitionPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(1000);

    public State getById(PrismState id) {
        return stateDAO.getById(id);
    }

    public void save(State state) {
        stateDAO.save(state);
    }

    public List<State> getAllConfigurableStates() {
        return stateDAO.getAllConfigurableStates();
    }

    public StateTransition executeStateTransition(PrismResource operativeResource, PrismResourceDynamic resource, Action action, Comment comment) {
        StateTransition stateTransition = getStateTransition(operativeResource, action, comment);
        
        postResourceStateChange(resource, stateTransition, comment);
        postResourceUpdate(resource, comment);
        setResourceDueDate(resource, comment);

        if (operativeResource != resource) {
            createResource(operativeResource, resource, comment);
        } else {
            updateResource(resource, action, comment);
        }
        
        if (stateTransition.isDoPostComment()) {
            entityService.save(comment);
        }

        roleService.executeUserRoleTransitions(resource, stateTransition, comment);
        
        for (NotificationDescriptor notification : userService.getUserStateTransitionNotifications(stateTransition)) {
            notificationService.sendEmailNotification(notification.getRecipient(), resource, notification.getNotificationTemplate(), comment);
        }

        if (stateTransition.getPropagatedActions().size() > 0) {
            entityService.save(new StateTransitionPending().withResource(operativeResource).withStateTransition(stateTransition));
        }

        return stateTransition;
    }

    @Scheduled(cron = "0 0/1 * * *")
    public void executePropagatedStateTransitions() {
        PrismResource lastResource = null;
        for (StateTransitionPending pendingStateTransition : stateDAO.getPendingStateTransitions()) {
            PrismResource thisResource = pendingStateTransition.getResource();

            if (thisResource != lastResource) {
                HashMultimap<Action, PrismResourceDynamic> propagations = stateDAO.getPropagatedStateTransitions(pendingStateTransition);
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

    private void executeThreadedStateTransitions(final HashMultimap<Action, PrismResourceDynamic> threadedStateTransitions, final User invoker) {
        for (final Action action : threadedStateTransitions.keySet()) {
            for (final PrismResourceDynamic resource : threadedStateTransitions.get(action)) {
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
    
    private void postResourceStateChange(PrismResourceDynamic resource, StateTransition stateTransition, Comment comment) {
        State transitionState = stateTransition.getTransitionState();
        resource.setPreviousState(resource.getState());
        resource.setState(transitionState);
        comment.setTransitionState(transitionState);
    }
    
    private void postResourceUpdate(PrismResourceDynamic resource, Comment comment) {
        DateTime transitionTimestamp = new DateTime();
        resource.setUpdatedTimestamp(transitionTimestamp);
        comment.setCreatedTimestamp(transitionTimestamp);
    }
    
    private void setResourceDueDate(PrismResourceDynamic resource, Comment comment) {
        LocalDate dueDate = comment.getUserSpecifiedDueDate();
        if (dueDate == null && actionDAO.getValidAction(resource, PrismAction.valueOf(resource.getResourceType().toString() + "_ESCALATE")) != null) {
            LocalDate dueDateBaseline = resource.getDueDateBaseline();
            Integer stateDurationSeconds = stateDAO.getStateDuration(resource);
            dueDate = dueDateBaseline.plusDays(stateDurationSeconds != null ? stateDurationSeconds : 0);
        }
        resource.setDueDate(entityService.getResourceDueDate(resource, dueDate));
    }
    
    private void createResource(PrismResource operativeResource, PrismResourceDynamic resource, Comment comment) {
        resource.setParentResource(operativeResource);
        entityService.save(resource);
        resource.setCode(resource.generateCode());
        comment.setRole(roleService.getResourceCreatorRole(resource).getAuthority().toString());
    }
    
    private void updateResource(PrismResourceDynamic resource, Action action, Comment comment) {
        String role;
        String delegateRole = null;
        if (action.getActionType() != PrismActionType.USER_INVOCATION) {
            role = Authority.SYSTEM_ADMINISTRATOR.toString();
        } else {
            role = Joiner.on("|").join(roleService.getActionOwnerRoles(comment.getUser(), resource, action));
            if (comment.getDelegateUser() != null) {
                delegateRole = Joiner.on("|").join(roleService.getActionOwnerRoles(comment.getDelegateUser(), resource, action.getDelegateAction()));
            }
        }
        comment.setRole(role);
        comment.setDelegateRole(delegateRole);
    }

    private StateTransition getStateTransition(PrismResource resource, Action action, Comment comment) {
        StateTransition stateTransition = null;

        List<StateTransition> potentialStateTransitions = stateDAO.getStateTransitions(resource, action);
        if (potentialStateTransitions.size() > 1) {
            try {
                String method = potentialStateTransitions.get(0).getEvaluation().getMethodName();
                stateTransition = (StateTransition) MethodUtils.invokeExactMethod(this, method, new Object[] { resource, comment, potentialStateTransitions });
            } catch (Exception e) {
                throw new Error(StateTransitionEvaluation.INCORRECT_PROCESSOR_TYPE, e);
            }
        } else {
            stateTransition = potentialStateTransitions.get(0);
        }

        return stateTransition;
    }

    @SuppressWarnings("unused")
    private StateTransition getApplicationCompletedOutcome(PrismResource resource, Comment comment, List<StateTransition> stateTransitions) {
        State transitionState = resource.getState();
        Application application = (Application) resource;
        if (application.getSubmittedTimestamp() != null) {
            transitionState = stateDAO.getById(PrismState.APPLICATION_VALIDATION_PENDING_FEEDBACK);
        }
        return stateDAO.getStateTransition(stateTransitions, transitionState);
    }

    @SuppressWarnings("unused")
    private StateTransition getApplicationEligibilityAssessedOutcome(PrismResource resource, Comment comment, List<StateTransition> stateTransitions) {
        PrismState transitionState = PrismState.APPLICATION_VALIDATION_PENDING_COMPLETION;
        if (comment.isAtLeastOneAnswerUnsure()) {
            transitionState = PrismState.APPLICATION_VALIDATION_PENDING_FEEDBACK;
        }
        return stateDAO.getStateTransition(stateTransitions, stateDAO.getById(transitionState));
    }

    @SuppressWarnings("unused")
    private StateTransition getApplicationExportedOutcome(PrismResource resource, Comment comment, List<StateTransition> stateTransitions) {
        State transitionState = resource.getState();
        State parentState = transitionState.getParentState();
        if (comment.getExportError() != null) {
            transitionState = stateDAO.getById(PrismState.valueOf(parentState.toString() + "_PENDING_CORRECTION"));
        } else if (comment.getExportResponse() != null && comment.getExportError() == null) {
            transitionState = stateDAO.getById(PrismState.valueOf(parentState.toString() + "_COMPLETED"));
        }
        return stateDAO.getStateTransition(stateTransitions, transitionState);
    }

    @SuppressWarnings("unused")
    private StateTransition getInterviewScheduledOutcome(PrismResource resource, Comment comment, List<StateTransition> stateTransitions) {
        State transitionState;
        DateTime baselineDateTime = new DateTime();
        DateTime interviewDateTime = comment.getInterviewDateTime();
        if (interviewDateTime != null) {
            if (interviewDateTime.isEqual(baselineDateTime) || interviewDateTime.isBefore(baselineDateTime)) {
                transitionState = stateDAO.getById(PrismState.APPLICATION_INTERVIEW_PENDING_FEEDBACK);
            } else {
                transitionState = stateDAO.getById(PrismState.APPLICATION_INTERVIEW_PENDING_INTERVIEW);
            }
        } else {
            if (resource.getState().getId() == PrismState.APPLICATION_INTERVIEW) {
                transitionState = stateDAO.getById(PrismState.APPLICATION_INTERVIEW_PENDING_AVAILABILITY);
            } else {
                transitionState = stateDAO.getById(PrismState.APPLICATION_INTERVIEW);
            }
        }
        return stateDAO.getStateTransition(stateTransitions, transitionState);
    }

}
