package com.zuehlke.pgadmissions.integration.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.RoleTransition;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.StateActionAssignment;
import com.zuehlke.pgadmissions.domain.StateActionNotification;
import com.zuehlke.pgadmissions.domain.StateDuration;
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
    
    private final Set<PrismState> statesVisited = Sets.newHashSet();
    
    private final Set<PrismRole> rolesCreated = Sets.newHashSet();
    
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
        State rootState = stateService.getById(PrismState.SYSTEM_APPROVED);
        rolesCreated.add(PrismRole.SYSTEM_ADMINISTRATOR);
        
        verifyStateActions(rootState);
        
        assertCollectionEquals(PrismScope.getCreatableScopes(), actualCreatableScopes);
        assertCollectionEquals(PrismAction.getCreationActions(), actualCreationActions);
        assertCollectionEquals(PrismState.getInitialStates(), actualInitialStates);
        assertCollectionEquals(PrismState.getFinalStates(), actualFinalStates);
    }

    private void verifyStateActions(State state) {
        statesVisited.add(state.getId());
        
        if (state.isInitialState()) {
            actualInitialStates.add(state.getId());
        }
        
        if (state.isFinalState()) {
            actualFinalStates.add(state.getId());
        }
        
        Set<StateAction> stateActions = state.getStateActions();
        
        for (StateAction stateAction : stateActions) {
            verifyStateTransitions(stateAction);
        }
        
        for (StateAction stateAction : stateActions) {
            verifyStateActionAssignments(stateAction);
            verifyStateActionNotifications(stateAction);
        }
    }

    private void verifyStateActionAssignments(StateAction stateAction) {
        for (StateActionAssignment assignment : stateAction.getStateActionAssignments()) {
            assertTrue(rolesCreated.contains(assignment.getRole().getId()));
        }
    }

    private void verifyStateActionNotifications(StateAction stateAction) {
        for (StateActionNotification notification : stateAction.getStateActionNotifications()) {
            assertTrue(rolesCreated.contains(notification.getRole().getId()));
        }
    }
    
    private void verifyStateTransitions(StateAction stateAction) {
        State state = stateAction.getState();
        Action action = stateAction.getAction();
        Set<StateTransition> stateTransitions = stateAction.getStateTransitions();
        
        if (action.getActionType().isSystemAction()) {
            assertTrue(stateTransitions.size() == 1);
            assertNotSame(stateAction.getState(), stateTransitions.iterator().next());
            
            if (action.getActionType() == PrismActionType.SYSTEM_ESCALATION) {
                StateDuration duration = systemService.getStateDuration(state);
                assertNotNull(duration);
            }
            
            assertFalse(stateAction.isRaisesUrgentFlag());
            assertNull(stateAction.getNotificationTemplate());
        } else if (stateAction.isRaisesUrgentFlag()) {
            assertNotNull(stateAction.getNotificationTemplate());
        } else if (action.isCreationAction()) { 
            actualCreatableScopes.add(action.getCreationScope().getId());
            actualCreationActions.add(action.getId());
        }
        
        Set<State> transitionStates = verifyStateTransitionEvaluation(state, action, stateTransitions);
        
        for (State transitionState : transitionStates) {
            verifyStateActions(transitionState);
        }
        
    }

    private Set<State> verifyStateTransitionEvaluation(State state, Action action, Set<StateTransition> stateTransitions) {
        Set<State> transitionStates = Sets.newHashSet();
        PrismTransitionEvaluation lastTransitionEvaluation = null;
        
        int stateTransitionCount = transitionStates.size();
        for (StateTransition stateTransition : stateTransitions) {
            PrismTransitionEvaluation thisTransitionEvaluation = stateTransition.getStateTransitionEvaluation();
            
            if (stateTransitionCount == 1) {
                assertNull(thisTransitionEvaluation);
            } else {
                assertNotNull(thisTransitionEvaluation);
                if (lastTransitionEvaluation != null) {
                    assertEquals(lastTransitionEvaluation, thisTransitionEvaluation);
                }
            }
            
            State transitionState = stateTransition.getTransitionState();
            if (state != transitionState && !statesVisited.contains(transitionState)) {
                transitionStates.add(transitionState);
            }
            
            verifyRoleTransitions(action, stateTransition);
            lastTransitionEvaluation = thisTransitionEvaluation;
        }
        
        return transitionStates;
    }

    private void verifyRoleTransitions(Action action, StateTransition stateTransition) {
        Set<PrismRole> actualCreatorRoles = Sets.newHashSet();
        
        for (RoleTransition roleTransition : stateTransition.getRoleTransitions()) {
            Role transitionRole = roleTransition.getTransitionRole();
            PrismRole transitionRoleId = transitionRole.getId();
            
            if (roleTransition.getRoleTransitionType() == PrismRoleTransitionType.CREATE) {
                rolesCreated.add(transitionRoleId);
                if (transitionRole.isScopeOwner()) {
                    actualCreatorRoles.add(transitionRoleId);
                }
            } else {
                assertTrue(rolesCreated.contains(transitionRoleId));
            }
        }
        
        if (actualCreatorRoles.size() > 0) {
            Set<PrismRole> expectedCreatorRoles = PrismRole.getScopeOwners(action.getCreationScope().getId());
            assertCollectionEquals(expectedCreatorRoles, actualCreatorRoles);
        }
    }
    
    private <T> void assertCollectionEquals(Collection<T> expectedCollection, Collection<T> actualCollection) {
        assertEquals(expectedCollection.size(), actualCollection.size());
        assertTrue(actualCollection.containsAll(expectedCollection));
    }
    
}
