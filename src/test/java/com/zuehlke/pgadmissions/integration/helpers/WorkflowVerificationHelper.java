package com.zuehlke.pgadmissions.integration.helpers;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Scope;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismTransitionEvaluation;
import com.zuehlke.pgadmissions.services.ScopeService;
import com.zuehlke.pgadmissions.services.StateService;

@Service
@Transactional
public class WorkflowVerificationHelper {

    private final Set<Scope> actualCreatableScopes = Sets.newHashSet();
    
    @Autowired
    private ScopeService scopeService;
    
    @Autowired
    private StateService stateService;
    
    public void verifyWorkflowConfiguration() {
        verifyWorkflowConfiguration(null);
    }
    
    private void verifyWorkflowConfiguration(State state) {
        state = state == null ? verifyRootState() : state;
        verifyStateActions(state);
    }
    
    private State verifyRootState() {
        List<State> potentialRootState = stateService.getRootState();
        assertTrue(potentialRootState.size() == 1);
        
        State rootState = potentialRootState.get(0);
        assertTrue(rootState.getScope().getPrecedence() == 1);
        
        return rootState;
    }

    private void verifyStateActions(State state) {
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
        } else {
            if (stateTransitions.size() > 1) {
                verifyStateTransitionEvaluation(stateTransitions);
            }
        }
        
        if (action.isCreationAction()) { 
            actualCreatableScopes.add(action.getCreationScope());
        }
    }

    private void verifyStateTransitionEvaluation(Set<StateTransition> stateTransitions) {
        Set<PrismTransitionEvaluation> transitionEvaluations = Sets.newHashSet();
        for (StateTransition stateTransition : stateTransitions) {
            PrismTransitionEvaluation transitionEvaluation = stateTransition.getStateTransitionEvaluation();
            assertNotNull(transitionEvaluation);
            transitionEvaluations.add(transitionEvaluation);
        }
        assertTrue(transitionEvaluations.size() == 1);
    }
    
}
