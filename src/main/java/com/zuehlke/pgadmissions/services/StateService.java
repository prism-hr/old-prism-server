package com.zuehlke.pgadmissions.services;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.dao.StateDAO;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentApplicationInterviewAppointment;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDurationDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.RoleTransition;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.domain.workflow.StateAction;
import com.zuehlke.pgadmissions.domain.workflow.StateActionAssignment;
import com.zuehlke.pgadmissions.domain.workflow.StateActionNotification;
import com.zuehlke.pgadmissions.domain.workflow.StateDurationConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.StateDurationDefinition;
import com.zuehlke.pgadmissions.domain.workflow.StateGroup;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.domain.workflow.StateTransitionEvaluation;
import com.zuehlke.pgadmissions.domain.workflow.StateTransitionPending;
import com.zuehlke.pgadmissions.dto.StateTransitionPendingDTO;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.rest.representation.resource.ActionRepresentation.NextStateRepresentation;
import com.zuehlke.pgadmissions.utils.ReflectionUtils;

@Service
@Transactional
public class StateService {

    @Autowired
    private StateDAO stateDAO;

    @Autowired
    private ActionService actionService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CustomizationService customizationService;

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

    public State getById(PrismState id) {
        return entityService.getById(State.class, id);
    }

    public StateGroup getStateGroupById(PrismStateGroup stateGroupId) {
        return entityService.getById(StateGroup.class, stateGroupId);
    }

    public StateDurationDefinition getStateDurationDefinitionById(PrismStateDurationDefinition stateDurationDefinitionId) {
        return entityService.getById(StateDurationDefinition.class, stateDurationDefinitionId);
    }

    public StateTransitionEvaluation getStateTransitionEvaluationById(PrismStateTransitionEvaluation stateTransitionEvaluationId) {
        return entityService.getById(StateTransitionEvaluation.class, stateTransitionEvaluationId);
    }

    public List<State> getStates() {
        return entityService.list(State.class);
    }

    public List<State> getConfigurableStates() {
        return stateDAO.getConfigurableStates();
    }

    public List<StateGroup> getStateGroups() {
        return entityService.list(StateGroup.class);
    }

    public List<StateTransitionEvaluation> getStateTransitionEvaluations() {
        return entityService.list(StateTransitionEvaluation.class);
    }

    public StateDurationConfiguration getStateDurationConfiguration(Resource resource, User user, StateDurationDefinition definition) {
        return (StateDurationConfiguration) customizationService.getConfiguration(PrismConfiguration.STATE_DURATION, resource, user, definition);
    }

    public void deleteStateActions() {
        entityService.deleteAll(RoleTransition.class);
        entityService.deleteAll(StateTransition.class);
        entityService.deleteAll(StateActionAssignment.class);
        entityService.deleteAll(StateActionNotification.class);
        entityService.deleteAll(StateAction.class);
    }

