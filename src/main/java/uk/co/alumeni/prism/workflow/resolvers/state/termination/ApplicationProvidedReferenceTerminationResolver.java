package uk.co.alumeni.prism.workflow.resolvers.state.termination;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.APPLICATION_REFEREE;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.services.RoleService;

@Component
public class ApplicationProvidedReferenceTerminationResolver implements StateTerminationResolver<Application> {

    @Inject
    private RoleService roleService;

    @Override
    public boolean resolve(Application resource) {
        return roleService.getRoleUsers(resource, APPLICATION_REFEREE).size() == 1;
    }

}
