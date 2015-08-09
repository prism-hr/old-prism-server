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
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.domain.resource.Project;
import com.zuehlke.pgadmissions.domain.resource.System;
import com.zuehlke.pgadmissions.domain.resource.department.Department;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceOpportunityRepresentationClient;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceParentDivisionRepresentationClient;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationRepresentationClient;

public class RestUtils {

    private static final Logger logger = LoggerFactory.getLogger(RestUtils.class);

    public static ResourceDescriptor getResourceDescriptor(String resourceScope) {
        if ("applications".equals(resourceScope)) {
            return new ResourceDescriptor(Application.class, ApplicationRepresentationClient.class, APPLICATION);
        } else if ("projects".equals(resourceScope)) {
            return new ResourceDescriptor(Project.class, ResourceOpportunityRepresentationClient.class, PROJECT);
        } else if ("programs".equals(resourceScope)) {
            return new ResourceDescriptor(Program.class, ResourceOpportunityRepresentationClient.class, PROGRAM);
        } else if ("departments".equals(resourceScope)) {
            return new ResourceDescriptor(Department.class, ResourceParentDivisionRepresentationClient.class, DEPARTMENT);
        } else if ("institutions".equals(resourceScope)) {
            return new ResourceDescriptor(Institution.class, ResourceParentDivisionRepresentationClient.class, INSTITUTION);
        } else if ("systems".equals(resourceScope)) {
            return new ResourceDescriptor(System.class, ResourceRepresentationSimple.class, SYSTEM);
        }
        String errorMessage = "Unknown resource scope " + resourceScope;
        logger.error(errorMessage);
        throw new ResourceNotFoundException(errorMessage);
    }

}
