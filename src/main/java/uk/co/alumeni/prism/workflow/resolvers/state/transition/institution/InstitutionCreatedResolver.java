package uk.co.alumeni.prism.workflow.resolvers.state.transition.institution;

import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.resource.Institution;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.workflow.State;
import uk.co.alumeni.prism.domain.workflow.StateTransition;
import uk.co.alumeni.prism.services.RoleService;
import uk.co.alumeni.prism.services.StateService;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.StateTransitionResolver;

import javax.inject.Inject;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.SYSTEM_ADMINISTRATOR;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.*;

@Component
public class InstitutionCreatedResolver implements StateTransitionResolver<Institution> {

    @Inject
    private RoleService roleService;

    @Inject
    private StateService stateService;

    @Override
    public StateTransition resolve(Institution resource, Comment comment) {
        State initialState = comment.getTransitionState();
        Resource parentResource = resource.getParentResource();
        if (initialState == null) {
            if (roleService.hasUserRole(resource, comment.getUser(), SYSTEM_ADMINISTRATOR)) {
                return stateService.getStateTransition(parentResource, comment.getAction(), INSTITUTION_APPROVED);
            }
            return stateService.getStateTransition(parentResource, comment.getAction(), INSTITUTION_APPROVAL);
        }
        return stateService.getStateTransition(parentResource, comment.getAction(), INSTITUTION_UNSUBMITTED);
    }

}
