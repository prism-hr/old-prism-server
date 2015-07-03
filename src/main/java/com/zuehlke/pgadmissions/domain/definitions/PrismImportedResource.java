package com.zuehlke.pgadmissions.domain.definitions;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import uk.co.alumeni.prism.api.model.resource.Resource;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public enum PrismImportedResource {

    RESOURCE_INSTITUTION(INSTITUTION, uk.co.alumeni.prism.api.model.resource.Institution.class), //
    RESOURCE_DEPARTMENT(DEPARTMENT, uk.co.alumeni.prism.api.model.resource.Department.class), //
    RESOURCE_PROGRAM(PROGRAM, uk.co.alumeni.prism.api.model.resource.Program.class), //
    RESOURCE_PROJECT(PROJECT, uk.co.alumeni.prism.api.model.resource.Project.class);

    private PrismScope resourceScope;

    private Class<? extends Resource> importClass;

    private PrismImportedResource(PrismScope resourceScope, Class<? extends Resource> importClass) {
        this.resourceScope = resourceScope;
        this.importClass = importClass;
    }

    public PrismScope getResourceScope() {
        return resourceScope;
    }

    public Class<? extends Resource> getImportClass() {
        return importClass;
    }

}
