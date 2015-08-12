package com.zuehlke.pgadmissions.workflow.user;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.services.UserService;

@Component
public class CommentReassignmentProcessor implements PrismUserReassignmentProcessor {

    @Inject
    private UserService userService;

    @Override
    public void reassign(User oldUser, User newUser, String userProperty) throws Exception {
        for (Comment oldComment : oldUser.getComments()) {
            if (!oldComment.isResourceUserAssignmentProperty()) {
                userService.mergeUserAssignmentStrict(oldComment, newUser, userProperty);
            }
        }
    }

}
