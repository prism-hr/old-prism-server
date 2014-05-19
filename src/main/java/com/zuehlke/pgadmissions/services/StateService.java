package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.StateDAO;
import com.zuehlke.pgadmissions.domain.State;
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
    
    public List<PrismState> getAllStatesThatApplicationsCanBeAssignedTo() {
        return stateDAO.getAllStatesThatApplicationsCanBeAssignedTo();
    }
    
    public List<PrismState> getAllStatesThatApplicationsCanBeAssignedFrom() {
        return stateDAO.getAllStatesThatApplicationsCanBeAssignedFrom();
    }
    
}
