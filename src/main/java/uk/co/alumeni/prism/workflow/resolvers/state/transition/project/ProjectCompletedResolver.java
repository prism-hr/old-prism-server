package uk.co.alumeni.prism.workflow.resolvers.state.transition.project;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.PROJECT_APPROVAL_PARENT_APPROVAL;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.resource.Project;
import uk.co.alumeni.prism.domain.workflow.StateTransition;
import uk.co.alumeni.prism.services.ResourceService;
import uk.co.alumeni.prism.services.StateService;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.StateTransitionResolver;

@Component
public class ProjectCompletedResolver implements StateTransitionResolver<Project> {

    @Inject
    private ResourceService resourceService;

    @Inject
    private StateService stateService;

    @Override
    public StateTransition resolve(Project resource, Comment comment) {
        if (resourceService.isUnderApproval(resource)) {
            return stateService.getStateTransition(resource, comment.getAction(), PROJECT_APPROVAL_PARENT_APPROVAL);
        }
        return stateService.getPredefinedStateTransition(resource, comment);
    }

}
