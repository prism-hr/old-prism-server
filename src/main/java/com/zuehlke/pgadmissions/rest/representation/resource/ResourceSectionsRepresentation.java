package com.zuehlke.pgadmissions.rest.representation.resource;

import java.util.LinkedHashSet;

public class ResourceSectionsRepresentation extends LinkedHashSet<ResourceSectionRepresentation> {

    private static final long serialVersionUID = -3916340537735922375L;

    public ResourceSectionsRepresentation withSection(ResourceSectionRepresentation section) {
        add(section);
        return this;
    }

    public ResourceSectionsRepresentation withSections(ResourceSectionsRepresentation sections) {
        addAll(sections);
        return this;
    }

}
