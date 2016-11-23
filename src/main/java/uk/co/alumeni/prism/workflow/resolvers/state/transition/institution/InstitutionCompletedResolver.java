package uk.co.alumeni.prism.workflow.resolvers.state.transition.institution;

import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.resource.Institution;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.workflow.State;
import uk.co.alumeni.prism.domain.workflow.StateTransition;
import uk.co.alumeni.prism.services.RoleService;
import uk.co.alumeni.prism.services.StateService;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.StateTransitionResolver;

import javax.inject.Inject;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.INSTITUTION_ADMINISTRATOR;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.INSTITUTION_APPROVAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.INSTITUTION_APPROVED;

@Component
public class InstitutionCompletedResolver implements StateTransitionResolver<Institution> {

    @Inject
    private RoleService roleService;

    @Inject
    private StateService stateService;

    @Override
    public StateTransition resolve(Institution resource, Comment comment) {
        State transitionState = comment.getTransitionState();
        if (transitionState == null) {
            User user = comment.getUser();
            if (roleService.hasUserRole(resource, user, INSTITUTION_ADMINISTRATOR) || roleService.createsUserRole(comment, user, INSTITUTION_ADMINISTRATOR)) {
                return stateService.getStateTransition(resource, comment.getAction(), INSTITUTION_APPROVED);
            }
            return stateService.getStateTransition(resource, comment.getAction(), INSTITUTION_APPROVAL);
        }
        return stateService.getPredefinedStateTransition(resource, comment);
    }

}
