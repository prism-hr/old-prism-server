package uk.co.alumeni.prism.workflow.resolvers.state.termination;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.APPLICATION_REFEREE;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.services.UserService;

@Component
public class ApplicationProvidedReferenceTerminationResolver implements StateTerminationResolver<Application> {

    @Inject
    private UserService userService;

    @Override
    public boolean resolve(Application resource) {
        return userService.getUsersWithRoles(resource, APPLICATION_REFEREE).size() == 1;
    }

}
