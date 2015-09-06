package com.zuehlke.pgadmissions.workflow.resolvers.state.termination;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_REFEREE;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.services.RoleService;

@Component
public class ApplicationProvidedReferenceTerminationResolver implements StateTerminationResolver<Application> {

    @Inject
    private RoleService roleService;

    @Override
    public boolean resolve(Application resource) {
        return roleService.getRoleUsers(resource, APPLICATION_REFEREE).size() == 1;
    }

}
