package com.zuelhke.pgadmissions.workflow.resolvers.role.transition;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.DELETE;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.RoleService;

@Component
public class RoleTransitionResolverExhume implements RoleTransitionResolver {

	@Inject
	private EntityService entityService;
	
	@Inject
	private RoleService roleService;
	
	@Override
	public void resolve(UserRole userRole, UserRole transitionUserRole, Comment comment) throws DeduplicationException {
		UserRole persistentRole = entityService.getDuplicateEntity(userRole);
		if (persistentRole != null) {
			comment.addAssignedUser(userRole.getUser(), userRole.getRole(), DELETE);
			comment.addAssignedUser(transitionUserRole.getUser(), transitionUserRole.getRole(), CREATE);
			roleService.deleteUserRole(persistentRole.getResource(), persistentRole.getUser(), persistentRole.getRole());
			roleService.getOrCreateUserRole(transitionUserRole);
		}
	}

}
