package uk.co.alumeni.prism.workflow.resolvers.state.transition.program;

import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.resource.Program;
import uk.co.alumeni.prism.domain.workflow.StateTransition;
import uk.co.alumeni.prism.services.StateService;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.StateTransitionResolver;

import javax.inject.Inject;

@Component
public class ProgramApprovedResolver implements StateTransitionResolver<Program> {

    @Inject
    private StateService stateService;

    @Override
    public StateTransition resolve(Program resource, Comment comment) {
        return stateService.getPredefinedStateTransition(resource, comment);
    }

}
