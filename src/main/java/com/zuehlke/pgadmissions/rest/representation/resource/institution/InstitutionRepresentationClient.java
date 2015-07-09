package com.zuehlke.pgadmissions.rest.representation.resource.institution;

import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationClient;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryRepresentation;

public class InstitutionRepresentationClient extends InstitutionRepresentation implements ResourceRepresentationClient {

    private ResourceSummaryRepresentation resourceSummary;
    
    @Override
    public ResourceSummaryRepresentation getResourceSummary() {
        return this.resourceSummary;
    }

    @Override
    public void setResourceSummary(ResourceSummaryRepresentation resourceSummary) {
        this.resourceSummary = resourceSummary;
    }
    
    
}
