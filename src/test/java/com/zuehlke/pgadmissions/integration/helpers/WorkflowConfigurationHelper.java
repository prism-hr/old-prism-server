package com.zuehlke.pgadmissions.integration.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.RoleTransition;
import com.zuehlke.pgadmissions.domain.Scope;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.StateActionAssignment;
import com.zuehlke.pgadmissions.domain.StateActionNotification;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismTransitionEvaluation;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ScopeService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.services.SystemService;

@Service
@Transactional
public class WorkflowConfigurationHelper {

    private final Set<State> statesVisited = Sets.newHashSet();

    private final HashMultimap<PrismScope, PrismScope> actualChildScopes = HashMultimap.create();
    
    private final HashMultimap<PrismScope, PrismScope> actualParentScopes = HashMultimap.create();

    private final Set<PrismRole> actualRolesCreated = Sets.newHashSet();

    private final Set<PrismState> actualInitialStates = Sets.newHashSet();

    private final Set<PrismState> actualFinalStates = Sets.newHashSet();

    private final Set<PrismAction> actualCreationActions = Sets.newHashSet();
    
    private final Set<PrismAction> actualEscalationActions = Sets.newHashSet();
    
    private final Set<PrismAction> actualPropagationActions = Sets.newHashSet();
    
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

        List<State> workflowStates = stateService.getWorkflowStates();
        assertEquals(workflowStates.size(), statesVisited.size());
        assertCollectionEquals(PrismScope.getCreatableScopes(), actualParentScopes.keySet());
        assertCollectionEquals(PrismAction.getCreationActions(), actualCreationActions);
        assertCollectionEquals(PrismState.getInitialStates(), actualInitialStates);
        assertCollectionEquals(PrismState.getFinalStates(), actualFinalStates);
        
        List<PrismState> escalatableStates = stateService.getActionableStates(actualEscalationActions);
        List<PrismState> propagatableStates = stateService.getActionableStates(actualPropagationActions);
        
        Set<PrismState> actualTransitionStates = Sets.newHashSet(escalatableStates);
        actualTransitionStates.addAll(propagatableStates);
        
        assertTrue(workflowStates.size() == actualTransitionStates.size() + actualFinalStates.size());
        
