package com.zuehlke.pgadmissions.workflow.resolvers.state.transition.project;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROGRAM_ADVERTISER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_APPROVAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_APPROVAL_INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_APPROVED;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.StateTransitionResolver;

@Component
public class ProjectCreatedResolver implements StateTransitionResolver {

    @Inject
    private RoleService roleService;

    @Inject
    private StateService stateService;

    @Override
    public StateTransition resolve(Resource resource, Comment comment) {
        User user = comment.getUser();
        Institution institution = resource.getInstitution();
        List<PrismState> activeInstitutionStates = stateService.getActiveInstitutionStates();
        if (!activeInstitutionStates.contains(institution.getState().getId())) {
            return stateService.getStateTransition(resource.getParentResource(), comment.getAction(), PROJECT_APPROVAL_INSTITUTION);
        } else if (roleService.hasUserRole(resource, user, PROGRAM_ADVERTISER_GROUP)) {
            return stateService.getStateTransition(resource.getParentResource(), comment.getAction(), PROJECT_APPROVED);
        }
        return stateService.getStateTransition(resource.getParentResource(), comment.getAction(), PROJECT_APPROVAL);
    }

}
