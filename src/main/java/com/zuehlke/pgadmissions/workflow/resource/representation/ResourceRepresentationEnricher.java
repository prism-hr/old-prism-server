package com.zuehlke.pgadmissions.workflow.resource.representation;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceClientRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationExtended;

public interface ResourceRepresentationEnricher<T extends ResourceRepresentationExtended & ResourceClientRepresentation> {

    public void enrich(PrismScope resourceScope, Integer resourceId, T representation) throws Exception;

}
