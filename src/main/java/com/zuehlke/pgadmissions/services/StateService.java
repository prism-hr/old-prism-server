package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.StateDAO;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

@Service
@Transactional
public class StateService {

    @Autowired
    private StateDAO stateDAO;

    public State getById(ApplicationFormStatus id) {
        return stateDAO.getById(id);
    }
    
    public void save(State state) {
        stateDAO.save(state);
    }
    
    public List<State> getAllConfigurableStates() {
        return stateDAO.getAllConfigurableStates();
    }
    
    public List<ApplicationFormStatus> getAllStatesThatApplicationsCanBeAssignedTo() {
        return stateDAO.getAllStatesThatApplicationsCanBeAssignedTo();
    }
    
    public List<ApplicationFormStatus> getAllStatesThatApplicationsCanBeAssignedFrom() {
        return stateDAO.getAllStatesThatApplicationsCanBeAssignedFrom();
    }
    
}
