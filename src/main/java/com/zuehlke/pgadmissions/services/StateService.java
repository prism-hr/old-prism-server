package com.zuehlke.pgadmissions.services;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.zuehlke.pgadmissions.domain.definitions.workflow.*;
import org.apache.commons.beanutils.MethodUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.dao.StateDAO;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.IUniqueEntity;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.RoleTransition;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.StateActionAssignment;
import com.zuehlke.pgadmissions.domain.StateActionNotification;
import com.zuehlke.pgadmissions.domain.StateDuration;
import com.zuehlke.pgadmissions.domain.StateGroup;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.StateTransitionPending;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;

@Service
@Transactional
public class StateService {
    
    private ThreadPoolExecutor threadedStateTransitionPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(1000);
    
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

    public List<StateTransitionPending> getPendingStateTransitions() {
        return stateDAO.getPendingStateTransitions();
    }

    public void executeEscalatedStateTransitions() {
        executeThreadedStateTransitions(stateDAO.getEscalatedStateTransitions(), systemService.getSystem().getUser());
    }

    public void deleteStateActions() {
        Set<Class<? extends IUniqueEntity>> workflowConfigurationClasses = Sets.newLinkedHashSet();

        workflowConfigurationClasses.add(RoleTransition.class);
        workflowConfigurationClasses.add(StateTransition.class);
        workflowConfigurationClasses.add(StateActionAssignment.class);
        workflowConfigurationClasses.add(StateActionNotification.class);
        workflowConfigurationClasses.add(StateAction.class);

        for (Class<? extends IUniqueEntity> workflowConfigurationClass : workflowConfigurationClasses) {
            entityService.deleteAll(workflowConfigurationClass);
        }
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
            queuePropagatedStateTransitions(stateTransition, resource);
        }

