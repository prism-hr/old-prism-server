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

    public List<PrismScope> getScopesDescending() {
        return scopeDAO.getScopesDescending();
    }
    
    public <T extends Resource> List<PrismScope> getParentScopesAscending(PrismScope scopeId) {
        return scopeDAO.getParentScopesAscending(scopeId);
    }
    
    public <T extends Resource> List<PrismScope> getJoinScopesAscending(PrismScope scopeId) {
        return scopeDAO.getJoinScopesAscending(scopeId);
    }
    
}
