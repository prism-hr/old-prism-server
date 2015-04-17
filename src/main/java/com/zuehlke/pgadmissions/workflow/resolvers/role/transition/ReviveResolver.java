package com.zuehlke.pgadmissions.workflow.resolvers.role.transition;

import com.zuehlke.pgadmissions.dao.NotificationDAO;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.services.EntityService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class ReviveResolver implements RoleTransitionResolver {

    @Inject
    private EntityService entityService;

    @Inject
    private NotificationDAO notificationDAO;

    @Override
    public void resolve(UserRole userRole, UserRole transitionUserRole, Comment comment) throws DeduplicationException {
        UserRole persistentRole = entityService.getDuplicateEntity(userRole);
        notificationDAO.removeUserNotifications(persistentRole);
    }

}
