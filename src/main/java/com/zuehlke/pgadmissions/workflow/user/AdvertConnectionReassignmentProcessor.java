package com.zuehlke.pgadmissions.workflow.user;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.advert.AdvertConnection;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.services.UserService;

@Component
public class AdvertConnectionReassignmentProcessor implements PrismUserReassignmentProcessor {

    @Inject
    private UserService userService;

    @Override
    public void reassign(User oldUser, User newUser, String userProperty) {
        for (AdvertConnection oldAdvertConnection : oldUser.getAdvertConnections()) {
            userService.mergeUserAssignmentStrict(oldAdvertConnection, newUser, userProperty);
        }

        for (AdvertConnection oldAdvertTargetConnection : oldUser.getAdvertTargetConnections()) {
            userService.mergeUserAssignmentStrict(oldAdvertTargetConnection, newUser, userProperty);
        }

        for (AdvertConnection oldAdvertAcceptingConnection : oldUser.getAdvertAcceptingConnections()) {
            userService.mergeUserAssignmentStrict(oldAdvertAcceptingConnection, newUser, userProperty);
        }
    }

}
