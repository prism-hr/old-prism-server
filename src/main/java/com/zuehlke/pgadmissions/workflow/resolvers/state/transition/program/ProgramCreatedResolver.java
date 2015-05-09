package com.zuehlke.pgadmissions.workflow.resolvers.state.transition.program;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.INSTIUTTION_ADVERTISER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROGRAM_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_APPROVAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_APPROVAL_PARTNER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_APPROVAL_PARTNER_INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_APPROVED;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.StateTransitionResolver;

@Component
public class ProgramCreatedResolver implements StateTransitionResolver {

	@Inject
	private RoleService roleService;

	@Inject
	private StateService stateService;

    @Override
    public StateTransition resolve(Resource resource, Comment comment) {
        User user = comment.getUser();
        if (comment.isPartnershipComment()) {
            Institution partner = resource.getPartner();
            List<PrismState> activeInstitutionStates = stateService.getActiveInstitutionStates();
            if (!activeInstitutionStates.contains(partner.getState().getId())) {
                return stateService.getStateTransition(resource.getParentResource(), comment.getAction(), PROGRAM_APPROVAL_PARTNER_INSTITUTION);
            } else if (roleService.hasUserRole(partner, user, INSTIUTTION_ADVERTISER_GROUP)) {
                return resolveProgramTransition(resource, comment, user);
            }
            return stateService.getStateTransition(resource.getParentResource(), comment.getAction(), PROGRAM_APPROVAL_PARTNER);
        } else {
            return resolveProgramTransition(resource, comment, user);
        }
    }

    public StateTransition resolveProgramTransition(Resource resource, Comment comment, User user) {
        if (roleService.hasUserRole(resource, user, PROGRAM_ADMINISTRATOR_GROUP)) {
            return stateService.getStateTransition(resource.getParentResource(), comment.getAction(), PROGRAM_APPROVED);
        }
        return stateService.getStateTransition(resource.getParentResource(), comment.getAction(), PROGRAM_APPROVAL);
    }

}
