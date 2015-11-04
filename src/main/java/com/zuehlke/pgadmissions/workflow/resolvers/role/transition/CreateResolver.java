package com.zuehlke.pgadmissions.workflow.resolvers.role.transition;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.RoleService;

@Component
public class CreateResolver implements RoleTransitionResolver {

    @Inject
    private EntityService entityService;
    
    @Inject
    private RoleService roleService;

    @Override
    public void resolve(UserRole userRole, UserRole transitionUserRole, Comment comment) throws DeduplicationException {
        roleService.getOrCreateUserRole(transitionUserRole);
        comment.addAssignedUser(transitionUserRole.getUser(), transitionUserRole.getRole(), CREATE);
        entityService.flush();
    }

}
