package com.zuehlke.pgadmissions.integration.helpers;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.services.StateService;

@Service
@Transactional
public class WorkflowVerificationHelper {

    @Autowired
    private StateService stateService;
    
    public void verifyWorkflowConfiguration() {
        verifyWorkflowConfiguration(null);
    }
    
    private void verifyWorkflowConfiguration(State state) {
        state = state == null ? verifyRootState() : state;
        verifyStateActions(state);
    }

    private void verifyStateActions(State state) {
        Set<StateAction> stateActions = state.getStateActions();
        
        for (StateAction stateAction : stateActions) {
            Set<StateTransition> stateTransitions = stateAction.getStateTransitions();
            
        }
    }

    private State verifyRootState() {
        List<State> potentialRootState = stateService.getRootState();
        assertTrue(potentialRootState.size() == 1);
        
        State rootState = potentialRootState.get(0);
        assertTrue(rootState.getScope().getPrecedence() == 1);
        
        return rootState;
    }
    
}
