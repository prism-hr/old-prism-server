package com.zuehlke.pgadmissions.rest;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.resource.Department;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.domain.resource.Project;
import com.zuehlke.pgadmissions.domain.resource.System;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceOpportunityClientRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceParentClientRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationClientRepresentation;

public class RestApiUtils {

    private static final Logger logger = LoggerFactory.getLogger(RestApiUtils.class);

    public static ResourceDescriptor getResourceDescriptor(String resourceScope) {
        if ("applications".equals(resourceScope)) {
            return new ResourceDescriptor(Application.class, ApplicationClientRepresentation.class, APPLICATION);
        } else if ("projects".equals(resourceScope)) {
            return new ResourceDescriptor(Project.class, ResourceOpportunityClientRepresentation.class, PROJECT);
        } else if ("programs".equals(resourceScope)) {
            return new ResourceDescriptor(Program.class, ResourceOpportunityClientRepresentation.class, PROGRAM);
        } else if ("departments".equals(resourceScope)) {
            return new ResourceDescriptor(Department.class, ResourceParentClientRepresentation.class, DEPARTMENT);
        } else if ("institutions".equals(resourceScope)) {
            return new ResourceDescriptor(Institution.class, ResourceParentClientRepresentation.class, INSTITUTION);
        } else if ("systems".equals(resourceScope)) {
            return new ResourceDescriptor(System.class, ResourceRepresentationSimple.class, SYSTEM);
        }
        String errorMessage = "Unknown resource scope " + resourceScope;
        logger.error(errorMessage);
        throw new ResourceNotFoundException(errorMessage);
    }

}
