package com.zuehlke.pgadmissions.workflow.resolvers.state.transition.resume;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Resume;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.StateTransitionResolver;

@Component
public class ResumeUpdatedResolver implements StateTransitionResolver<Resume> {

    @Inject
    private StateService stateService;

    @Override
    public StateTransition resolve(Resume resource, Comment comment) {
        return stateService.getPredefinedOrCurrentStateTransition(resource, comment);
    }

}
