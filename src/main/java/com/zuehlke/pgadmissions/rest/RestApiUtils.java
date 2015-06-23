package com.zuehlke.pgadmissions.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.system.System;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.rest.representation.resource.InstitutionClientRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceOpportunityClientRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.SystemClientRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationClientRepresentation;

public class RestApiUtils {

    private static final Logger logger = LoggerFactory.getLogger(RestApiUtils.class);

    public static ResourceDescriptor getResourceDescriptor(String resourceScope) {
        if ("applications".equals(resourceScope)) {
            return new ResourceDescriptor(Application.class, ApplicationClientRepresentation.class, PrismScope.APPLICATION);
        } else if ("projects".equals(resourceScope)) {
            return new ResourceDescriptor(Project.class, ResourceOpportunityClientRepresentation.class, PrismScope.PROJECT);
        } else if ("programs".equals(resourceScope)) {
            return new ResourceDescriptor(Program.class, ResourceOpportunityClientRepresentation.class, PrismScope.PROGRAM);
        } else if ("institutions".equals(resourceScope)) {
            return new ResourceDescriptor(Institution.class, InstitutionClientRepresentation.class, PrismScope.INSTITUTION);
        } else if ("systems".equals(resourceScope)) {
            return new ResourceDescriptor(System.class, SystemClientRepresentation.class, PrismScope.SYSTEM);
        }
        String errorMessage = "Unknown resource scope " + resourceScope;
        logger.error(errorMessage);
        throw new ResourceNotFoundException(errorMessage);
    }

}
