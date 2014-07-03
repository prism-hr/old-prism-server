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
import com.zuehlke.pgadmissions.services.ScopeService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.services.SystemService;

@Service
@Transactional
public class WorkflowConfigurationHelper {

    private final Set<State> statesVisited = Sets.newHashSet();
    
    private final Set<State> actualEscalationStates = Sets.newHashSet();

    private final Set<PrismRole> actualRolesCreated = Sets.newHashSet();

    private final Set<PrismScope> actualCreatableScopes = Sets.newHashSet();

    private final Set<PrismState> actualInitialStates = Sets.newHashSet();

    private final Set<PrismState> actualFinalStates = Sets.newHashSet();

    private final Set<PrismAction> actualCreationActions = Sets.newHashSet();

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
        assertEquals(workflowStates.size(), actualEscalationStates.size() + actualFinalStates.size());

        assertCollectionEquals(PrismScope.getCreatableScopes(), actualCreatableScopes);
        assertCollectionEquals(PrismAction.getCreationActions(), actualCreationActions);
        assertCollectionEquals(PrismState.getInitialStates(), actualInitialStates);
        assertCollectionEquals(PrismState.getFinalStates(), actualFinalStates);
        
        statesVisited.clear();
        actualEscalationStates.clear();
        actualRolesCreated.clear();
        actualCreatableScopes.clear();
        actualInitialStates.clear();
        actualFinalStates.clear();
        actualCreationActions.clear();
    }

    private void verifyState(State state) {
        if (state == null) {
            state = getRootState();
        }
        
        statesVisited.add(state);
        assertTrue(state.getSequenceOrder() == null || state == state.getParentState());

        verifyAsInitialState(state);
        verifyAsFinalState(state);

        verifyStateActions(state);
        verifyStateActionAssignments(state);
        verifyStateActionNotifications(state);
        
        List<State> transitionStates = stateService.getOrderedTransitionStates(state, statesVisited.toArray(new State[statesVisited.size()]));
        for (State transitionState : transitionStates) {
            verifyState(transitionState);
        }
    }
    
    private State getRootState() {
        State rootState = stateService.getById(PrismState.SYSTEM_APPROVED);
        assertTrue(rootState.getScope().getPrecedence() == 1);
        
        Set<StateTransition> inverseStateTransitions = rootState.getInverseStateTransitions();
        int inverseStateTransitionCount = inverseStateTransitions.size();
        assertTrue(inverseStateTransitionCount <= 1);
        
        if (inverseStateTransitionCount == 1) {
            assertEquals(PrismState.SYSTEM_APPROVED, inverseStateTransitions.iterator().next().getStateAction().getState().getId());
        }
        
        return rootState;
    }
    
    private void verifyAsInitialState(State state) {
        if (state.isInitialState()) {
            Scope scope = state.getScope();
            Set<Scope> parentScopes = Sets.newHashSet();
            
            Set<StateTransition> inverseStateTransitions = state.getInverseStateTransitions();
            for (StateTransition stateTransition : inverseStateTransitions) {
                Scope fromScope = stateTransition.getStateAction().getState().getScope();
                
                if (fromScope != scope) {
                    parentScopes.add(fromScope);
                }
            }
            
            assertTrue(parentScopes.size() > 0 || state.isFinalState());
            actualInitialStates.add(state.getId());
        }
    }
    
    private void verifyAsFinalState(State state) {
        if (state.isFinalState()) {
            Set<State> transitionStates = Sets.newHashSet();
            
            for (StateAction stateAction : state.getStateActions()) {
                for (StateTransition stateTransition : stateAction.getStateTransitions()) {
                    transitionStates.add(stateTransition.getTransitionState());
                }
            }
            
            int transitionStateCount = transitionStates.size();
            assertTrue(transitionStateCount <= 1 || state.isInitialState());
            
            if (transitionStateCount == 1) {
                assertEquals(state, transitionStates.iterator().next());
            }
            
            actualFinalStates.add(state.getId());
        }
    }

    private void verifyStateActions(State state) {
        Set<Action> escalationActions = Sets.newHashSet();
        
        for (StateAction stateAction : state.getStateActions()) {
            Action action = stateAction.getAction();

            if (action.getActionType().isSystemAction()) {
                assertNotSame(stateAction.getState(),  stateAction.getStateTransitions().iterator().next());
                assertFalse(stateAction.isRaisesUrgentFlag());
                assertNull(stateAction.getNotificationTemplate());
                actualEscalationStates.add(state);
            } 
           
            if (stateAction.isRaisesUrgentFlag()) {
                assertNotNull(stateAction.getNotificationTemplate());
            } 
            
            if (action.isCreationAction()) {
                actualCreatableScopes.add(action.getCreationScope().getId());
                actualCreationActions.add(action.getId());
            } 
            
            if (action.getActionType() == PrismActionType.SYSTEM_ESCALATION) {
                escalationActions.add(action);
            }

            verifyStateTransitions(stateAction);
        }
        
        if (systemService.getStateDuration(state) != null) {
            assertFalse(escalationActions.isEmpty());
        }
    }

    private void verifyStateTransitions(StateAction stateAction) {
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

            lastTransitionEvaluation = thisTransitionEvaluation;
            verifyRoleTransitions(stateTransition);
        }
    }

    private void verifyRoleTransitions(StateTransition stateTransition) {
        Set<PrismRole> actualOwnerRoles = Sets.newHashSet();
        Set<PrismRole> actualProcessedRoles = Sets.newHashSet();

        for (RoleTransition roleTransition : stateTransition.getRoleTransitions()) {
            Role transitionRole = roleTransition.getTransitionRole();
            PrismRole transitionRoleId = transitionRole.getId();

            actualProcessedRoles.add(transitionRoleId);
            PrismRoleTransitionType roleTransitionType = roleTransition.getRoleTransitionType();

            if (roleTransitionType != PrismRoleTransitionType.REMOVE) {
                actualRolesCreated.add(transitionRoleId);
                if (transitionRole.isScopeOwner() && roleTransitionType == PrismRoleTransitionType.CREATE) {
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

    private void verifyStateActionAssignments(State state) {
        for (StateAction stateAction : state.getStateActions()) {
            Set<StateActionAssignment> assignments = stateAction.getStateActionAssignments();

            if (stateAction.getAction().isSystemAction()) {
                assertTrue(assignments.size() == 0);
            }

            for (StateActionAssignment assignment : assignments) {
                assertTrue(actualRolesCreated.contains(assignment.getRole().getId()));
            }
        }
    }

    private void verifyStateActionNotifications(State state) {
        for (StateAction stateAction : state.getStateActions()) {
            for (StateActionNotification notification : stateAction.getStateActionNotifications()) {
                assertTrue(actualRolesCreated.contains(notification.getRole().getId()));
            }
        }
    }

    private <T> void assertCollectionEquals(Collection<T> expectedCollection, Collection<T> actualCollection) {
        assertEquals(expectedCollection.size(), actualCollection.size());
        assertTrue(actualCollection.containsAll(expectedCollection));
    }

}
