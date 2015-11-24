package uk.co.alumeni.prism.workflow.resolvers.role.transition;

import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.user.UserRole;
import uk.co.alumeni.prism.exceptions.DeduplicationException;

public interface RoleTransitionResolver {

	void resolve(UserRole userRole, UserRole transitionUserRole, Comment comment) throws DeduplicationException;

}
