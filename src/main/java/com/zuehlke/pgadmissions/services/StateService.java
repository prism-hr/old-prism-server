package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.StateDAO;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.domain.enums.StateTransitionType;
import com.zuehlke.pgadmissions.domain.enums.SystemAction;

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

    public List<PrismState> getAllStatesThatApplicationsCanBeAssignedTo() {
        return stateDAO.getAllStatesThatApplicationsCanBeAssignedTo();
    }

    public List<PrismState> getAllStatesThatApplicationsCanBeAssignedFrom() {
        return stateDAO.getAllStatesThatApplicationsCanBeAssignedFrom();
    }

    public List<StateTransition> getUserStateTransitions(PrismState state, SystemAction action) {
        return stateDAO.getStateTransitions(state, action, StateTransitionType.ONE_COMPLETED, StateTransitionType.ALL_COMPLETED,
                StateTransitionType.PROPAGATION);
    }

    public List<StateTransition> getStateTransitions(PrismState state, SystemAction action, StateTransitionType... stateTransitionTypes) {
        return stateDAO.getStateTransitions(state, action, stateTransitionTypes);
    }

}
