package uk.co.alumeni.prism.workflow.user;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.services.UserService;

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
