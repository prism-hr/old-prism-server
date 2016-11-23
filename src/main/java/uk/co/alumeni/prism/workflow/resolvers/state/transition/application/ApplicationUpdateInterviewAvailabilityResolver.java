package uk.co.alumeni.prism.workflow.resolvers.state.transition.application;

import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.workflow.StateTransition;
import uk.co.alumeni.prism.services.StateService;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.StateTransitionResolver;

import javax.inject.Inject;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_SCHEDULING;

@Component
public class ApplicationUpdateInterviewAvailabilityResolver implements StateTransitionResolver<Application> {

    @Inject
    private StateService stateService;

    @Override
    public StateTransition resolve(Application resource, Comment comment) {
        if (stateService.getPreviousPrimaryState(resource, resource.getState().getId()).equals(APPLICATION_INTERVIEW)) {
            return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_INTERVIEW);
        }
        return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_INTERVIEW_PENDING_SCHEDULING);
    }

}
