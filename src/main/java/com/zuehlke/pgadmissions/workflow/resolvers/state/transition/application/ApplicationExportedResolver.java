package com.zuehlke.pgadmissions.workflow.resolvers.state.transition.application;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.StateTransitionResolver;

@Component
public class ApplicationExportedResolver implements StateTransitionResolver<Application> {

    @Inject
    private StateService stateService;

    @Override
    public StateTransition resolve(Application resource, Comment comment) {
        String exportException = comment.getExport().getExportException();
        if (exportException == null) {
            return stateService.getStateTransition(resource, comment.getAction(),
                    PrismState.valueOf(resource.getState().getStateGroup().getId() + "_COMPLETED"));
        }
        return stateService.getStateTransition(resource, comment.getAction(),
                PrismState.valueOf(resource.getState().getStateGroup().getId() + "_PENDING_CORRECTION"));
    }

}
