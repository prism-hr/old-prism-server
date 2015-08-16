package com.zuehlke.pgadmissions.workflow.resolvers.state.transition.application;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.StateTransitionResolver;

@Component
public class ApplicationTerminatedResolver implements StateTransitionResolver<Application> {

    @Inject
    private ApplicationStateTransitionEvaluation applicationStateTransitionEvaluation;

    @Override
    public StateTransition resolve(Application resource, Comment comment) {
        return applicationStateTransitionEvaluation.resolveApplicationRejectedOrTerminatedTransition(resource, comment);
    }

}
