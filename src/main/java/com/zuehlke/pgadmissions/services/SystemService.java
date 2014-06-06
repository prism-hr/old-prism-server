package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.Scope;
import com.zuehlke.pgadmissions.domain.System;
import com.zuehlke.pgadmissions.domain.enums.PrismScope;

@Service
@Transactional
public class SystemService {

    @Autowired 
    private EntityService entityService;
    
    public System getSystem() {
        return (System) entityService.getByProperty(com.zuehlke.pgadmissions.domain.System.class, "name", "PRiSM");
    }
    
    public Scope getSystemScope(PrismScope scopeId) {
        return entityService.getByProperty(Scope.class, "id", scopeId);
    }
    
    public List<Scope> getAllScopes() {
        return entityService.getAll(Scope.class);
    }
    
}
