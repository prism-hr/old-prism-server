package com.zuehlke.pgadmissions.workflow.resolvers.state.transition.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_FEEDBACK;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_INTERVIEW;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.StateTransitionResolver;

@Component
public class ApplicationConfirmedInterviewResolver implements StateTransitionResolver<Application> {

    @Inject
    private StateService stateService;

    @Override
    public StateTransition resolve(Application resource, Comment comment) {
        DateTime baseline = new DateTime();
        if (comment.isApplicationInterviewRecordedComment(baseline)) {
            return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_INTERVIEW_PENDING_FEEDBACK);
        } else if (comment.isApplicationInterviewScheduledComment(baseline)) {
            return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_INTERVIEW_PENDING_INTERVIEW);
        }
        return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_INTERVIEW);
    }

}
