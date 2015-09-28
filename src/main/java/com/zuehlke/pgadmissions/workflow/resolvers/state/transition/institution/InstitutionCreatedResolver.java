package com.zuehlke.pgadmissions.workflow.resolvers.state.transition.institution;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.SYSTEM_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.INSTITUTION_APPROVAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.INSTITUTION_APPROVED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.INSTITUTION_UNSUBMITTED;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.StateTransitionResolver;

@Component
public class InstitutionCreatedResolver implements StateTransitionResolver<Institution> {

    @Inject
    private RoleService roleService;

    @Inject
    private StateService stateService;

    @Override
    public StateTransition resolve(Institution resource, Comment comment) {
        State initialState = comment.getTransitionState();
        Resource parentResource = resource.getParentResource();
        if (initialState == null) {
            if (roleService.hasUserRole(resource, comment.getUser(), SYSTEM_ADMINISTRATOR)) {
                return stateService.getStateTransition(parentResource, comment.getAction(), INSTITUTION_APPROVED);
            }
            return stateService.getStateTransition(parentResource, comment.getAction(), INSTITUTION_APPROVAL);
        }
        return stateService.getStateTransition(parentResource, comment.getAction(), INSTITUTION_UNSUBMITTED);
    }

}
