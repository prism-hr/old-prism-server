package com.zuehlke.pgadmissions.rest.representation;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSectionsRepresentation;

public class ScopeRepresentation {

    private PrismScope id;

    private ResourceSectionsRepresentation sections;

    public PrismScope getId() {
        return id;
    }

    public ResourceSectionsRepresentation getSections() {
        return sections;
    }

    public ScopeRepresentation withId(PrismScope id) {
        this.id = id;
        return this;
    }

    public ScopeRepresentation withSections(ResourceSectionsRepresentation sections) {
        this.sections = sections;
        return this;
    }

}
