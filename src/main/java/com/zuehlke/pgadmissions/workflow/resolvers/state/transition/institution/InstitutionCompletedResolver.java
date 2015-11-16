package com.zuehlke.pgadmissions.workflow.resolvers.state.transition.institution;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.INSTITUTION_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.INSTITUTION_APPROVAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.INSTITUTION_APPROVED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.INSTITUTION_UNSUBMITTED;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.StateTransitionResolver;

@Component
public class InstitutionCompletedResolver implements StateTransitionResolver<Institution> {

    @Inject
    private RoleService roleService;

    @Inject
    private StateService stateService;

    @Override
    public StateTransition resolve(Institution resource, Comment comment) {
        State transitionState = comment.getTransitionState();
        if (transitionState == null) {
            if (roleService.hasUserRole(resource, comment.getUser(), INSTITUTION_ADMINISTRATOR_GROUP)) {
                return stateService.getStateTransition(resource, comment.getAction(), INSTITUTION_APPROVED);
            }
            return stateService.getStateTransition(resource, comment.getAction(), INSTITUTION_APPROVAL);
        }
        return stateService.getStateTransition(resource, comment.getAction(), INSTITUTION_UNSUBMITTED);
    }

}
