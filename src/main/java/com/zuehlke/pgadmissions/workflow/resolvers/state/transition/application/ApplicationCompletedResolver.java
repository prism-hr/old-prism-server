package com.zuehlke.pgadmissions.workflow.resolvers.state.transition.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_VALIDATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_VALIDATION_PENDING_COMPLETION;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.StateTransitionResolver;

@Component
public class ApplicationCompletedResolver implements StateTransitionResolver<Application> {

    @Inject
    private StateService stateService;

    @Override
    public StateTransition resolve(Application resource, Comment comment) {
        if (resource.getSubmittedTimestamp() == null) {
            return stateService.getStateTransition(resource, comment.getAction(), resource.getState().getId());
        } else {
            LocalDate closingDate = resource.getApplication().getClosingDate();
            if (closingDate == null || closingDate.isBefore(new LocalDate())) {
                return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_VALIDATION_PENDING_COMPLETION);
            }
            return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_VALIDATION);
        }
    }

}
