package com.zuehlke.pgadmissions.workflow.resolvers.role.transition;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;

public interface RoleTransitionResolver {

	void resolve(UserRole userRole, UserRole transitionUserRole, Comment comment) throws DeduplicationException;

}
