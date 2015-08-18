package com.zuehlke.pgadmissions.workflow.resolvers.state.transition.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_POTENTIAL_INTERVIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_AVAILABILITY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_SCHEDULING;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.StateTransitionResolver;

@Component
public class ApplicationProvidedInterviewAvailabilityResolver implements StateTransitionResolver<Application> {

    @Inject
    private RoleService roleService;

    @Inject
    private StateService stateService;

    @Override
    public StateTransition resolve(Application resource, Comment comment) {
        if (roleService.getRoleUsers(resource, APPLICATION_POTENTIAL_INTERVIEWEE, APPLICATION_POTENTIAL_INTERVIEWER).size() == 1) {
            return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_INTERVIEW_PENDING_SCHEDULING);
        }
        return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_INTERVIEW_PENDING_AVAILABILITY);
    }

}
