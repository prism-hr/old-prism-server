package com.zuehlke.pgadmissions.workflow.user;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.services.UserService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class CommentReassignmentProcessor implements PrismUserReassignmentProcessor {

    @Inject
    private UserService userService;

    @Override
    public void reassign(User oldUser, User newUser, String userProperty) {
        for (Comment oldComment : oldUser.getComments()) {
            if (!oldComment.isResourceUserAssignmentProperty()) {
                userService.mergeUserAssignmentStrict(oldComment, newUser, userProperty);
            }
        }
    }

}
