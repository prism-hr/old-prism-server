package com.zuehlke.pgadmissions.integration.helpers;

import static org.junit.Assert.assertEquals;
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
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismTransitionEvaluation;
import com.zuehlke.pgadmissions.services.ScopeService;
import com.zuehlke.pgadmissions.services.StateService;

@Service
@Transactional
public class WorkflowConfigurationHelper {
    
    private final Set<State> statesVisited = Sets.newHashSet();
    
    private final Set<PrismScope> actualCreatableScopes = Sets.newHashSet();
    
    private final Set<PrismState> actualInitialStates = Sets.newHashSet();
    
    private final Set<PrismState> actualFinalStates = Sets.newHashSet();
    
    private final Set<PrismAction> actualCreationActions = Sets.newHashSet();
    
    @Autowired
    private ScopeService scopeService;
    
    @Autowired
    private StateService stateService;

    public void verifyWorkflowConfiguration() {
        State rootState = verifyRootState();
        verifyStateActions(rootState);
        
        verifyCollectionsMatch(PrismScope.getCreatableScopes(), actualCreatableScopes);
        verifyCollectionsMatch(PrismAction.getCreationActions(), actualCreationActions);
        verifyCollectionsMatch(PrismState.getInitialStates(), actualInitialStates);
        verifyCollectionsMatch(PrismState.getFinalStates(), actualFinalStates);
    }
    
    private State verifyRootState() {
        List<State> potentialRootState = stateService.getRootState();
        assertTrue(potentialRootState.size() == 1);
        
        State rootState = potentialRootState.get(0);
        assertTrue(rootState.getScope().getPrecedence() == 1);
        
        return rootState;
    }

    private void verifyStateActions(State state) {
        statesVisited.add(state);
        
        if (state.isInitialState()) {
            actualInitialStates.add(state.getId());
        }
        
        if (state.isFinalState()) {
            actualFinalStates.add(state.getId());
        }
        
        for (StateAction stateAction : state.getStateActions()) {
            verifyStateTransitions(stateAction);   
        }
    }

    private void verifyStateTransitions(StateAction stateAction) {
        Action action = stateAction.getAction();
        Set<StateTransition> stateTransitions = stateAction.getStateTransitions();
        
        if (action.getActionType().isSystemAction()) {
            assertTrue(stateTransitions.size() == 1);
            assertNotSame(stateAction.getState(), stateTransitions.iterator().next());
        }
        
        if (action.isCreationAction()) { 
            actualCreatableScopes.add(action.getCreationScope().getId());
            actualCreationActions.add(action.getId());
        }
        
        verifyStateTransitionEvaluation(stateTransitions);
    }

    private void verifyStateTransitionEvaluation(Set<StateTransition> stateTransitions) {
        if (stateTransitions.size() > 1) {
            Set<PrismTransitionEvaluation> transitionEvaluations = Sets.newHashSet();
            for (StateTransition stateTransition : stateTransitions) {
                PrismTransitionEvaluation transitionEvaluation = stateTransition.getStateTransitionEvaluation();
                assertNotNull(transitionEvaluation);
                transitionEvaluations.add(transitionEvaluation);
                
                State currentState = stateTransition.getStateAction().getState();
                State transitionState = stateTransition.getTransitionState();
                
                if (currentState != transitionState && !statesVisited.contains(transitionState)) {
                    verifyStateActions(transitionState);
                }
            }
            assertTrue(transitionEvaluations.size() == 1);
        } else if (stateTransitions.size() == 1) {
            assertNull(stateTransitions.iterator().next().getStateTransitionEvaluation());
        }
    }
    
    private <T> void verifyCollectionsMatch(Collection<T> expectedCollection, Collection<T> actualCollection) {
        assertEquals(expectedCollection.size(), actualCollection.size());
        assertTrue(actualCollection.containsAll(expectedCollection));
    }
    
}
