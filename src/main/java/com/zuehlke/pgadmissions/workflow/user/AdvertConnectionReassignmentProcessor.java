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
        for (AdvertConnection oldUserAdvertTargetAdvert : oldUser.getInvitedConnections()) {
            userService.mergeUserAssignmentStrict(oldUserAdvertTargetAdvert, newUser, userProperty);
        }

        for (AdvertConnection oldUserAdvertTargetAdvert : oldUser.getReceivedConnections()) {
            userService.mergeUserAssignmentStrict(oldUserAdvertTargetAdvert, newUser, userProperty);
        }
    }

}
