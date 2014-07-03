package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ScopeDAO;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.Scope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

@Service
@Transactional
public class ScopeService {

    @Autowired
    private ScopeDAO scopeDAO;
    
    @Autowired
    private EntityService entityService;
    
    public Scope getById(PrismScope id) {
        return entityService.getByProperty(Scope.class, "id", id);
    }
    
    public <T extends Resource> Scope getByResourceClass(Class<T> resourceClass) {
        return scopeDAO.getByResourceClass(resourceClass);
    }
    
    public List<Scope> getScopes() {
        return scopeDAO.getScopes();
    }
    
    public <T extends Resource> List<Scope> getParentScopes(Class<T> resourceClass) {
        return scopeDAO.getParentScopes(resourceClass);
    }
    
    public <T extends Resource> List<Scope> getChildScopes(Class<T> resourceClass) {
        return scopeDAO.getChildScopes(resourceClass);
    }
    
}
