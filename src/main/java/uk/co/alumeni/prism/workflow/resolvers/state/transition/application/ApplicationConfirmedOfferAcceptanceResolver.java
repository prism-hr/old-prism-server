package uk.co.alumeni.prism.workflow.resolvers.state.transition.application;

import static org.apache.commons.lang.BooleanUtils.isTrue;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_ACCEPTED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_APPROVED_PENDING_OFFER_REVISION;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.workflow.StateTransition;
import uk.co.alumeni.prism.services.StateService;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.StateTransitionResolver;

@Component
public class ApplicationConfirmedOfferAcceptanceResolver implements StateTransitionResolver<Application> {

    @Inject
    private StateService stateService;

    @Override
    public StateTransition resolve(Application resource, Comment comment) {
        if (isTrue(comment.getApplicantAcceptAppointment())) {
            return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_ACCEPTED);
        }
        return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_APPROVED_PENDING_OFFER_REVISION);
    }

}
