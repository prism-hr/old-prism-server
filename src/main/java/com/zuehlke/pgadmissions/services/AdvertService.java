package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.AdvertDAO;
import com.zuehlke.pgadmissions.domain.Advert;

@Service
@Transactional
public class AdvertService {

    @Autowired
    private AdvertDAO advertDAO;
    
    @Autowired
    private ProgramService programService;
    
    @Autowired
    private ProjectService projectService;
    
    // TODO: user filters
    public List<Advert> getActiveAdverts() {
        List<Integer> activeProgramIds = programService.getActiveProgramIds();
        List<Integer> activeProjectIds = projectService.getActiveProjectIds();
        return advertDAO.getActiveAdverts(activeProgramIds, activeProjectIds);
    }
    
}
