package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.System;

@Service
@Transactional
public class SystemService {

    @Autowired 
    private EntityService entityService;
    
    public System getSystem() {
        return (System) entityService.getByProperty(com.zuehlke.pgadmissions.domain.System.class, "name", "PRiSM");
    }
    
}
