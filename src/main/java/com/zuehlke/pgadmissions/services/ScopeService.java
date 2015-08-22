package com.zuehlke.pgadmissions.services;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.ScopeDAO;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.workflow.Scope;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSectionRepresentation;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;

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

    public HashMultimap<PrismScope, PrismState> getChildScopesWithActiveStates(PrismScope resourceScope,
                                                                               PrismScope... excludedScopes) {
        HashMultimap<PrismScope, PrismState> childScopes = HashMultimap.create();
        for (PrismScope childScope : getChildScopesAscending(resourceScope)) {
            if (excludedScopes.length == 0 || !ArrayUtils.contains(excludedScopes, childScope)) {
                childScopes.putAll(childScope, stateService.getActiveResourceStates(childScope));
            }
        }
        return childScopes;
    }

    public List<ResourceSectionRepresentation> getRequiredSections(PrismScope prismScope) {
        return getRequiredSections(prismScope.getSections(), null);
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

    private List<ResourceSectionRepresentation> getRequiredSections(
            List<ResourceSectionRepresentation> sections, List<ResourceSectionRepresentation> requiredSections) {
        requiredSections = requiredSections == null ? Lists.newArrayList() : requiredSections;
        for (ResourceSectionRepresentation section : sections) {
            if (section.isRequired()) {
                requiredSections.add(section);
            }
            List<ResourceSectionRepresentation> subsections = section.getSubsections();
            if (subsections != null) {
                getRequiredSections(subsections, requiredSections);
            }
        }
        return requiredSections;
    }

}
