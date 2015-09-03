package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.hibernate.criterion.Projections;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.zuehlke.pgadmissions.dao.ScopeDAO;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Scope;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceListFilterDTO;
import com.zuehlke.pgadmissions.rest.representation.ScopeActionSummaryRepresentation.ActionSummaryRepresentation;

@Service
@Transactional
public class ScopeService {

    @Inject
    private ScopeDAO scopeDAO;

    @Inject
    private EntityService entityService;

    @Inject
    private ResourceService resourceService;

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

    public HashMultimap<PrismScope, PrismScope> getExpandedScopes(PrismScope prismScope) {
        HashMultimap<PrismScope, PrismScope> enclosedScopes = HashMultimap.create();
        List<PrismScope> scopes = Lists.newLinkedList(getEnclosingScopesDescending(APPLICATION, prismScope));

        int scopeCount = scopes.size();
        for (int i = 0; i < scopeCount; i++) {
            if (i < (scopeCount - 1)) {
                enclosedScopes.putAll(scopes.get(i), scopes.subList(0, (i + 1)));
            } else {
                enclosedScopes.putAll(scopes.get(i), scopes);
            }
        }

        return enclosedScopes;
    }

    public Multimap<PrismScope, ActionSummaryRepresentation> getScopeActionSummaries(User user, PrismScope permissionScope) {
        permissionScope = getEffectivePermissionsScope(permissionScope);
        LinkedHashMultimap<PrismScope, ActionSummaryRepresentation> summaries = LinkedHashMultimap.create();
        
        List<PrismScope> visibleScopes = getEnclosingScopesDescending(APPLICATION, permissionScope);
        visibleScopes.forEach(scope -> {
            summaries.putAll(scope, resourceService.getResources(user, scope, visibleScopes.stream()
                    .filter(as -> as.ordinal() < scope.ordinal())
                    .collect(Collectors.toList()),
                    new ResourceListFilterDTO().withUrgentOnly(true), //
                    Projections.projectionList() //
                            .add(Projections.groupProperty("stateAction.action.id").as("action")) //
                            .add(Projections.countDistinct("id").as("actionCount")),
                    ActionSummaryRepresentation.class));
        });
        return summaries;
    }

    private PrismScope getEffectivePermissionsScope(PrismScope permissionScope) {
        permissionScope = permissionScope.equals(SYSTEM) ? PrismScope.INSTITUTION : permissionScope;
        return permissionScope;
    }

}
