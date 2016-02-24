package uk.co.alumeni.prism.workflow.resolvers.state.transition.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_FEEDBACK;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_INTERVIEW;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.workflow.StateTransition;
import uk.co.alumeni.prism.services.StateService;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.StateTransitionResolver;

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
