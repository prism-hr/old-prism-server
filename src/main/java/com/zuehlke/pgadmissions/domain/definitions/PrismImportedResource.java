package com.zuehlke.pgadmissions.domain.definitions;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import uk.co.alumeni.prism.api.model.resource.request.ResourceRequest;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public enum PrismImportedResource {

    RESOURCE_INSTITUTION(INSTITUTION, uk.co.alumeni.prism.api.model.resource.request.InstitutionRequest.class), //
    RESOURCE_DEPARTMENT(DEPARTMENT, uk.co.alumeni.prism.api.model.resource.request.ResourceParentDivisionRequest.class), //
    RESOURCE_PROGRAM(PROGRAM, uk.co.alumeni.prism.api.model.resource.request.ProgramRequest.class), //
    RESOURCE_PROJECT(PROJECT, uk.co.alumeni.prism.api.model.resource.request.ProjectRequest.class);

    private PrismScope resourceScope;

    private Class<? extends ResourceRequest> importClass;

    private PrismImportedResource(PrismScope resourceScope, Class<? extends ResourceRequest> importClass) {
        this.resourceScope = resourceScope;
        this.importClass = importClass;
    }

    public PrismScope getResourceScope() {
        return resourceScope;
    }

    public Class<? extends ResourceRequest> getImportClass() {
        return importClass;
    }

}
