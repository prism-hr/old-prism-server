package uk.co.alumeni.prism.workflow.user;

import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserEmploymentPosition;
import uk.co.alumeni.prism.services.UserService;

import javax.inject.Inject;

@Component
public class UserEmploymentPositionReassignmentProcessor implements PrismUserReassignmentProcessor {

    @Inject
    private UserService userService;

    @Override
    public void reassign(User oldUser, User newUser, String userProperty) {
        for (UserEmploymentPosition oldUserEmploymentPosition : oldUser.getUserEmploymentPositions()) {
            userService.mergeUserAssignmentStrict(oldUserEmploymentPosition, newUser, userProperty);
        }
    }

}