    public void deleteObsoleteStateDurations() {
        stateDAO.deleteObseleteStateDurations();
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

    public StateTransition executeStateTransition(Resource resource, Action action, Comment comment) throws DeduplicationException, InstantiationException,
            IllegalAccessException {
        comment.setResource(resource);

        if (action.getActionCategory() == PrismActionCategory.CREATE_RESOURCE) {
            resourceService.persistResource(resource, action);
        }

        commentService.create(comment);
        resource.addComment(comment);

        resourceService.preProcessResource(resource, comment);
        entityService.flush();

        State state = resource.getState();
        StateTransition stateTransition = getStateTransition(resource, action, comment);

        if (stateTransition == null) {
            commentService.recordStateTransition(comment, state, state);
            roleService.executeRoleTransitions(resource, comment);
        } else {
            State transitionState = stateTransition.getTransitionState();
            transitionState = transitionState == null ? state : transitionState;
            state = state == null ? transitionState : state;

            Set<State> stateTerminations = stateTransition.getStateTerminations();
            commentService.recordStateTransition(comment, state, transitionState, stateTerminations);
            resourceService.recordStateTransition(resource, comment, state, transitionState);

            commentService.processComment(comment);
            resourceService.processResource(resource, comment);

            roleService.executeRoleTransitions(resource, comment, stateTransition);

            if (stateTransition.getPropagatedActions().size() > 0) {
                StateTransitionPending transientTransitionPending = new StateTransitionPending().withResource(resource).withStateTransition(stateTransition);
                entityService.getOrCreate(transientTransitionPending);
            }

            notificationService.sendWorkflowNotifications(resource, comment);
        }

        commentService.postProcessComment(comment);
        resourceService.postProcessResource(resource, comment);

        return stateTransition;
    }

    public StateTransition getStateTransition(Resource resource, Action action, Comment comment) {
        Resource operative = resourceService.getOperativeResource(resource, action);

        List<StateTransition> potentialStateTransitions = getPotentialStateTransitions(operative, action);

        if (potentialStateTransitions.size() > 1) {
            PrismStateTransitionEvaluation transitionEvaluation = potentialStateTransitions.get(0).getStateTransitionEvaluation().getId();
            return (StateTransition) ReflectionUtils.invokeMethod(this, ReflectionUtils.getMethodName(transitionEvaluation), operative, comment);
        }

        return potentialStateTransitions.isEmpty() ? null : potentialStateTransitions.get(0);
    }

    public StateTransition getApplicationCompletedOutcome(Resource resource, Comment comment) {
        PrismState transitionStateId = PrismState.APPLICATION_VALIDATION;
        LocalDate closingDate = resource.getApplication().getClosingDate();
        if (closingDate == null || closingDate.isBefore(new LocalDate())) {
            transitionStateId = PrismState.APPLICATION_VALIDATION_PENDING_COMPLETION;
        }
        return stateDAO.getStateTransition(resource, comment.getAction(), transitionStateId);
    }

    public StateTransition getApplicationStateCompletedOutcome(Resource resource, Comment comment) {
        PrismState transitionStateId = comment.getTransitionState().getId();
        return stateDAO.getStateTransition(resource, comment.getAction(), transitionStateId);
    }

    public StateTransition getApplicationReviewedOutcome(Resource resource, Comment comment) {
        PrismState transitionState = PrismState.APPLICATION_REVIEW_PENDING_FEEDBACK;
        if (roleService.getRoleUsers(resource, roleService.getById(PrismRole.APPLICATION_REVIEWER)).size() == 1) {
            transitionState = PrismState.APPLICATION_REVIEW_PENDING_COMPLETION;
        }
        return stateDAO.getStateTransition(resource, comment.getAction(), transitionState);
    }

    public StateTransition getApplicationInterviewRsvpedOutcome(Resource resource, Comment comment) {
        PrismState transitionStateId = PrismState.APPLICATION_INTERVIEW_PENDING_AVAILABILITY;
        List<User> interviewees = roleService.getRoleUsers(resource, roleService.getById(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE));
        List<User> interviewers = roleService.getRoleUsers(resource, roleService.getById(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER));
        if ((interviewees.size() + interviewers.size()) == 1) {
            transitionStateId = PrismState.APPLICATION_INTERVIEW_PENDING_SCHEDULING;
        }
        return stateDAO.getStateTransition(resource, comment.getAction(), transitionStateId);
    }

    public StateTransition getApplicationSupervisionConfirmedOutcome(Resource resource, Comment comment) {
        PrismState transitionStateId = PrismState.APPLICATION_APPROVAL_PENDING_FEEDBACK;
        List<User> primarySupervisors = roleService.getRoleUsers(resource, roleService.getById(PrismRole.APPLICATION_PRIMARY_SUPERVISOR));
        List<User> secondarySupervisors = roleService.getRoleUsers(resource, roleService.getById(PrismRole.APPLICATION_SECONDARY_SUPERVISOR));
        if ((primarySupervisors.size() + secondarySupervisors.size()) == 1) {
            transitionStateId = PrismState.APPLICATION_APPROVAL_PENDING_COMPLETION;
        }
        return stateDAO.getStateTransition(resource, comment.getAction(), transitionStateId);
    }

    public StateTransition getApplicationAssignedRecruiterOutcome(Resource resource, Comment comment) {
        PrismState stateId = resource.getState().getId();
        Set<CommentAssignedUser> assignedUsers = comment.getAssignedUsers();
        CommentAssignedUser firstAssignee = assignedUsers.isEmpty() ? null : assignedUsers.iterator().next();
        if (Arrays.asList(PrismState.APPLICATION_REVIEW, PrismState.APPLICATION_INTERVIEW, PrismState.APPLICATION_APPROVAL).contains(stateId)
                && firstAssignee != null && firstAssignee.getRole().getId() == PrismRole.APPLICATION_ADMINISTRATOR
                && firstAssignee.getRoleTransitionType() == PrismRoleTransitionType.CREATE) {
            return stateDAO.getStateTransition(resource, comment.getAction(), stateId);
        } else if (Arrays.asList(PrismState.APPLICATION_REVIEW, PrismState.APPLICATION_APPROVAL).contains(stateId)) {
            return stateDAO.getStateTransition(resource, comment.getAction(), PrismState.valueOf(stateId.name() + "_PENDING_FEEDBACK"));
        } else {
            return getInterviewerAssignedOutcome(resource, comment);
        }
    }

    public StateTransition getApplicationInterviewedOutcome(Resource resource, Comment comment) {
        PrismState transitionStateId = PrismState.APPLICATION_INTERVIEW_PENDING_FEEDBACK;
        if (roleService.getRoleUsers(resource, roleService.getById(PrismRole.APPLICATION_INTERVIEWER)).size() == 1) {
            transitionStateId = PrismState.APPLICATION_INTERVIEW_PENDING_COMPLETION;
        }
        return stateDAO.getStateTransition(resource, comment.getAction(), transitionStateId);
    }

    public StateTransition getInstitutionCreatedOutcome(Resource resource, Comment comment) {
        PrismState transitionStateId = PrismState.INSTITUTION_APPROVAL;
        if (roleService.hasUserRole(resource, comment.getUser(), PrismRole.SYSTEM_ADMINISTRATOR)) {
            transitionStateId = PrismState.INSTITUTION_APPROVED;
        }
        return stateDAO.getStateTransition(resource, comment.getAction(), transitionStateId);
    }

    public StateTransition getApplicationExportedOutcome(Resource resource, Comment comment) {
        State currentState = resource.getState();
        PrismState transitionStateId = currentState.getId();
        PrismStateGroup stateGroupId = currentState.getStateGroup().getId();
        if (comment.getExportException() == null) {
            transitionStateId = PrismState.valueOf(stateGroupId.name() + "_COMPLETED");
        } else {
            transitionStateId = PrismState.valueOf(stateGroupId.name() + "_PENDING_CORRECTION");
        }
        return stateDAO.getStateTransition(resource, comment.getAction(), transitionStateId);
    }

    public StateTransition getApplicationProcessedOutcome(Resource resource, Comment comment) {
        PrismAction actionId = comment.getAction().getId();
        if (actionId == PrismAction.APPLICATION_ESCALATE) {
            return getApplicationRejectedOutcome(resource, comment);
        } else if (actionId == PrismAction.APPLICATION_WITHDRAW) {
            return getApplicationWithdrawnOutcome(resource, comment);
        } else {
            return getApplicationFinalizedOutcome(resource, comment);
        }
    }

    public StateTransition getApplicationTerminatedOutcome(Resource resource, Comment comment) {
        PrismState transitionStateId = PrismState.APPLICATION_REJECTED_COMPLETED;
        PrismStateGroup stateGroupId = resource.getState().getStateGroup().getId();
        if (stateGroupId == PrismStateGroup.APPLICATION_UNSUBMITTED) {
            transitionStateId = PrismState.APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED;
        } else if (resource.getInstitution().getUclInstitution()) {
            transitionStateId = PrismState.APPLICATION_REJECTED_PENDING_EXPORT;
        }
        return stateDAO.getStateTransition(resource, comment.getAction(), transitionStateId);
    }

    public StateTransition getApplicationVerifiedOutcome(Resource resource, Comment comment) {
        PrismStateGroup stateGroupId = resource.getState().getStateGroup().getId();
        if (stateGroupId == PrismStateGroup.APPLICATION_VERIFICATION) {
            return stateDAO.getStateTransition(resource, comment.getAction(), PrismState.APPLICATION_VERIFICATION_PENDING_COMPLETION);
        } else {
            return stateDAO.getStateTransition(resource, comment.getAction(), null);
        }
    }

    public StateTransition getApplicationVerificationCompletedOutcome(Resource resource, Comment comment) {
        PrismStateGroup stateGroupId = resource.getState().getStateGroup().getId();
        if (stateGroupId == PrismStateGroup.APPLICATION_VERIFICATION) {
            return getApplicationStateCompletedOutcome(resource, comment);
        } else {
            return stateDAO.getStateTransition(resource, comment.getAction(), null);
        }
    }

    public StateTransition getApplicationReferencedOutcome(Resource resource, Comment comment) throws DeduplicationException {
        PrismState transitionState = PrismState.APPLICATION_REFERENCE;
        if (roleService.getRoleUsers(resource, roleService.getById(PrismRole.APPLICATION_REFEREE)).size() == 1) {
            transitionState = PrismState.APPLICATION_REFERENCE_PENDING_COMPLETION;
        }
        return stateDAO.getStateTransition(resource, comment.getAction(), transitionState);
    }

    public StateTransition getApplicationReferenceCompletedOutcome(Resource resource, Comment comment) {
        PrismStateGroup stateGroupId = resource.getState().getStateGroup().getId();
        if (stateGroupId == PrismStateGroup.APPLICATION_REFERENCE) {
            return getApplicationStateCompletedOutcome(resource, comment);
        } else {
            return stateDAO.getStateTransition(resource, comment.getAction(), null);
        }
    }

    public StateTransition getProgramCreatedOutcome(Resource resource, Comment comment) {
        PrismState transitionStateId = PrismState.PROGRAM_APPROVAL;
        if (roleService.hasUserRole(resource, comment.getUser(), PrismRole.INSTITUTION_ADMINISTRATOR)) {
            transitionStateId = PrismState.PROGRAM_APPROVED;
        }
        return stateDAO.getStateTransition(resource, comment.getAction(), transitionStateId);
    }

    public StateTransition getProjectCreatedOutcome(Resource resource, Comment comment) {
        PrismState transitionStateId = PrismState.PROJECT_APPROVAL;
        if (roleService.hasAnyUserRole(resource, comment.getUser(), PrismRole.INSTITUTION_ADMINISTRATOR, PrismRole.PROGRAM_ADMINISTRATOR)) {
            transitionStateId = PrismState.PROJECT_APPROVED;
        }
        return stateDAO.getStateTransition(resource, comment.getAction(), transitionStateId);
    }

    public StateTransition getApplicationRecruitedOutcome(Resource resource, Comment comment) {
        Comment recruitedComment = commentService.getEarliestComment((ResourceParent) resource, Application.class,
                PrismAction.APPLICATION_CONFIRM_OFFER_RECOMMENDATION);
        State parentResourceTransitionState = recruitedComment.getParentResourceTransitionState();

        if (parentResourceTransitionState == null) {
            return stateDAO.getStateTransition(resource, comment.getAction(), resource.getState().getId());
        } else {
            return stateDAO.getStateTransition(resource, comment.getAction(), recruitedComment.getParentResourceTransitionState().getId());
        }
    }

    public StateTransition getInstitutionApprovedOutcome(Resource resource, Comment comment) {
        return getUserDefinedNextState(resource, comment);
    }

    public StateTransition getProgramApprovedOutcome(Resource resource, Comment comment) {
        return getUserDefinedNextState(resource, comment);
    }

    public StateTransition getProjectApprovedOutcome(Resource resource, Comment comment) {
        return getUserDefinedNextState(resource, comment);
    }

    public StateTransition getInstitutionViewEditOutcome(Resource resource, Comment comment) {
        return getViewEditNextState(resource, comment);
    }

    public StateTransition getProgramViewEditOutcome(Resource resource, Comment comment) {
        return getViewEditNextState(resource, comment);
    }

    public StateTransition getProgramEscalatedOutcome(Resource resource, Comment comment) {
        if (BooleanUtils.isTrue(resource.getProgram().getImported())) {
            return stateDAO.getStateTransition(resource, comment.getAction(), PrismState.PROGRAM_DISABLED_PENDING_REACTIVATION);
        } else {
            return stateDAO.getStateTransition(resource, comment.getAction(), PrismState.PROGRAM_DISABLED_COMPLETED);
        }
    }

    public StateTransition getProjectViewEditOutcome(Resource resource, Comment comment) {
        return getViewEditNextState(resource, comment);
    }

    public <T extends Resource> void executeDeferredStateTransition(Class<T> resourceClass, Integer resourceId, PrismAction actionId)
            throws DeduplicationException, InstantiationException, IllegalAccessException {
        Resource resource = resourceService.getById(resourceClass, resourceId);
        Action action = actionService.getById(actionId);
        Comment comment = new Comment().withResource(resource).withUser(systemService.getSystem().getUser()).withAction(action).withDeclinedResponse(false)
                .withCreatedTimestamp(new DateTime());
        executeStateTransition(resource, action, comment);
    }

    public void deleteStateTransitionPending(Integer stateTransitionPendingId) {
        StateTransitionPending pending = entityService.getById(StateTransitionPending.class, stateTransitionPendingId);
        entityService.delete(pending);
    }

    public List<PrismState> getStatesByStateGroup(PrismStateGroup stateGroupId) {
        return stateDAO.getStatesByStateGroup(stateGroupId);
    }

    public List<PrismState> getActiveProgramStates() {
        return stateDAO.getActiveProgramStates();
    }

    public List<PrismState> getActiveProjectStates() {
        return stateDAO.getActiveProjectStates();
    }

    public List<State> getCurrentStates(Resource resource) {
        return stateDAO.getCurrentStates(resource);
    }

    public List<PrismState> getRecommendedNextStates(Resource resource) {
        List<PrismState> recommendations = Lists.newLinkedList();
        String recommendationTokens = stateDAO.getRecommendedNextStates(resource);
        if (recommendationTokens != null) {
            for (String recommendationToken : recommendationTokens.split("\\|")) {
                recommendations.add(PrismState.valueOf(recommendationToken));
            }
            return recommendations;
        }
        return null;
    }

    public List<PrismStateGroup> getSecondaryResourceStateGroups(Resource resource) {
        return getSecondaryResourceStateGroups(resource.getResourceScope(), resource.getId());
    }

    public List<PrismStateGroup> getSecondaryResourceStateGroups(PrismScope resourceScope, Integer resourceId) {
        return stateDAO.getSecondaryResourceStateGroups(resourceScope, resourceId);
    }

    public List<NextStateRepresentation> getSelectableTransitionStates(State state, boolean importedResource) {
        return stateDAO.getSelectableTransitionStates(state, importedResource);
    }

    public List<NextStateRepresentation> getSelectableTransitionStates(State state, PrismAction actionId, boolean importedResource) {
        return stateDAO.getSelectableTransitionStates(state, actionId, importedResource);
    }

    private StateTransition getViewEditNextState(Resource resource, Comment comment) {
        if (comment.getTransitionState() == null) {
            return stateDAO.getStateTransition(resource, comment.getAction(), resource.getState().getId());
        }
        return getUserDefinedNextState(resource, comment);
    }

    private StateTransition getUserDefinedNextState(Resource resource, Comment comment) {
        if (comment.getTransitionState() == null) {
            return null;
        }
        PrismState transitionStateId = comment.getTransitionState().getId();
        return stateDAO.getStateTransition(resource, comment.getAction(), transitionStateId);
    }

    private List<StateTransition> getPotentialStateTransitions(Resource resource, Action action) {
        if (action.getActionType() == PrismActionType.USER_INVOCATION) {
            Map<SimpleEntry<Action, State>, StateTransition> potentialStateTransitions = Maps.newLinkedHashMap();
            for (State state : stateDAO.getResourceStates(resource)) {
                for (StateTransition stateTransition : stateDAO.getStateTransitions(state, action)) {
                    SimpleEntry<Action, State> key = stateTransition.getStateTransitionEvaluation() == null ? new SimpleEntry<Action, State>(action, null)
                            : new SimpleEntry<Action, State>(action, stateTransition.getTransitionState());
                    if (!potentialStateTransitions.containsKey(key)) {
                        potentialStateTransitions.put(key, stateTransition);
                    }
                }
            }
            return Lists.newLinkedList(potentialStateTransitions.values());
        } else {
            return Lists.newLinkedList(stateDAO.getStateTransitions(resource.getState(), action));
        }
    }

    private StateTransition getApplicationRejectedOutcome(Resource resource, Comment comment) {
        if (BooleanUtils.isTrue(resource.getInstitution().getUclInstitution())) {
            return stateDAO.getStateTransition(resource, comment.getAction(), PrismState.APPLICATION_REJECTED_PENDING_EXPORT);
        } else {
            return stateDAO.getStateTransition(resource, comment.getAction(), PrismState.APPLICATION_REJECTED_COMPLETED);
        }
    }

    private StateTransition getApplicationWithdrawnOutcome(Resource resource, Comment comment) {
        if (BooleanUtils.isTrue(resource.getInstitution().getUclInstitution())) {
            return stateDAO.getStateTransition(resource, comment.getAction(), PrismState.APPLICATION_WITHDRAWN_PENDING_EXPORT);
        } else {
            return stateDAO.getStateTransition(resource, comment.getAction(), PrismState.APPLICATION_WITHDRAWN_COMPLETED);
        }
    }

    private StateTransition getApplicationFinalizedOutcome(Resource resource, Comment comment) {
        if (BooleanUtils.isTrue(resource.getInstitution().getUclInstitution())) {
            return stateDAO.getStateTransition(resource, comment.getAction(), PrismState.valueOf(resource.getState().getId().name() + "_PENDING_EXPORT"));
        } else {
            return stateDAO.getStateTransition(resource, comment.getAction(), PrismState.valueOf(resource.getState().getId().name() + "_COMPLETED"));
        }
    }

    private StateTransition getInterviewerAssignedOutcome(Resource resource, Comment comment) {
        CommentApplicationInterviewAppointment interviewAppointment = comment.getInterviewAppointment();
        LocalDateTime interviewDateTime = interviewAppointment == null ? null : interviewAppointment.getInterviewDateTime();
        if (interviewDateTime != null) {
            DateTime interviewZonedDateTime = interviewDateTime.toDateTime(DateTimeZone.forTimeZone(interviewAppointment.getInterviewTimeZone()));
            if (new DateTime().isAfter(interviewZonedDateTime)) {
                return stateDAO.getStateTransition(resource, comment.getAction(), PrismState.APPLICATION_INTERVIEW_PENDING_FEEDBACK);
            } else {
                return stateDAO.getStateTransition(resource, comment.getAction(), PrismState.APPLICATION_INTERVIEW_PENDING_INTERVIEW);
            }
        } else {
            if (resource.getState().getId() == PrismState.APPLICATION_INTERVIEW) {
                return stateDAO.getStateTransition(resource, comment.getAction(), PrismState.APPLICATION_INTERVIEW_PENDING_AVAILABILITY);
            } else {
                return stateDAO.getStateTransition(resource, comment.getAction(), PrismState.APPLICATION_INTERVIEW);
            }
        }
    }

}
