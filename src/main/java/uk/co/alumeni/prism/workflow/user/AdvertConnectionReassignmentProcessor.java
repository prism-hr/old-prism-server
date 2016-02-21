package uk.co.alumeni.prism.workflow.user;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.advert.AdvertTarget;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.services.UserService;

@Component
public class AdvertConnectionReassignmentProcessor implements PrismUserReassignmentProcessor {

    @Inject
    private UserService userService;

    @Override
    public void reassign(User oldUser, User newUser, String userProperty) {
        for (AdvertTarget oldAdvertTarget : oldUser.getAdvertTargets()) {
            userService.mergeUserAssignmentStrict(oldAdvertTarget, newUser, userProperty);
        }

        for (AdvertTarget oldAdvertTargetTarget : oldUser.getAdvertTargetsTarget()) {
            userService.mergeUserAssignmentStrict(oldAdvertTargetTarget, newUser, userProperty);
        }

        for (AdvertTarget oldAdvertTargetAccepting : oldUser.getAdvertTargetsAccept()) {
            userService.mergeUserAssignmentStrict(oldAdvertTargetAccepting, newUser, userProperty);
        }
    }

}
