package uk.co.alumeni.prism.workflow.resolvers.role.transition;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.user.UserRole;
import uk.co.alumeni.prism.exceptions.DeduplicationException;
import uk.co.alumeni.prism.services.EntityService;
import uk.co.alumeni.prism.services.RoleService;

@Component
public class UpdateResolver implements RoleTransitionResolver {

    @Inject
    private EntityService entityService;

    @Inject
    private RoleService roleService;

    @Override
    public void resolve(UserRole userRole, UserRole transitionUserRole, Comment comment) throws DeduplicationException {
        roleService.updateUserRole(userRole, transitionUserRole, comment);
        entityService.flush();
    }

}
