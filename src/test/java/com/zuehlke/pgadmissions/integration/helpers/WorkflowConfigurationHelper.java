package com.zuehlke.pgadmissions.integration.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.RoleTransition;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.StateActionAssignment;
import com.zuehlke.pgadmissions.domain.StateActionNotification;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.StateTransitionEvaluation;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ScopeService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.services.SystemService;

@Service
@Scope("prototype")
@Transactional
public class WorkflowConfigurationHelper {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Set<State> statesVisited = Sets.newHashSet();

    private final HashMultimap<PrismScope, PrismScope> actualChildScopes = HashMultimap.create();

    private final HashMultimap<PrismScope, PrismScope> actualParentScopes = HashMultimap.create();

    private final Set<PrismRole> actualRolesCreated = Sets.newHashSet();

    private final HashMultimap<PrismScope, PrismRole> actualCreatorRoles = HashMultimap.create();

    private final Set<StateTransition> propagatingStateTransitions = Sets.newHashSet();

    private final HashMultimap<PrismState, AbstractMap.SimpleEntry<PrismRole, PrismRole>> actualRoleExclusions = HashMultimap.create();

    @Autowired
    private ActionService actionService;

    @Autowired
    private ScopeService scopeService;

    @Autowired
    private StateService stateService;

    @Autowired
    private SystemService systemService;

    public void verifyWorkflowConfiguration() {
        verifyState(null);

        List<State> workflowStates = stateService.getStates();
        assertEquals(workflowStates.size(), statesVisited.size());

        verifyPropagatedActions();
        verifyCreatorRoles();
        verifyFallbackActions();
    }

    private void verifyState(State state) {
        if (state == null) {
            state = stateService.getById(PrismState.SYSTEM_RUNNING);
        }
        
        statesVisited.add(state);
        logger.info("Verifying state: " + state.getId().toString());
        assertEquals(state.getScope(), state.getStateGroup().getScope());

        verifyStateActions(state);
        verifyStateActionAssignments(state);
        verifyStateActionNotifications(state);
        verifyRoleTransitionExclusions(state);

        for (State transitionState : stateService.getOrderedTransitionStates(state, statesVisited.toArray(new State[statesVisited.size()]))) {
            verifyTransitionState(state, transitionState);
            verifyState(transitionState);
        }
    }

    private void verifyTransitionState(State state, State transitionState) {
        statesVisited.add(transitionState);
        
        int statePrecedence = state.getScope().getPrecedence();
        int transitionStatePrecedence = transitionState.getScope().getPrecedence();

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
        Set<Action> userDefaultActions = Sets.newHashSet();
        Set<Action> systemDefaultActions = Sets.newHashSet();
        Set<Action> viewEditActions = Sets.newHashSet();

        for (StateAction stateAction : state.getStateActions()) {
            Action action = stateAction.getAction();

            logger.info("Verifying action: " + action.getId().toString());
            assertEquals(state.getScope(), action.getScope());

            PrismActionCategory actionCategory = action.getActionCategory();

            if (action.getActionType() == PrismActionType.SYSTEM_INVOCATION) {
                assertNotSame(stateAction.getState(), stateAction.getStateTransitions().iterator().next());
                assertFalse(stateAction.getRaisesUrgentFlag());
                assertNull(stateAction.getNotificationTemplate());
                assertTrue(stateAction.getStateActionAssignments().isEmpty());
            }

            if (stateAction.getRaisesUrgentFlag()) {
                assertNotNull(stateAction.getNotificationTemplate());
            }

            if (stateAction.isDefaultAction()) {
                if (action.getActionType() == PrismActionType.USER_INVOCATION) {
                    userDefaultActions.add(action);
                } else {
                    systemDefaultActions.add(action);
                }
            }

            if (actionCategory == PrismActionCategory.ESCALATE_RESOURCE) {
                escalationActions.add(action);
            }

            if (actionCategory == PrismActionCategory.VIEW_EDIT_RESOURCE) {
                verifyActionEnhancements(stateAction);
                viewEditActions.add(action);
            }

            verifyStateTransitions(stateAction);
        }

        boolean actionsEmpty = state.getStateActions().isEmpty();     
        assertTrue(actionsEmpty || userDefaultActions.size() == 1);
        assertTrue(systemDefaultActions.size() <= 1);
        assertTrue(actionsEmpty || viewEditActions.size() >= 1);

        if (stateService.getStateDuration(systemService.getSystem(), state) != null) {
            assertFalse(escalationActions.isEmpty());
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

            StateTransitionEvaluation thisTransitionEvaluation = stateTransition.getStateTransitionEvaluation();
            PrismStateTransitionEvaluation thisTransitionEvaluationId = thisTransitionEvaluation == null ? null : thisTransitionEvaluation.getId();

            assertTrue(stateTransition.getRoleTransitions().size() > 0 || state != stateTransition.getTransitionState()
                    || action != stateTransition.getTransitionAction() || thisTransitionEvaluationId != null);

            logger.info("Verifying state transition: " + state.getId().toString() + " "
                    + (thisTransitionEvaluationId == null ? "" : thisTransitionEvaluationId + " ") + stateTransition.getTransitionState().getId().toString());

            if (stateTransitionCount == 1) {
                assertNull(thisTransitionEvaluationId);
            } else {
                assertTrue(lastTransitionEvaluation == null || lastTransitionEvaluation == thisTransitionEvaluationId);
            }

            State transitionState = stateTransition.getTransitionState();
            assertTrue(state.getScope() == transitionState.getScope() || action.getCreationScope() == transitionState.getScope());

            lastTransitionEvaluation = thisTransitionEvaluationId;
            verifyRoleTransitions(stateTransition);

            if (!stateTransition.getPropagatedActions().isEmpty()) {
                propagatingStateTransitions.add(stateTransition);
            }
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
            assertEquals(transitionState.getScope(), transitionRole.getScope());

            if (roleTransitionType != PrismRoleTransitionType.RETIRE) {
                actualRolesCreated.add(transitionRoleId);

                PrismActionCategory actionCategory = action.getActionCategory();
                if (transitionRole.isScopeCreator() && roleTransitionType == PrismRoleTransitionType.CREATE
                        && (actionCategory == PrismActionCategory.CREATE_RESOURCE || actionCategory == PrismActionCategory.INITIALISE_RESOURCE)) {
                    assertTrue(roleTransition.getMinimumPermitted() == 1);
                    assertTrue(roleTransition.getMaximumPermitted() == 1);
                    actualCreatorRoles.put(transitionState.getScope().getId(), transitionRoleId);
                }

                if (roleTransitionType == PrismRoleTransitionType.CREATE || roleTransitionType == PrismRoleTransitionType.BRANCH) {
                    for (Role excludedRole : transitionRole.getExcludedRoles()) {
                        actualRoleExclusions
                                .put(state.getId(), new AbstractMap.SimpleEntry<PrismRole, PrismRole>(transitionRole.getId(), excludedRole.getId()));
                    }
                } else {
                    assertEquals(state.getScope(), role.getScope());
                    assertEquals(state.getScope(), transitionRole.getScope());
                }
            }
        }

        assertTrue(actualRolesCreated.containsAll(actualProcessedRoles));
    }

