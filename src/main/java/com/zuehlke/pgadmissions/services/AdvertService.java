package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.AdvertDAO;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.State;

@Service
@Transactional
public class AdvertService {

    @Autowired
    private AdvertDAO advertDAO;
    
    @Autowired
    private EntityService entityService;
    
    @Autowired
    private StateService stateService;
    
    public Advert getById(Integer id) {
        return entityService.getById(Advert.class, id);
    }
    
    // TODO: user filters
    public List<Advert> getActiveAdverts() {
        List<State> activeProgramStates = stateService.getActiveProgramStates();
        List<State> activeProjectStates = stateService.getActiveProjectStates();
        return advertDAO.getActiveAdverts(activeProgramStates, activeProjectStates);
    }
    
}
