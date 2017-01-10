package uk.co.alumeni.prism.workflow.resolvers.state.transition.department;

import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.resource.Department;
import uk.co.alumeni.prism.domain.workflow.StateTransition;
import uk.co.alumeni.prism.services.StateService;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.StateTransitionResolver;

import javax.inject.Inject;

@Component
public class DepartmentUpdatedResolver implements StateTransitionResolver<Department> {

    @Inject
    private StateService stateService;

    @Override
    public StateTransition resolve(Department resource, Comment comment) {
        return stateService.getPredefinedOrCurrentStateTransition(resource, comment);
    }

}
