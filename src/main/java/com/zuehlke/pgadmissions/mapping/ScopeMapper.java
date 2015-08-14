package com.zuehlke.pgadmissions.mapping;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.representation.ScopeRepresentation;

@Service
@Transactional
public class ScopeMapper {

    public List<ScopeRepresentation> getScopeRepresentations() {
        List<ScopeRepresentation> representations = Lists.newLinkedList();
        for (PrismScope scope : PrismScope.values()) {
            representations.add(new ScopeRepresentation().withId(scope).withSections(scope.getSections()));
        }
        return representations;
    }

}
