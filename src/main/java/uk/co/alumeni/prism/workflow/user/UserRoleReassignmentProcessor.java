package uk.co.alumeni.prism.workflow.user;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserRole;
import uk.co.alumeni.prism.services.UserService;

@Component
public class UserRoleReassignmentProcessor implements PrismUserReassignmentProcessor {

    @Inject
    private UserService userService;

    @Override
    public void reassign(User oldUser, User newUser, String userProperty) {
        for (UserRole oldUserRole : oldUser.getUserRoles()) {
            userService.mergeUserAssignmentStrict(oldUserRole, newUser, userProperty);
        }
    }

}
