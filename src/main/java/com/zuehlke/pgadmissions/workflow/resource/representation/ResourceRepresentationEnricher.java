package com.zuehlke.pgadmissions.workflow.resource.representation;

import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationExtended;

public interface ResourceRepresentationEnricher<T extends Resource, V extends ResourceRepresentationExtended> {

    public void enrich(T resource, V representation) throws Exception;

}
