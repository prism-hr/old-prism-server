package com.zuehlke.pgadmissions.mapping;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.rest.representation.ScopeActionSummaryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.ScopeActionSummaryRepresentation.ActionSummaryRepresentation;
import com.zuehlke.pgadmissions.services.ScopeService;

@Service
@Transactional
public class ScopeMapper {

    @Inject
    private ScopeService scopeService;
    
    public List<ScopeActionSummaryRepresentation> getScopeActionSummaryRepresentations(User user, PrismScope permissionScope) {
        List<ScopeActionSummaryRepresentation> scopeActionSummaryRepresentations = Lists.newArrayList();
        Multimap<PrismScope, ActionSummaryRepresentation> actionSummaries = scopeService.getScopeActionSummaries(user, permissionScope);
        actionSummaries.keySet().forEach(actionScope -> {
            scopeActionSummaryRepresentations
                    .add(new ScopeActionSummaryRepresentation().withScope(actionScope).withActionSummaries(Lists.newLinkedList(actionSummaries.get(actionScope))));
        });
        return scopeActionSummaryRepresentations;
    }
    
}
