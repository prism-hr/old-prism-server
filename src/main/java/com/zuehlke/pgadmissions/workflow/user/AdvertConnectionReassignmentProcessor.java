package com.zuehlke.pgadmissions.workflow.user;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.advert.AdvertTarget;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.services.UserService;

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