        return stateTransition;
    }
    
    public StateTransition getStateTransition(Resource resource, Action action, Comment comment) {
        Resource operative = resourceService.getOperativeResource(resource, action);
        List<StateTransition> potentialStateTransitions = stateDAO.getStateTransitions(operative, action);
        
        if (potentialStateTransitions.size() > 1) {
            try {
                PrismTransitionEvaluation evaluation = potentialStateTransitions.get(0).getStateTransitionEvaluation();
                return (StateTransition) MethodUtils.invokeMethod(this, evaluation.getMethodName(), new Object[]{operative, comment, evaluation});
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

    public StateTransition getApplicationEvaluatedOutcome(Resource resource, Comment comment, PrismTransitionEvaluation evaluation) {
        return stateDAO.getStateTransition(resource.getState(), comment.getAction(), comment.getTransitionState());
	}

    public StateTransition getApplicationReviewedOutcome(Resource resource, Comment comment, PrismTransitionEvaluation evaluation) {
        PrismState transitionState = PrismState.APPLICATION_REVIEW_PENDING_FEEDBACK;
        if (roleService.getRoleUsers(resource, roleService.getById(PrismRole.APPLICATION_REVIEWER)).size() == 1) {
            transitionState = PrismState.APPLICATION_REVIEW_PENDING_COMPLETION;
        }
        return stateDAO.getStateTransition(resource, evaluation, getById(transitionState));
    }
    
    public StateTransition getApplicationInterviewRsvpedOutcome(Resource resource, Comment comment, PrismTransitionEvaluation evaluation) {
        PrismState transitionState = PrismState.APPLICATION_INTERVIEW_PENDING_AVAILABILITY;
        List<User> interviewees = roleService.getRoleUsers(resource, roleService.getById(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE));
        List<User> interviewers = roleService.getRoleUsers(resource, roleService.getById(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER));
        if ((interviewees.size() + interviewers.size()) == 1) {
            transitionState = PrismState.APPLICATION_INTERVIEW_PENDING_SCHEDULING;
        }
        return stateDAO.getStateTransition(resource, evaluation, getById(transitionState));
    }

    public StateTransition getApplicationSupervisionConfirmedOutcome(Resource resource, Comment comment, PrismTransitionEvaluation evaluation) {
        PrismState transitionState = PrismState.APPLICATION_APPROVAL_PENDING_FEEDBACK;
        List<User> primarySupervisors = roleService.getRoleUsers(resource, roleService.getById(PrismRole.APPLICATION_PRIMARY_SUPERVISOR));
        List<User> secondarySupervisors = roleService.getRoleUsers(resource, roleService.getById(PrismRole.APPLICATION_SECONDARY_SUPERVISOR));
        if ((primarySupervisors.size() + secondarySupervisors.size()) == 1) {
            transitionState = PrismState.APPLICATION_APPROVAL_PENDING_COMPLETION;
        }
        return stateDAO.getStateTransition(resource, evaluation, getById(transitionState));
    }
    
    public StateTransition getApplicationInterviewScheduledOutcome(Resource resource, Comment comment, PrismTransitionEvaluation evaluation) {
        State transitionState;
        DateTime interviewDateTime = comment.getInterviewDateTime();
        if (interviewDateTime != null) {
            if (new DateTime().isAfter(interviewDateTime)) {
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
        return stateDAO.getStateTransition(resource, evaluation, transitionState);
    }
    
    public StateTransition getApplicationInterviewedOutcome(Resource resource, Comment comment, PrismTransitionEvaluation evaluation) {
        PrismState transitionState = PrismState.APPLICATION_INTERVIEW_PENDING_FEEDBACK;
        if (roleService.getRoleUsers(resource, roleService.getById(PrismRole.APPLICATION_INTERVIEWER)).size() == 1) {
            transitionState = PrismState.APPLICATION_INTERVIEW_PENDING_COMPLETION;
        }
        return stateDAO.getStateTransition(resource, evaluation, getById(transitionState));
    }
    
    public StateTransition getInstitutionCreatedOutcome(Resource resource, Comment comment, PrismTransitionEvaluation evaluation) {
        PrismState transitionState = PrismState.INSTITUTION_APPROVAL;
        if (roleService.hasUserRole(resource, comment.getUser(), PrismRole.SYSTEM_ADMINISTRATOR)) {
            transitionState = PrismState.INSTITUTION_APPROVED;
        }
        return stateDAO.getStateTransition(resource, evaluation, getById(transitionState));
    }

    public StateTransition getApplicationEligibilityAssessedOutcome(Resource resource, Comment comment, PrismTransitionEvaluation evaluation) {
        PrismState transitionState = PrismState.APPLICATION_VALIDATION_PENDING_COMPLETION;
        if (comment.isApplicationCreatorEligibilityUncertain()) {
            transitionState = PrismState.APPLICATION_VALIDATION_PENDING_FEEDBACK;
        }
        return stateDAO.getStateTransition(resource, evaluation, getById(transitionState));
    }

    public StateTransition getApplicationExportedOutcome(Resource resource, Comment comment, PrismTransitionEvaluation evaluation) {
        State transitionState = resource.getState();
        StateGroup stateGroup = transitionState.getStateGroup();
        if (comment.getExportError() != null) {
            transitionState = getById(PrismState.valueOf(stateGroup.toString() + "_PENDING_CORRECTION"));
        } else if (comment.getExportResponse() != null && comment.getExportError() == null) {
            transitionState = getById(PrismState.valueOf(stateGroup.toString() + "_COMPLETED"));
        }
        return stateDAO.getStateTransition(resource, evaluation, transitionState);
    }

    public StateTransition getApplicationProcessedOutcome(Resource resource, Comment comment, PrismTransitionEvaluation evaluation) {
        PrismState transitionState = PrismState.valueOf(resource.getState().getId().toString() + "_COMPLETED");
        if (comment.getAction().getId() == PrismAction.APPLICATION_WITHDRAW) {
            transitionState = PrismState.APPLICATION_WITHDRAWN_COMPLETED;
        }
        if (resource.getInstitution().isUclInstitution() && resource.getState().getStateGroup().getId() != PrismStateGroup.APPLICATION_UNSUBMITTED) {
            transitionState = PrismState.valueOf(transitionState.toString().replace("COMPLETED", "PENDING_EXPORT"));
        }
        return stateDAO.getStateTransition(resource, evaluation, getById(transitionState));
    }

    private void queuePropagatedStateTransitions(StateTransition stateTransition, Resource resource) {
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
                        try {
                            Comment comment = new Comment().withResource(resource).withUser(invoker).withAction(action);
                            executeStateTransition(resource, action, comment);
                        } catch (WorkflowEngineException e) {
                            throw new Error(e);
                        }
                    }
                });
            }
        }
    }
}
