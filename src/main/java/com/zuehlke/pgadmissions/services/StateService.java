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
import com.zuehlke.pgadmissions.dao.ActionDAO;
import com.zuehlke.pgadmissions.dao.StateDAO;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.PrismResource;
import com.zuehlke.pgadmissions.domain.PrismResourceTransient;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;
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

    public StateTransition executeThreadedStateTransition(final PrismResource operativeResource, final PrismResourceTransient resource, final Action action,
            final Comment comment) {
        final StateTransition stateTransition = getStateTransition(operativeResource, action, comment);

        threadedStateTransitionPool.submit(new Runnable() {
            @Override
            public void run() {
                executeStateTransition(operativeResource, resource, stateTransition, action, comment);
            }

        });

        return stateTransition;
    }

    public void executeEscalatedStateTransitions() {
        HashMultimap<Action, PrismResourceTransient> escalations = stateDAO.getEscalatedStateTransitions();
        for (Action escalatedAction : escalations.keySet()) {
            for (PrismResourceTransient escalatedResource : escalations.get(escalatedAction)) {
                Comment escalatedComment = new Comment().withResource(escalatedResource).withUser(systemService.getSystem().getUser())
                        .withAction(escalatedAction);
                executeThreadedStateTransition(escalatedResource, escalatedResource, escalatedAction, escalatedComment);
            }
        }
    }

    private void executePropagatedStateTransitions(PrismResourceTransient resource, StateTransition stateTransition) {
        HashMultimap<Action, PrismResourceTransient> propagations = stateDAO.getPropagatedStateTransitions(resource, stateTransition);
        for (Action propagatedAction : propagations.keySet()) {
            for (PrismResourceTransient propagatedResource : propagations.get(propagatedAction)) {
                Comment propagatedComment = new Comment().withResource(propagatedResource).withUser(systemService.getSystem().getUser())
                        .withAction(propagatedAction);
                StateTransition propagatedStateTransition = getStateTransition(propagatedResource, propagatedAction, propagatedComment);
                executeStateTransition(propagatedResource, propagatedResource, propagatedStateTransition, propagatedAction, propagatedComment);
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void executeStateTransition(PrismResource operativeResource, PrismResourceTransient resource, StateTransition stateTransition, Action action,
            Comment comment) {
        transitionResourceState(resource, stateTransition, comment.getUserSpecifiedDueDate());

        if (operativeResource != resource) {
            resource.setParentResource(operativeResource);
            entityService.save(resource);
            resource.setCode(resource.generateCode());
            comment.setRole(roleService.getResourceCreatorRole(resource).getAuthority().toString());
        } else if (action.isSystemAction()) {
            comment.setRole(Authority.SYSTEM_ADMINISTRATOR.toString());
        } else {
            comment.setRole(Joiner.on("|").join(roleService.getActionOwnerRoles(comment.getUser(), resource, action)));
        }

        if (stateTransition.isDoPostComment()) {
            comment.setCreatedTimestamp(new DateTime());
            entityService.save(comment);
        }

        roleService.executeUserRoleTransitions(resource, stateTransition, comment);
        notificationService.setStateTransitionNotifications(resource, stateTransition);

        executePropagatedStateTransitions(resource, stateTransition);
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

    private void transitionResourceState(PrismResourceTransient resource, StateTransition stateTransition, LocalDate userSpecifiedDueDate) {
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
