package com.zuehlke.pgadmissions.rest.representation;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSectionRepresentation;

public class ScopeRepresentation {

    private PrismScope id;

    private List<ResourceSectionRepresentation> sections;

    public PrismScope getId() {
        return id;
    }

    public List<ResourceSectionRepresentation> getSections() {
        return sections;
    }

    public ScopeRepresentation withId(PrismScope id) {
        this.id = id;
        return this;
    }

    public ScopeRepresentation withSections(List<ResourceSectionRepresentation> sections) {
        this.sections = sections;
        return this;
    }

}
