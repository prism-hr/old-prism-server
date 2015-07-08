package com.zuehlke.pgadmissions.services;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ScopeDAO;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.Scope;

@Service
@Transactional
public class ScopeService {

    @Inject
    private ScopeDAO scopeDAO;

    @Inject
    private EntityService entityService;

    public Scope getById(PrismScope id) {
        return entityService.getByProperty(Scope.class, "id", id);
    }

    public List<PrismScope> getScopesDescending() {
        return scopeDAO.getScopesDescending();
    }

    public <T extends Resource> List<PrismScope> getParentScopesDescending(PrismScope prismScope) {
        return scopeDAO.getParentScopesDescending(prismScope);
    }

    public <T extends Resource> List<PrismScope> getChildScopesAscending(PrismScope prismScope) {
        return scopeDAO.getChildScopesAscending(prismScope);
    }

}
