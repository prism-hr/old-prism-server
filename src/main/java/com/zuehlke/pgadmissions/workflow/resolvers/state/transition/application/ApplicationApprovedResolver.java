package com.zuehlke.pgadmissions.workflow.resolvers.state.transition.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_APPROVED_COMPLETED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_APPROVED_PENDING_EXPORT;

import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.StateTransitionResolver;

@Component
public class ApplicationApprovedResolver implements StateTransitionResolver<Application> {

    @Inject
    private StateService stateService;

    @Override
    public StateTransition resolve(Application resource, Comment comment) {
        if (BooleanUtils.isTrue(resource.getAdvert().isImported())) {
            return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_APPROVED_PENDING_EXPORT);
        } else {
            return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_APPROVED_COMPLETED);
        }
    }

}
