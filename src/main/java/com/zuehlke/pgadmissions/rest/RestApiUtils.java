package com.zuehlke.pgadmissions.rest;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.rest.representation.resource.InstitutionExtendedRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ProgramExtendedRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ProjectExtendedRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.SystemExtendedRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationExtendedRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestApiUtils {

    private static final Logger logger = LoggerFactory.getLogger(RestApiUtils.class);

    public static ResourceDescriptor getResourceDescriptor(String resourceScope) {
        if ("applications".equals(resourceScope)) {
            return new ResourceDescriptor(Application.class, ApplicationExtendedRepresentation.class, PrismScope.APPLICATION);
        } else if ("projects".equals(resourceScope)) {
            return new ResourceDescriptor(Project.class, ProjectExtendedRepresentation.class, PrismScope.PROJECT);
        } else if ("programs".equals(resourceScope)) {
            return new ResourceDescriptor(Program.class, ProgramExtendedRepresentation.class, PrismScope.PROGRAM);
        } else if ("institutions".equals(resourceScope)) {
            return new ResourceDescriptor(Institution.class, InstitutionExtendedRepresentation.class, PrismScope.INSTITUTION);
        } else if ("systems".equals(resourceScope)) {
            return new ResourceDescriptor(com.zuehlke.pgadmissions.domain.System.class, SystemExtendedRepresentation.class, PrismScope.SYSTEM);
        }
        logger.error("Unknown resource scope " + resourceScope);
        throw new ResourceNotFoundException();
    }
}
