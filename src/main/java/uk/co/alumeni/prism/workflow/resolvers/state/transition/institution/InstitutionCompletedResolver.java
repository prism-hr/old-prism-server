package uk.co.alumeni.prism.workflow.resolvers.state.transition.institution;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.INSTITUTION_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.INSTITUTION_APPROVAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.INSTITUTION_APPROVED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.INSTITUTION_UNSUBMITTED;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.resource.Institution;
import uk.co.alumeni.prism.domain.workflow.State;
import uk.co.alumeni.prism.domain.workflow.StateTransition;
import uk.co.alumeni.prism.services.RoleService;
import uk.co.alumeni.prism.services.StateService;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.StateTransitionResolver;

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
            if (roleService.hasUserRole(resource, comment.getUser(), INSTITUTION_ADMINISTRATOR_GROUP)) {
                return stateService.getStateTransition(resource, comment.getAction(), INSTITUTION_APPROVED);
            }
            return stateService.getStateTransition(resource, comment.getAction(), INSTITUTION_APPROVAL);
        }
        return stateService.getStateTransition(resource, comment.getAction(), INSTITUTION_UNSUBMITTED);
    }

}
