package uk.co.alumeni.prism.integration.helpers;

import com.google.common.base.Objects;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Sets;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.alumeni.prism.domain.definitions.workflow.*;
import uk.co.alumeni.prism.domain.resource.System;
import uk.co.alumeni.prism.domain.workflow.*;
import uk.co.alumeni.prism.services.ActionService;
import uk.co.alumeni.prism.services.StateService;
import uk.co.alumeni.prism.services.SystemService;

import java.util.*;

import static org.junit.Assert.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCategory.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionType.BRANCH;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.SYSTEM;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.SYSTEM_RUNNING;

@Service
@Transactional
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class WorkflowConfigurationHelper {

    private static final Logger logger = LoggerFactory.getLogger(WorkflowConfigurationHelper.class);

    private final Set<State> statesVisited = Sets.newHashSet();

    private final HashMultimap<PrismScope, PrismScope> actualChildScopes = HashMultimap.create();

    private final HashMultimap<PrismScope, PrismScope> actualParentScopes = HashMultimap.create();

    private final HashMultimap<PrismScope, PrismRole> actualCreatorRoles = HashMultimap.create();

    private final Set<StateTransition> propagatingStateTransitions = Sets.newHashSet();

    private final HashMultimap<PrismState, AbstractMap.SimpleEntry<PrismRole, PrismRole>> actualRoleExclusions = HashMultimap.create();

    @Autowired
    private ActionService actionService;

    @Autowired
    private StateService stateService;

    @Autowired
    private SystemService systemService;

    public void verifyWorkflowConfiguration() {
        verifyState(null);

        List<State> states = stateService.getStates();
        assertEquals(states.size(), statesVisited.size());

        verifyPropagatedActions();
        verifyCreatorRoles();
        verifyFallbackActions();
    }

    private void verifyState(State state) {
        if (state == null) {
            state = stateService.getById(SYSTEM_RUNNING);
        }

        statesVisited.add(state);
        logger.info("Verifying state: " + state.getId().toString());
        assertEquals(state.getScope(), state.getStateGroup().getScope());

        verifyStateActions(state);
        verifyStateActionAssignments(state);
        verifyRoleTransitionExclusions(state);

        for (State transitionState : stateService.getOrderedTransitionStates(state, statesVisited.toArray(new State[statesVisited.size()]))) {
            verifyTransitionState(state, transitionState);
            verifyState(transitionState);
        }
    }

    private void verifyTransitionState(State state, State transitionState) {
        statesVisited.add(transitionState);

        int statePrecedence = state.getScope().getOrdinal();
        int transitionStatePrecedence = transitionState.getScope().getOrdinal();

        assertTrue(statePrecedence <= transitionStatePrecedence);

        if (statePrecedence != transitionStatePrecedence) {
            PrismScope parentScopeId = state.getScope().getId();
            PrismScope childScopeId = transitionState.getScope().getId();

            actualChildScopes.put(parentScopeId, childScopeId);
            actualParentScopes.put(childScopeId, parentScopeId);
        }
    }

    private void verifyStateActions(State state) {
        Set<Action> escalationActions = Sets.newHashSet();
        Set<Action> viewEditActions = Sets.newHashSet();

        for (StateAction stateAction : state.getStateActions()) {
            Action action = stateAction.getAction();

            logger.info("Verifying action: " + action.getId().toString());
            assertEquals(state.getScope().getId(), action.getScope().getId());

            PrismActionCategory actionCategory = action.getActionCategory();

            if (BooleanUtils.isTrue(action.getSystemInvocationOnly())) {
                assertNotSame(stateAction.getState(), stateAction.getStateTransitions().iterator().next());
                assertFalse(stateAction.getRaisesUrgentFlag());
                assertNull(stateAction.getNotificationDefinition());
                assertTrue(stateAction.getStateActionAssignments().isEmpty());
            }

            Set<PrismScope> assigneeRoleScopes = Sets.newHashSet();
            stateAction.getStateActionAssignments().forEach(stateActionAssignment -> {
                assigneeRoleScopes.add(stateActionAssignment.getRole().getScope().getId());
            });

            if (stateAction.getNotificationDefinition() != null) {
                assertTrue(stateAction.getRaisesUrgentFlag());
            }

            if (actionCategory == ESCALATE_RESOURCE) {
                escalationActions.add(action);
            }

            if (actionCategory == VIEW_EDIT_RESOURCE) {
                verifyActionEnhancements(stateAction);
                viewEditActions.add(action);
            }

            verifyStateTransitions(stateAction);
        }

        boolean actionsEmpty = state.getStateActions().isEmpty();
        assertTrue(actionsEmpty || viewEditActions.size() == 1);

        System system = systemService.getSystem();
        StateDurationDefinition definition = state.getStateDurationDefinition();

        if (definition != null) {
            if (stateService.getStateDurationConfiguration(system, state.getStateDurationDefinition()) != null) {
                assertFalse(escalationActions.isEmpty());
            }
        }
    }

    private void verifyActionEnhancements(StateAction stateAction) {
        Set<PrismActionEnhancement> enhancements = Sets.newHashSet();
        enhancements.add(stateAction.getActionEnhancement());

        for (StateActionAssignment stateActionAssignment : stateAction.getStateActionAssignments()) {
            enhancements.add(stateActionAssignment.getActionEnhancement());
        }

        assertFalse(enhancements.isEmpty());
    }

    private void verifyStateTransitions(StateAction stateAction) {
        State state = stateAction.getState();
        Action action = stateAction.getAction();

        PrismStateTransitionEvaluation lastTransitionEvaluation = null;
        Set<StateTransition> stateTransitions = stateAction.getStateTransitions();
        int stateTransitionCount = stateTransitions.size();

        for (StateTransition stateTransition : stateTransitions) {
            State transitionState = stateTransition.getTransitionState();

            if (transitionState == null) {
                assertTrue(state.getParallelizable());
            } else {
                StateTransitionEvaluation thisTransitionEvaluation = stateTransition.getStateTransitionEvaluation();
                PrismStateTransitionEvaluation thisTransitionEvaluationId = thisTransitionEvaluation == null ? null : thisTransitionEvaluation.getId();

                logger.info("Verifying state transition: " + state.getId().toString() + " "
                        + (thisTransitionEvaluationId == null ? "" : thisTransitionEvaluationId + " ") + transitionState.getId().name());

                if (stateTransitionCount == 1) {
                    assertNull(thisTransitionEvaluationId);
                } else {
                    assertTrue(lastTransitionEvaluation == null || lastTransitionEvaluation == thisTransitionEvaluationId);
                }

                assertTrue(Objects.equal(state.getScope(), transitionState.getScope()) || Objects.equal(action.getCreationScope(), transitionState.getScope())
                        || action.getId().name().contains("_IMPORT_"));

                lastTransitionEvaluation = thisTransitionEvaluationId;

                verifyStateTransitionNotifications(stateTransition);
                verifyRoleTransitions(stateTransition);

                if (!stateTransition.getPropagatedActions().isEmpty()) {
                    propagatingStateTransitions.add(stateTransition);
                }
            }
        }
    }

    private void verifyStateTransitionNotifications(StateTransition stateTransition) {
        for (StateTransitionNotification notification : stateTransition.getStateTransitionNotifications()) {
            NotificationDefinition template = notification.getNotificationDefinition();
            uk.co.alumeni.prism.domain.workflow.Scope templateScope = template.getScope();
            logger.info("Verifying notification: " + template.getId().toString());

            StateAction stateAction = stateTransition.getStateAction();
            assertTrue(stateAction.getState().getScope() == templateScope || templateScope.getId() == SYSTEM
                    || stateAction.getAction().getCreationScope() == templateScope);
        }
    }

    private void verifyRoleTransitions(StateTransition stateTransition) {
        Set<PrismRole> actualProcessedRoles = Sets.newHashSet();

        State state = stateTransition.getStateAction().getState();
        Action action = stateTransition.getStateAction().getAction();
        State transitionState = stateTransition.getTransitionState();

        Set<RoleTransition> roleTransitions = stateTransition.getRoleTransitions();

        for (RoleTransition roleTransition : roleTransitions) {
            Role role = roleTransition.getRole();
            Role transitionRole = roleTransition.getTransitionRole();

            PrismRole transitionRoleId = transitionRole.getId();
            PrismRoleTransitionType roleTransitionType = roleTransition.getRoleTransitionType();
            logger.info("Verifying role transition: " + role.getId().toString() + " " + roleTransitionType + " " + transitionRoleId.toString());

            actualProcessedRoles.add(transitionRoleId);

            PrismActionCategory actionCategory = action.getActionCategory();
            if (transitionRole.getScopeCreator() != null && roleTransitionType == CREATE
                    && (actionCategory == CREATE_RESOURCE || actionCategory == INITIALISE_RESOURCE)) {
                assertTrue(roleTransition.getMinimumPermitted() == 1);
                assertTrue(roleTransition.getMaximumPermitted() == 1);
                actualCreatorRoles.put(transitionState.getScope().getId(), transitionRoleId);
            }

            if (!(roleTransitionType == CREATE || roleTransitionType == BRANCH)) {
                assertEquals(state.getScope(), role.getScope());
                assertEquals(state.getScope(), transitionRole.getScope());
            }
        }
    }

    private void verifyStateActionAssignments(State state) {
        for (StateAction stateAction : state.getStateActions()) {
            Action action = stateAction.getAction();
            Set<StateActionAssignment> assignments = stateAction.getStateActionAssignments();

            if (BooleanUtils.isTrue(action.getSystemInvocationOnly())) {
                assertTrue(assignments.size() == 0);
            }

            for (StateActionAssignment assignment : assignments) {
                Role assignedRole = assignment.getRole();
                logger.info("Verifying assignment: " + assignedRole.getId().toString());

                if (BooleanUtils.isFalse(assignment.getExternalMode())) {
                    assertTrue(assignedRole.getScope().getOrdinal() <= state.getScope().getOrdinal());
                }
            }
        }
    }

    private void verifyRoleTransitionExclusions(State state) {
        for (AbstractMap.SimpleEntry<PrismRole, PrismRole> roleExclusion : actualRoleExclusions.get(state.getId())) {
            PrismRole role = roleExclusion.getKey();
            PrismRole excludedRole = roleExclusion.getValue();

            logger.info("Verifying role transition exclusion: " + role + " " + excludedRole);
            assertEquals(role.getScope(), excludedRole.getScope());
            assertNotSame(role, excludedRole);
        }
    }

    private void verifyPropagatedActions() {
        for (StateTransition stateTransition : propagatingStateTransitions) {
            uk.co.alumeni.prism.domain.workflow.Scope propagatingScope = stateTransition.getStateAction().getState().getScope();

            Set<PrismScope> parentScopes = actualParentScopes.get(propagatingScope.getId());
            Set<PrismScope> childScopes = actualChildScopes.get(propagatingScope.getId());

            for (Action propagatedAction : stateTransition.getPropagatedActions()) {
                logger.info("Verifying propagated action: " + stateTransition.getStateAction().getState().getId().toString() + " "
                        + stateTransition.getStateAction().getAction().getId().toString() + " " + propagatedAction.getId().toString());

                uk.co.alumeni.prism.domain.workflow.Scope actionScope = propagatedAction.getScope();
                if (actionScope.getOrdinal() > propagatingScope.getOrdinal()) {
                    assertTrue(childScopes.contains(actionScope.getId()));
                } else {
                    assertTrue(parentScopes.contains(actionScope.getId()));
                }
            }
        }
    }

    private void verifyCreatorRoles() {
        Set<PrismScope> actualScopes = actualCreatorRoles.keySet();
        assertCollectionEquals(Arrays.asList(PrismScope.values()), actualScopes);

        for (PrismScope scope : actualScopes) {
            assertEquals(1, actualCreatorRoles.get(scope).size());
        }
    }

    private void verifyFallbackActions() {
        for (Action action : actionService.getActions()) {
            assertEquals(SYSTEM, action.getFallbackAction().getScope().getId());
        }
    }

    private <T> void assertCollectionEquals(Collection<T> expectedCollection, Collection<T> actualCollection) {
        assertEquals(expectedCollection.size(), actualCollection.size());
        assertTrue(actualCollection.containsAll(expectedCollection));
    }

}
