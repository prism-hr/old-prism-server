package com.zuehlke.pgadmissions.workflow.resolvers.state.transition.project;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.StateTransitionResolver;

@Component
public class ProjectUpdatedResolver implements StateTransitionResolver {

    @Inject
    private StateService stateService;

    @Inject
    private ProjectCreatedResolver projectCreatedResolver;

    @Override
    public StateTransition resolve(Resource resource, Comment comment) {
        if (comment.isPartnershipComment()) {
            return projectCreatedResolver.resolve(resource, comment);
        }
        return stateService.getPredefinedOrCurrentStateTransition(resource, comment);
    }

}
