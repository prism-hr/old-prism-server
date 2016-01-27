package uk.co.alumeni.prism.services;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.alumeni.prism.dao.ScopeDAO;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.workflow.Scope;

import com.google.common.collect.HashMultimap;

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

    public List<PrismScope> getEnclosingScopesDescending(PrismScope prismScope, PrismScope finalScope) {
        return scopeDAO.getEnclosingScopesDescending(prismScope, finalScope);
    }

    public List<PrismScope> getParentScopesDescending(PrismScope scope, PrismScope finalScope) {
        return scopeDAO.getParentScopesDescending(scope, finalScope);
    }

    public List<PrismScope> getChildScopesAscending(PrismScope scope, PrismScope finalScope) {
        return scopeDAO.getChildScopesAscending(scope, finalScope);
    }

    public HashMultimap<PrismScope, PrismState> getChildScopesWithActiveStates(PrismScope scope, PrismScope finalScope) {
        HashMultimap<PrismScope, PrismState> childScopes = HashMultimap.create();
        for (PrismScope childScope : getChildScopesAscending(scope, finalScope)) {
            childScopes.putAll(childScope, stateService.getActiveResourceStates(childScope));
        }
        return childScopes;
    }

}
