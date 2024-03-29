package uk.co.alumeni.prism.workflow.resolvers.state.transition.project;

import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.resource.Project;
import uk.co.alumeni.prism.domain.workflow.StateTransition;
import uk.co.alumeni.prism.services.StateService;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.StateTransitionResolver;

import javax.inject.Inject;

@Component
public class ProjectUpdatedResolver implements StateTransitionResolver<Project> {

    @Inject
    private StateService stateService;

    @Override
    public StateTransition resolve(Project resource, Comment comment) {
        return stateService.getPredefinedOrCurrentStateTransition(resource, comment);
    }

}
