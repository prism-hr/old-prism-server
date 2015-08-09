package com.zuehlke.pgadmissions.services;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.HashMultimap;
import com.zuehlke.pgadmissions.dao.ScopeDAO;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.workflow.Scope;

@Service
@Transactional
public class ScopeService {

    @Inject
    private ScopeDAO scopeDAO;

    @Inject
    private EntityService entityService;
    
    @Inject
    private StateService stateService;

    public Scope getById(PrismScope id) {
        return entityService.getById(Scope.class, id);
    }

    public List<PrismScope> getScopesDescending() {
        return scopeDAO.getScopesDescending();
    }
    
    public List<PrismScope> getParentScopesDescending(PrismScope prismScope) {
        return scopeDAO.getParentScopesDescending(prismScope);
    }

    public List<PrismScope> getParentScopesDescending(PrismScope prismScope, PrismScope finalScope) {
        return scopeDAO.getParentScopesDescending(prismScope, finalScope);
    }
    
    public List<PrismScope> getChildScopesAscending(PrismScope prismScope) {
        return scopeDAO.getChildScopesAscending(prismScope);
    }

    public List<PrismScope> getChildScopesAscending(PrismScope prismScope, PrismScope finalScope) {
        return scopeDAO.getChildScopesAscending(prismScope, finalScope);
    }
    
    public HashMultimap<PrismScope, PrismState> getChildScopesWithActiveStates(PrismScope resourceScope, PrismScope... excludedScopes) {
        HashMultimap<PrismScope, PrismState> childScopes = HashMultimap.create();
        for (PrismScope childScope : getChildScopesAscending(resourceScope)) {
            if (excludedScopes.length == 0 || !ArrayUtils.contains(excludedScopes, childScope)) {
                childScopes.putAll(childScope, stateService.getActiveResourceStates(childScope));
            }
        }
        return childScopes;
    }

}
