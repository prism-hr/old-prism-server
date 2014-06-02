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
import com.zuehlke.pgadmissions.mail.NotificationService;

@Service
@Transactional
public class StateService {

    public static final int SECONDS_IN_DAY = 86400;

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
        transitionResourceState(resource, stateTransition, comment.getUserSpecifiedDueDate());

        if (operativeResource != resource) {
            resource.setParentResource(operativeResource);
            entityService.save(resource);
            resource.setCode(resource.generateCode());
            comment.setRole(roleService.getResourceCreatorRole(resource).getAuthority().toString());
        } else if (action.getActionType() != PrismActionType.USER) {
            comment.setRole(Authority.SYSTEM_ADMINISTRATOR.toString());
        } else {
            comment.setRole(Joiner.on("|").join(roleService.getActionOwnerRoles(comment.getUser(), resource, action)));
        }

        if (stateTransition.isDoPostComment()) {
            comment.setCreatedTimestamp(new DateTime());
            entityService.save(comment);
        }

        roleService.executeUserRoleTransitions(resource, stateTransition, comment);
        notificationService.sendStateTransitionNotifications(resource, stateTransition);
        
        if (stateTransition.getPropagatedActions().size() > 0) {
            StateTransitionPending pendingStateTransition = new StateTransitionPending().withResource(operativeResource).withStateTransition(stateTransition);
            entityService.save(pendingStateTransition);
        }
        
        return stateTransition;
    }

    @Scheduled(cron="0 0/1 * * *")
    public void executePropagatedStateTransitions() {
        for (StateTransitionPending pendingStateTransition : stateDAO.getPendingStateTransitions()) {
            HashMultimap<Action, PrismResourceDynamic> propagations = stateDAO.getPropagatedStateTransitions(pendingStateTransition);
            executeThreadedStateTransitions(propagations, systemService.getSystem().getUser());
            
            if (propagations.isEmpty()) {
                entityService.delete(pendingStateTransition);
            }
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

    private void transitionResourceState(PrismResourceDynamic resource, StateTransition stateTransition, LocalDate userSpecifiedDueDate) {
        resource.setPreviousState(resource.getState());
        resource.setState(stateTransition.getTransitionState());

        LocalDate dueDate = userSpecifiedDueDate;
        if (dueDate == null && actionDAO.getValidAction(resource, PrismAction.valueOf(resource.getResourceType().toString() + "_ESCALATE")) != null) {
            LocalDate dueDateBaseline = resource.getDueDateBaseline();
            Integer stateDurationSeconds = stateDAO.getStateDuration(resource);
            dueDate = dueDateBaseline.plusDays(stateDurationSeconds != null ? stateDurationSeconds / SECONDS_IN_DAY : 0);
        }

        resource.setDueDate(entityService.getResourceDueDate(resource, dueDate));
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
