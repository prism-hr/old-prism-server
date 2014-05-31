package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SystemService {

    @Autowired 
    private EntityService entityService;
    
    public com.zuehlke.pgadmissions.domain.System getSystem() {
        return (com.zuehlke.pgadmissions.domain.System) entityService.getByProperty(com.zuehlke.pgadmissions.domain.System.class, "name", "PRiSM");
    }
    
}
