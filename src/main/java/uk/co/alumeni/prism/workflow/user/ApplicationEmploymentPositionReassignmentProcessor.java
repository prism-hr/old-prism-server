package uk.co.alumeni.prism.workflow.user;

import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.domain.application.ApplicationEmploymentPosition;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.services.UserService;

import javax.inject.Inject;

@Component
public class ApplicationEmploymentPositionReassignmentProcessor implements PrismUserReassignmentProcessor {

    @Inject
    private UserService userService;

    @Override
    public void reassign(User oldUser, User newUser, String userProperty) {
        for (ApplicationEmploymentPosition oldApplicationEmploymentPosition : oldUser.getApplicationEmploymentPositions()) {
            userService.mergeUserAssignmentStrict(oldApplicationEmploymentPosition, newUser, userProperty);
        }
    }

}
