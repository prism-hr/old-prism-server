package com.zuehlke.pgadmissions.workflow.resolvers.role.transition;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.services.RoleService;

@Component
public class ExhumeResolver implements RoleTransitionResolver {

	@Inject
	private RoleService roleService;

	@Override
	public void resolve(UserRole userRole, UserRole transitionUserRole, Comment comment) throws DeduplicationException {
		roleService.updateUserRole(userRole, transitionUserRole, comment);
	}

}
