package uk.co.alumeni.prism.workflow.resolvers.role.transition;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionType.CREATE;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.user.UserRole;
import uk.co.alumeni.prism.exceptions.DeduplicationException;
import uk.co.alumeni.prism.services.EntityService;
import uk.co.alumeni.prism.services.RoleService;

@Component
public class BranchResolver implements RoleTransitionResolver {

	@Inject
	private EntityService entityService;

	@Inject
	private RoleService roleService;

	@Override
	public void resolve(UserRole userRole, UserRole transitionUserRole, Comment comment) throws DeduplicationException {
		UserRole persistentRole = entityService.getDuplicateEntity(userRole);
		if (persistentRole != null) {
			comment.addAssignedUser(transitionUserRole.getUser(), transitionUserRole.getRole(), CREATE);
			roleService.getOrCreateUserRole(transitionUserRole);
	        entityService.flush();
		}
	}

}
