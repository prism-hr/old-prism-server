package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.apache.commons.beanutils.MethodUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Joiner;
import com.zuehlke.pgadmissions.dao.ActionDAO;
import com.zuehlke.pgadmissions.dao.StateDAO;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.PrismResource;
import com.zuehlke.pgadmissions.domain.PrismResourceTransient;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;
import com.zuehlke.pgadmissions.domain.enums.PrismState;

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

    public State getById(PrismState id) {
        return stateDAO.getById(id);
    }

    public void save(State state) {
        stateDAO.save(state);
    }

    public List<State> getAllConfigurableStates() {
        return stateDAO.getAllConfigurableStates();
    }

    public StateTransition getStateTransition(PrismResource resource, PrismAction action, Comment comment) {
        StateTransition stateTransition = null;

        List<StateTransition> potentialStateTransitions = getPotentialStateTransitions(resource, action);
        if (potentialStateTransitions.size() > 1) {
            try {
                String method = potentialStateTransitions.get(0).getEvaluation().getMethodName();
                stateTransition = (StateTransition) MethodUtils.invokeExactMethod(this, method, new Object[] { resource, comment, potentialStateTransitions });
            } catch (Exception e) {

            }
        } else {
            stateTransition = potentialStateTransitions.get(0);
        }

        return stateTransition;
    }

    public StateTransition executeStateTransition(PrismResource operativeResource, PrismResourceTransient resource, User invoker, PrismAction action,
            Comment comment) {
        StateTransition stateTransition = getStateTransition(operativeResource, action, comment);
        transitionResourceState(resource, stateTransition, comment.getUserSpecifiedDueDate());

        if (operativeResource != resource) {
            resource.setParentResource(operativeResource);
            entityService.save(resource);
            PrismResourceTransient codableResource = (PrismResourceTransient) resource;
            codableResource.setCode(codableResource.generateCode());
        }

        if (stateTransition.isDoPostComment()) {
            comment.setCreatedTimestamp(new DateTime());
            entityService.save(comment);
        }

        roleService.executeUserRoleTransitions(stateTransition, resource, invoker, comment);
        comment.setRole(Joiner.on("|").join(roleService.getActionInvokerRoles(invoker, resource, action)));
        return stateTransition;
    }

    public void executeDelegateStateTransition(PrismResourceTransient resource, StateTransition delegateStateTransition, User invoker) {
        transitionResourceState(resource, delegateStateTransition, null);
        roleService.executeDelegateUserRoleTransitions(resource, delegateStateTransition, invoker);
    }

    public void executePropagatedStateTransitions(PrismResourceTransient resource, StateTransition stateTransition) {
        stateDAO.executePropagatedStateTransitions(resource, stateTransition);
    }

    public void executeEscalatedStateTransitions() {
        stateDAO.executeEscalatedStateTransitions();
    }

    public StateTransition getApplicationCompletedOutcome(PrismResource resource, Comment comment, List<StateTransition> stateTransitions) {
        return stateDAO.getStateTransition(stateTransitions, comment.getTransitionState());
    }

    public StateTransition getApplicationEligibilityAssessedOutcome(PrismResource resource, Comment comment, List<StateTransition> stateTransitions) {
        PrismState transitionState = PrismState.APPLICATION_VALIDATION_PENDING_COMPLETION;
        if (comment.isAtLeastOneAnswerUnsure()) {
            transitionState = PrismState.APPLICATION_VALIDATION_PENDING_FEEDBACK;
        }
        return stateDAO.getStateTransition(stateTransitions, stateDAO.getById(transitionState));
    }

    public StateTransition getApplicationExportedOutcome(PrismResource resource, Comment comment, List<StateTransition> stateTransitions) {
        State transitionState = resource.getState();
        State parentState = transitionState.getParentState();
        if (comment.getExportError() != null) {
            transitionState = stateDAO.getById(PrismState.valueOf(parentState.toString() + "_PENDING_CORRECTION"));
        } else if (comment.getExportResponse() != null && comment.getExportError() == null) {
            transitionState = stateDAO.getById(PrismState.valueOf(parentState.toString() + "_COMPLETED"));
        }
        return stateDAO.getStateTransition(stateTransitions, transitionState);
    }

    public StateTransition getInterviewScheduledOutcome(PrismResource resource, Comment comment, List<StateTransition> stateTransitions) {
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

    private List<StateTransition> getPotentialStateTransitions(PrismResource resource, PrismAction action) {
        return stateDAO.getStateTransitions(resource, action);
    }

    private void transitionResourceState(PrismResourceTransient resource, StateTransition stateTransition, LocalDate userSpecifiedDueDate) {
        resource.setState(stateTransition.getTransitionState());

        LocalDate dueDate = userSpecifiedDueDate;
        if (dueDate == null && actionDAO.getValidResourceAction(resource, PrismAction.valueOf(resource.getResourceType().toString() + "_ESCALATE")) != null) {
            LocalDate dueDateBaseline = resource.getDueDateBaseline();
            Integer stateDurationSeconds = stateDAO.getStateDuration(resource);
            dueDate = dueDateBaseline.plusDays(stateDurationSeconds != null ? stateDurationSeconds / SECONDS_IN_DAY : 0);
        }

        resource.setDueDate(entityService.getResourceDueDate(resource, dueDate));
    }
}
