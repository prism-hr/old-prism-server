package uk.co.alumeni.prism.workflow.resolvers.state.transition.application;

import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.workflow.StateTransition;
import uk.co.alumeni.prism.services.StateService;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.StateTransitionResolver;

@Component
public class ApplicationPurgedResolver implements StateTransitionResolver<Application> {

    @Inject
    private StateService stateService;

    @Override
    public StateTransition resolve(Application resource, Comment comment) {
        if (BooleanUtils.isTrue(resource.getApplication().getShared())) {
            return stateService.getStateTransition(resource, comment.getAction(), PrismState.valueOf(resource.getState().getId() + "_RETAINED"));
        }
        return stateService.getStateTransition(resource, comment.getAction(), PrismState.valueOf(resource.getState().getId() + "_PURGED"));
    }

}
