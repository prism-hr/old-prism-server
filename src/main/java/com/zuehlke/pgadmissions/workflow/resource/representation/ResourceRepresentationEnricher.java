package com.zuehlke.pgadmissions.workflow.resource.representation;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.representation.resource.AbstractResourceRepresentation;

public interface ResourceRepresentationEnricher {

    public void enrich(PrismScope resourceScope, Integer resourceId, AbstractResourceRepresentation representation) throws Exception;
    
}