        statesVisited.clear();
        actualChildScopes.clear();
        actualRolesCreated.clear();
        actualInitialStates.clear();
        actualFinalStates.clear();
        actualCreationActions.clear();
        actualEscalationActions.clear();
        actualPropagationActions.clear();
    }

    private void verifyState(State state) {
        if (state == null) {
            state = verifyRootState();
        }

        statesVisited.add(state);
        assertTrue(state.getSequenceOrder() == null || state == state.getParentState());

        verifyAsInitialState(state);
        verifyAsFinalState(state);

        verifyStateActions(state);
        verifyStateActionAssignments(state);
        verifyStateActionNotifications(state);

        for (State transitionState : stateService.getOrderedTransitionStates(state, statesVisited.toArray(new State[0]))) {
            verifyState(transitionState);
        }
    }

    private State verifyRootState() {
        List<State> potentialRootStates = stateService.getRootState();
        assertTrue(potentialRootStates.size() == 1);

        State rootState = potentialRootStates.get(0);
        assertTrue(rootState.getScope().getPrecedence() == 1);

        List<State> upstreamStates = stateService.getUpstreamStates(rootState);
        assertTrue(upstreamStates.size() <= 1);

        if (upstreamStates.size() == 1) {
            assertEquals(rootState.getId(), upstreamStates.get(0).getId());
        }

        return rootState;
    }

    private void verifyAsInitialState(State state) {
        if (state.isInitialState()) {
            Scope scope = state.getScope();
            Set<Scope> parentScopes = Sets.newHashSet();

            for (State upstreamState : stateService.getUpstreamStates(state)) {
                Scope upstreamScope = upstreamState.getScope();
                if (scope != upstreamScope) {
                    parentScopes.add(upstreamScope);
                    actualChildScopes.put(upstreamScope.getId(), scope.getId());
                    actualParentScopes.put(scope.getId(), upstreamScope.getId());
                }
            }

            assertTrue(parentScopes.size() > 0 || state.isFinalState());

            for (Scope parentScope : parentScopes) {
                assertTrue(parentScope.getPrecedence() > scope.getPrecedence());
            }

            actualInitialStates.add(state.getId());
        }
    }

    private void verifyAsFinalState(State state) {
        if (state.isFinalState()) {
            List<State> downstreamStates = stateService.getDownstreamStates(state);
            
            assertTrue(downstreamStates.size() <= 1);
            
            for (State downstreamState : downstreamStates) {
                assertEquals(state, downstreamState);
            }

            actualFinalStates.add(state.getId());
        }
    }

    private void verifyStateActions(State state) {
        Set<PrismAction> escalationActions = Sets.newHashSet();

        for (StateAction stateAction : state.getStateActions()) {
            Action action = stateAction.getAction();
            assertEquals(state.getScope(), action.getScope());

            if (action.getActionType().isSystemAction()) {
                assertNotSame(stateAction.getState(), stateAction.getStateTransitions().iterator().next());
                assertFalse(stateAction.isRaisesUrgentFlag());
                assertNull(stateAction.getNotificationTemplate());
            }

            if (stateAction.isRaisesUrgentFlag()) {
                assertNotNull(stateAction.getNotificationTemplate());
            }

            if (action.isCreationAction()) {
                actualCreationActions.add(action.getId());
            }

            if (action.getActionType() == PrismActionType.SYSTEM_ESCALATION) {
                escalationActions.add(action.getId());
            }

            verifyStateTransitions(stateAction);
        }

        if (systemService.getStateDuration(state) != null) {
            assertFalse(escalationActions.isEmpty());
        }
        
        actualEscalationActions.addAll(escalationActions);
    }

    private void verifyStateTransitions(StateAction stateAction) {
        State state = stateAction.getState();
        Action action = stateAction.getAction();

        PrismTransitionEvaluation lastTransitionEvaluation = null;
        Set<StateTransition> stateTransitions = stateAction.getStateTransitions();
        int stateTransitionCount = stateTransitions.size();

        for (StateTransition stateTransition : stateTransitions) {
            PrismTransitionEvaluation thisTransitionEvaluation = stateTransition.getStateTransitionEvaluation();

            if (stateTransitionCount == 1) {
                assertNull(thisTransitionEvaluation);
            } else {
                assertTrue(lastTransitionEvaluation == null || lastTransitionEvaluation == thisTransitionEvaluation);
            }

            State transitionState = stateTransition.getTransitionState();
            assertTrue(state.getScope() == transitionState.getScope() || action.getCreationScope() == transitionState.getScope());

            lastTransitionEvaluation = thisTransitionEvaluation;
            verifyRoleTransitions(stateTransition);
            verifyPropagatedActions(stateTransition);
        }
    }

    private void verifyRoleTransitions(StateTransition stateTransition) {
        Set<PrismRole> actualOwnerRoles = Sets.newHashSet();
        Set<PrismRole> actualProcessedRoles = Sets.newHashSet();

        State transitionState = stateTransition.getTransitionState();
        
        for (RoleTransition roleTransition : stateTransition.getRoleTransitions()) {
            Role transitionRole = roleTransition.getTransitionRole();
            PrismRole transitionRoleId = transitionRole.getId();

            actualProcessedRoles.add(transitionRoleId);
            PrismRoleTransitionType roleTransitionType = roleTransition.getRoleTransitionType();

            if (roleTransitionType != PrismRoleTransitionType.REMOVE) {
                actualRolesCreated.add(transitionRoleId);
                
                if (transitionRole.isScopeOwner() && roleTransitionType == PrismRoleTransitionType.CREATE) {
                    assertEquals(transitionState.getScope(), transitionRole.getScope());
                    actualOwnerRoles.add(transitionRoleId);
                }
            }
        }

        assertTrue(actualRolesCreated.containsAll(actualProcessedRoles));

        Action action = stateTransition.getStateAction().getAction();
        if (action.isCreationAction()) {
            Set<PrismRole> expectedOwnerRoles = PrismRole.getScopeOwners(action.getCreationScope().getId());
            assertCollectionEquals(expectedOwnerRoles, actualOwnerRoles);
        }
    }
    
    private void verifyPropagatedActions(StateTransition stateTransition) {
        Scope propagatingScope = stateTransition.getStateAction().getState().getScope();
        
        Set<PrismScope> parentScopes = actualParentScopes.get(propagatingScope.getId());
        Set<PrismScope> childScopes = actualChildScopes.get(propagatingScope.getId());
        
        for (Action propagatedAction : stateTransition.getPropagatedActions()) {
            Scope actionScope = propagatedAction.getScope();
            if (actionScope.getPrecedence() < propagatingScope.getPrecedence()) {
                assertTrue(childScopes.contains(actionScope.getId()));
            } else {
                assertTrue(parentScopes.contains(actionScope.getId()));
            }
            
            assertTrue(actionService.getCreationActions(stateTransition.getTransitionState(), actionScope).isEmpty());
            actualPropagationActions.add(propagatedAction.getId());
        }
        
    }

    private void verifyStateActionAssignments(State state) {
        for (StateAction stateAction : state.getStateActions()) {
            Set<StateActionAssignment> assignments = stateAction.getStateActionAssignments();

            if (stateAction.getAction().isSystemAction()) {
                assertTrue(assignments.size() == 0);
            }

            for (StateActionAssignment assignment : assignments) {
                Role assignedRole = assignment.getRole();
                assertTrue(assignedRole.getScope().getPrecedence() >= state.getScope().getPrecedence());
                assertTrue(actualRolesCreated.contains(assignedRole.getId()));
            }
        }
    }

    private void verifyStateActionNotifications(State state) {
        for (StateAction stateAction : state.getStateActions()) {
            
            for (StateActionNotification notification : stateAction.getStateActionNotifications()) {
                Scope templateScope = notification.getNotificationTemplate().getScope();
                assertTrue(state.getScope() == templateScope || stateAction.getAction().getCreationScope() == templateScope);
                assertTrue(actualRolesCreated.contains(notification.getRole().getId()));
            }
        }
    }

    private <T> void assertCollectionEquals(Collection<T> expectedCollection, Collection<T> actualCollection) {
        assertEquals(expectedCollection.size(), actualCollection.size());
        assertTrue(actualCollection.containsAll(expectedCollection));
    }

}
