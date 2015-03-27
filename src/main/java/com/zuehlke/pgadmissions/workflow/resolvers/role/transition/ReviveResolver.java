package com.zuehlke.pgadmissions.workflow.resolvers.role.transition;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.services.EntityService;

@Component
public class ReviveResolver implements RoleTransitionResolver {

	@Inject
	private EntityService entityService;
	
	@Override
	public void resolve(UserRole userRole, UserRole transitionUserRole, Comment comment) throws DeduplicationException {
		UserRole persistentRole = entityService.getDuplicateEntity(userRole);
		if (persistentRole != null) {
			persistentRole.setLastNotifiedDate(null);
		}
	}

}