    private void verifyStateActionAssignments(State state) {
        for (StateAction stateAction : state.getStateActions()) {
            Action action = stateAction.getAction();
            Set<StateActionAssignment> assignments = stateAction.getStateActionAssignments();

            if (action.getActionType() == PrismActionType.SYSTEM_INVOCATION) {
                assertTrue(assignments.size() == 0);
            }

            for (StateActionAssignment assignment : assignments) {
                Role assignedRole = assignment.getRole();
                logger.info("Verifying assignment: " + assignedRole.getId().toString());

                assertTrue(assignedRole.getScope().getPrecedence() <= state.getScope().getPrecedence());
                assertTrue(actualRolesCreated.contains(assignedRole.getId()));
            }
        }
    }

    private void verifyStateActionNotifications(State state) {
        for (StateAction stateAction : state.getStateActions()) {
            for (StateActionNotification notification : stateAction.getStateActionNotifications()) {
                NotificationTemplate template = notification.getNotificationTemplate();
                com.zuehlke.pgadmissions.domain.Scope templateScope = template.getScope();
                logger.info("Verifying notification: " + template.getId().toString());

                assertTrue(state.getScope() == templateScope || templateScope.getId() == PrismScope.SYSTEM
                        || stateAction.getAction().getCreationScope() == templateScope);
                assertTrue(actualRolesCreated.contains(notification.getRole().getId()));
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
            assertTrue(actualRolesCreated.contains(excludedRole));
        }
    }

    private void verifyPropagatedActions() {
        for (StateTransition stateTransition : propagatingStateTransitions) {
            com.zuehlke.pgadmissions.domain.Scope propagatingScope = stateTransition.getStateAction().getState().getScope();

            Set<PrismScope> parentScopes = actualParentScopes.get(propagatingScope.getId());
            Set<PrismScope> childScopes = actualChildScopes.get(propagatingScope.getId());

            for (Action propagatedAction : stateTransition.getPropagatedActions()) {
                logger.info("Verifying propagated action: " + stateTransition.getStateAction().getState().getId().toString() + " "
                        + stateTransition.getStateAction().getAction().getId().toString() + " " + propagatedAction.getId().toString());

                com.zuehlke.pgadmissions.domain.Scope actionScope = propagatedAction.getScope();
                if (actionScope.getPrecedence() > propagatingScope.getPrecedence()) {
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
            assertEquals(PrismScope.SYSTEM, action.getFallbackAction().getScope().getId());
        }
    }

    private <T> void assertCollectionEquals(Collection<T> expectedCollection, Collection<T> actualCollection) {
        assertEquals(expectedCollection.size(), actualCollection.size());
        assertTrue(actualCollection.containsAll(expectedCollection));
    }

}
