package com.zuehlke.pgadmissions.workflow.user;

import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.services.UserService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class CommentAssignmentUserReassignmentProcessor implements PrismUserReassignmentProcessor {

    @Inject
    private UserService userService;

    @Override
    public void reassign(User oldUser, User newUser, String userProperty) {
        for (CommentAssignedUser oldCommentAssignedUser : oldUser.getCommentAssignedUsers()) {
            if (!oldCommentAssignedUser.isResourceUserAssignmentProperty()) {
                userService.mergeUserAssignmentStrict(oldCommentAssignedUser, newUser, userProperty);
            }
        }
    }

}
