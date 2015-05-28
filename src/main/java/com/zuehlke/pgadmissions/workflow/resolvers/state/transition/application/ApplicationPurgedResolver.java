package com.zuehlke.pgadmissions.workflow.resolvers.state.transition.application;

import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.StateTransitionResolver;

@Component
public class ApplicationPurgedResolver implements StateTransitionResolver {

    @Inject
    private StateService stateService;

    @Override
    public StateTransition resolve(Resource resource, Comment comment) {
        if (BooleanUtils.isTrue(resource.getApplication().getRetain())) {
            return stateService.getStateTransition(resource, comment.getAction(), PrismState.valueOf(resource.getState().getId() + "_RETAINED"));
        } else {
            return stateService.getStateTransition(resource, comment.getAction(), PrismState.valueOf(resource.getState().getId() + "_PURGED"));
        }
    }

}
