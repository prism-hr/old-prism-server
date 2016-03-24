package uk.co.alumeni.prism.workflow.user;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.comment.CommentAssignedUser;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.services.UserService;

@Component
public class CommentAssignedUserReassignmentProcessor implements PrismUserReassignmentProcessor {

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
