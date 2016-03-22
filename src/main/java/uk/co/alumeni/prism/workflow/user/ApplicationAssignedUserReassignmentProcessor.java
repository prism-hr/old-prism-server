package uk.co.alumeni.prism.workflow.user;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.application.ApplicationHiringManager;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.services.UserService;

@Component
public class ApplicationAssignedUserReassignmentProcessor implements PrismUserReassignmentProcessor {

    @Inject
    private UserService userService;

    @Override
    public void reassign(User oldUser, User newUser, String userProperty) {
        for (ApplicationHiringManager oldApplicationAssignedUser : oldUser.getApplicationHiringManagers()) {
            if (!oldApplicationAssignedUser.isResourceUserAssignmentProperty()) {
                userService.mergeUserAssignmentStrict(oldApplicationAssignedUser, newUser, userProperty);
            }
        }
    }

}
