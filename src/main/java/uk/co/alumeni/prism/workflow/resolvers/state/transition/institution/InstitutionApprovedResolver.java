package uk.co.alumeni.prism.workflow.resolvers.state.transition.institution;

import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.resource.Institution;
import uk.co.alumeni.prism.domain.workflow.StateTransition;
import uk.co.alumeni.prism.services.StateService;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.StateTransitionResolver;

import javax.inject.Inject;

@Component
public class InstitutionApprovedResolver implements StateTransitionResolver<Institution> {

    @Inject
    private StateService stateService;

    @Override
    public StateTransition resolve(Institution resource, Comment comment) {
        return stateService.getPredefinedStateTransition(resource, comment);
    }

}
