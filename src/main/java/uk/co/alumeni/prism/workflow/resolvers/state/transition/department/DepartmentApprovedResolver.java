package uk.co.alumeni.prism.workflow.resolvers.state.transition.department;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.resource.Department;
import uk.co.alumeni.prism.domain.workflow.StateTransition;
import uk.co.alumeni.prism.services.StateService;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.StateTransitionResolver;

@Component
public class DepartmentApprovedResolver implements StateTransitionResolver<Department> {

	@Inject
	private StateService stateService;

	@Override
	public StateTransition resolve(Department resource, Comment comment) {
		return stateService.getPredefinedStateTransition(resource, comment);
	}

}
