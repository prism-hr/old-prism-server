package com.zuehlke.pgadmissions.workflow.resolvers.state.transition.department;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.department.Department;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.StateTransitionResolver;

@Component
public class DepartmentUpdatedResolver implements StateTransitionResolver<Department> {

    @Inject
    private StateService stateService;

    @Override
    public StateTransition resolve(Department resource, Comment comment) {
        return stateService.getPredefinedOrCurrentStateTransition(resource, comment);
    }

}