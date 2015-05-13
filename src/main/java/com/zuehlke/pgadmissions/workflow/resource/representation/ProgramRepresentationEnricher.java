package com.zuehlke.pgadmissions.workflow.resource.representation;

import javax.inject.Inject;

import org.dozer.Mapper;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.rest.representation.resource.AbstractResourceRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ProgramExtendedRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceAttributesRepresentation;
import com.zuehlke.pgadmissions.services.ResourceService;

@Component
public class ProgramRepresentationEnricher implements ResourceRepresentationEnricher {

    @Inject
    private ResourceService resourceService;

    @Inject
    private Mapper mapper;

    @Override
    public void enrich(PrismScope resourceScope, Integer resourceId, AbstractResourceRepresentation representation) throws Exception {
        Resource resource = resourceService.getById(resourceScope, resourceId);
        ProgramExtendedRepresentation programRepresentation = (ProgramExtendedRepresentation) representation;
        programRepresentation.setResourceSummary(resourceService.getResourceSummaryRepresentation(resourceScope, resourceId));
        representation.setAttributes(mapper.map(resource, ResourceAttributesRepresentation.class));
    }

}
