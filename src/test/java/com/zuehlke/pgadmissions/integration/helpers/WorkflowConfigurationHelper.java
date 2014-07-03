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
    
    private final Set<StateTransition> stateTransitionsFollowed = Sets.newHashSet();

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
        System.out.print(state.getId().toString() + "\n");

        if (state.isInitialState()) {
            actualInitialStates.add(state.getId());
        }

        if (state.isFinalState()) {
            actualFinalStates.add(state.getId());
        }

        Set<State> transitionStates = Sets.newHashSet();
        Set<StateAction> stateActions = state.getStateActions();

        for (StateAction stateAction : stateActions) {
            System.out.print("\t" + stateAction.getState().getId().toString() + ":" + stateAction.getAction().getId().toString() + "\n");
            HashMultimap<PrismScope, State> thisTransitionStates = verifyStateTransitions(stateAction);
            
            List<Scope> ascendingScopes = scopeService.getScopesAscending();
            for (Scope scope : ascendingScopes) {
                transitionStates.addAll(thisTransitionStates.get(scope.getId()));
            }
        }
        
        for (State transitionState : transitionStates) {
            verifyStateActions(transitionState);
        }
    }

    private HashMultimap<PrismScope, State> verifyStateTransitions(StateAction stateAction) {
        Action action = stateAction.getAction();
        Set<StateTransition> stateTransitions = stateAction.getStateTransitions();
        
        if (action.getActionType().isSystemAction()) {
            assertNotSame(stateAction.getState(), stateTransitions.iterator().next());
            assertFalse(stateAction.isRaisesUrgentFlag());
            assertNull(stateAction.getNotificationTemplate());
        } else if (stateAction.isRaisesUrgentFlag()) {
            assertNotNull(stateAction.getNotificationTemplate());
        } else if (action.isCreationAction()) {
            actualCreatableScopes.add(action.getCreationScope().getId());
            actualCreationActions.add(action.getId());
        }

        return verifyStateTransitionEvaluation(stateAction);
    }

    private HashMultimap<PrismScope, State> verifyStateTransitionEvaluation(StateAction stateAction) {
        HashMultimap<PrismScope, State> transitionStates = HashMultimap.create();
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
            verifyStateActionAssignments(stateAction);
            verifyStateActionNotifications(stateAction);

            Action action = stateAction.getAction();
            if (!stateTransitionsFollowed.contains(stateTransition)) {
                Scope transitionScope = action.isCreationAction() ? action.getCreationScope() : action.getScope();
                transitionStates.put(transitionScope.getId(), stateTransition.getTransitionState());
            }
            
            stateTransitionsFollowed.add(stateTransition);
        }

        return transitionStates;
    }

    private void verifyRoleTransitions(StateTransition stateTransition) {
        Set<PrismRole> actualOwnerRoles = Sets.newHashSet();
        Set<PrismRole> actualProcessedRoles = Sets.newHashSet();

        for (RoleTransition roleTransition : stateTransition.getRoleTransitions()) {
            Role role = roleTransition.getRole();
            PrismRole RoleId = role.getId();

            actualProcessedRoles.add(RoleId);

            if (roleTransition.getRoleTransitionType() == PrismRoleTransitionType.CREATE) {
                rolesCreated.add(RoleId);
                if (role.isScopeOwner()) {
                    actualOwnerRoles.add(RoleId);
                }
            }
        }

        assertTrue(rolesCreated.containsAll(actualProcessedRoles));

        Action action = stateTransition.getStateAction().getAction();
        if (action.isCreationAction()) {
            Set<PrismRole> expectedOwnerRoles = PrismRole.getScopeOwners(action.getCreationScope().getId());
            assertCollectionEquals(expectedOwnerRoles, actualOwnerRoles);
        }
    }
    
    private void verifyStateActionAssignments(StateAction stateAction) {
        for (StateActionAssignment assignment : stateAction.getStateActionAssignments()) {
            System.out.print("\t\t" + stateAction.getState().getId().toString() + ":" + stateAction.getAction().getId().toString() + ":"
                    + assignment.getRole().getId().toString() + "\n");
            assertTrue(rolesCreated.contains(assignment.getRole().getId()));
        }
    }
    
    private void verifyStateActionNotifications(StateAction stateAction) {
        for (StateActionNotification notification : stateAction.getStateActionNotifications()) {
            assertTrue(rolesCreated.contains(notification.getRole().getId()));
        }
    }

    private <T> void assertCollectionEquals(Collection<T> expectedCollection, Collection<T> actualCollection) {
        assertEquals(expectedCollection.size(), actualCollection.size());
        assertTrue(actualCollection.containsAll(expectedCollection));
    }

}
