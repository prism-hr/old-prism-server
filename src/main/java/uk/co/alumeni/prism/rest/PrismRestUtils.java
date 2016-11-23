package uk.co.alumeni.prism.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.resource.*;
import uk.co.alumeni.prism.domain.resource.System;
import uk.co.alumeni.prism.exceptions.ResourceNotFoundException;
import uk.co.alumeni.prism.rest.representation.resource.DepartmentRepresentationClient;
import uk.co.alumeni.prism.rest.representation.resource.ResourceOpportunityRepresentationClient;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationSimple;
import uk.co.alumeni.prism.rest.representation.resource.application.ApplicationRepresentationClient;
import uk.co.alumeni.prism.rest.representation.resource.institution.InstitutionRepresentationClient;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.*;

public class PrismRestUtils {

    private static final Logger logger = LoggerFactory.getLogger(PrismRestUtils.class);

    public static ResourceDescriptor getResourceDescriptor(String resourceScope) {
        if ("applications".equals(resourceScope)) {
            return new ResourceDescriptor(Application.class, ApplicationRepresentationClient.class, APPLICATION);
        } else if ("projects".equals(resourceScope)) {
            return new ResourceDescriptor(Project.class, ResourceOpportunityRepresentationClient.class, PROJECT);
        } else if ("programs".equals(resourceScope)) {
            return new ResourceDescriptor(Program.class, ResourceOpportunityRepresentationClient.class, PROGRAM);
        } else if ("departments".equals(resourceScope)) {
            return new ResourceDescriptor(Department.class, DepartmentRepresentationClient.class, DEPARTMENT);
        } else if ("institutions".equals(resourceScope)) {
            return new ResourceDescriptor(Institution.class, InstitutionRepresentationClient.class, INSTITUTION);
        } else if ("systems".equals(resourceScope)) {
            return new ResourceDescriptor(System.class, ResourceRepresentationSimple.class, SYSTEM);
        }
        String errorMessage = "Unknown resource scope " + resourceScope;
        logger.error(errorMessage);
        throw new ResourceNotFoundException(errorMessage);
    }

}
