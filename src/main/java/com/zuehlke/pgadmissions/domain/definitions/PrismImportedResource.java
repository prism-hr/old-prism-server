package com.zuehlke.pgadmissions.domain.definitions;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

import uk.co.alumeni.prism.api.model.advert.EnumDefinition;
import uk.co.alumeni.prism.api.model.resource.request.ResourceParentRequest;

public enum PrismImportedResource implements EnumDefinition<uk.co.alumeni.prism.enums.PrismImportedResource> {

    RESOURCE_INSTITUTION(INSTITUTION), //
    RESOURCE_DEPARTMENT(DEPARTMENT), //
    RESOURCE_PROGRAM(PROGRAM), //
    RESOURCE_PROJECT(PROJECT);

    private PrismScope resourceScope;

    private PrismImportedResource(PrismScope resourceScope) {
        this.resourceScope = resourceScope;
    }

    @Override
    public uk.co.alumeni.prism.enums.PrismImportedResource getDefinition() {
        return uk.co.alumeni.prism.enums.PrismImportedResource.valueOf(name());
    }

    public PrismScope getResourceScope() {
        return resourceScope;
    }

    public Class<? extends ResourceParentRequest> getRequestClass() {
        return getDefinition().getRequestClass();
    }

}
