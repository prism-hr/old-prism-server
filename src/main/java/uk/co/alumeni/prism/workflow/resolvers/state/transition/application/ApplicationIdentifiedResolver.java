package uk.co.alumeni.prism.workflow.resolvers.state.transition.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_VALIDATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_VALIDATION_PENDING_COMPLETION;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.workflow.StateTransition;
import uk.co.alumeni.prism.services.StateService;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.StateTransitionResolver;

@Component
public class ApplicationIdentifiedResolver implements StateTransitionResolver<Application> {

    @Inject
    private StateService stateService;

    @Override
    public StateTransition resolve(Application resource, Comment comment) {
        LocalDate closingDate = resource.getApplication().getClosingDate();
        if (closingDate == null || closingDate.isBefore(new LocalDate())) {
            return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_VALIDATION_PENDING_COMPLETION);
        }
        return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_VALIDATION);
    }

}
