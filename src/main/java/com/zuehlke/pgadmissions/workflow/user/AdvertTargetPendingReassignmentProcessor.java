package com.zuehlke.pgadmissions.workflow.user;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.advert.AdvertTargetPending;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.services.UserService;

@Component
public class AdvertTargetPendingReassignmentProcessor implements PrismUserReassignmentProcessor {

    @Inject
    private UserService userService;

    @Override
    public void reassign(User oldUser, User newUser, String userProperty) {
        for (AdvertTargetPending oldAdvertTargetPending : oldUser.getAdvertTargetPendings()) {
            userService.mergeUserAssignmentStrict(oldAdvertTargetPending, newUser, userProperty);
        }
    }

}
