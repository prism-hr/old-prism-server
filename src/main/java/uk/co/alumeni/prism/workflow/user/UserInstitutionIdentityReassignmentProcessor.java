package uk.co.alumeni.prism.workflow.user;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserInstitutionIdentity;
import uk.co.alumeni.prism.services.UserService;

@Component
public class UserInstitutionIdentityReassignmentProcessor implements PrismUserReassignmentProcessor {

    @Inject
    private UserService userService;

    @Override
    public void reassign(User oldUser, User newUser, String userProperty) {
        for (UserInstitutionIdentity oldUserInstitutionIdentity : oldUser.getInstitutionIdentities()) {
            userService.mergeUserAssignmentStrict(oldUserInstitutionIdentity, newUser, userProperty);
        }
    }

}
