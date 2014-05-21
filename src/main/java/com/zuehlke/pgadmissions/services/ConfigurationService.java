package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.StateDAO;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.dto.ServiceLevelsDTO;

@Service
public class ConfigurationService {

    @Autowired
    private StateDAO stateDAO;

    @Transactional
    public void saveServiceLevels(ServiceLevelsDTO serviceLevelsDTO) {
        // TODO implement
    }
    
    @Transactional
    public ServiceLevelsDTO getServiceLevels() {
        // TODO implement
        return null;
    }

    @Transactional
    public List<State> getConfigurableStates() {
        return stateDAO.getAllConfigurableStates();
    }

}