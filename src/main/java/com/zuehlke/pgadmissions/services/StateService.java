package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.apache.commons.beanutils.MethodUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.StateDAO;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.PrismResource;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;
import com.zuehlke.pgadmissions.domain.enums.PrismState;

@Service
@Transactional
public class StateService {

    @Autowired
    private StateDAO stateDAO;

    public State getById(PrismState id) {
        return stateDAO.getById(id);
    }

    public void save(State state) {
        stateDAO.save(state);
    }

    public List<State> getAllConfigurableStates() {
        return stateDAO.getAllConfigurableStates();
    }
    
    public List<StateTransition> getStateTransitions(PrismResource resource, PrismAction action) {
        return stateDAO.getStateTransitions(resource, action);
    }
    
    public StateTransition getStateTransition(PrismResource resource, PrismAction action, Comment comment) {
        StateTransition stateTransition = null;
        
        List<StateTransition> stateTransitions = getStateTransitions(resource, action);     
        if (stateTransitions.size() > 1) {
            try {
                String method = stateTransitions.get(0).getEvaluation().getProcessorMethodName(); 
                stateTransition = (StateTransition) MethodUtils.invokeExactMethod(this, method, new Object[] {resource, comment});
            } catch (Exception e) {
                
            }
        } else {
            stateTransition = stateTransitions.get(0);
        }
        
        return stateTransition;
    }
    
    // TODO : write the methods to get the next states for the conditional transitions

}
