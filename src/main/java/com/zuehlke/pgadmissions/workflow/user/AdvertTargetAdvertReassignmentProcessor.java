package com.zuehlke.pgadmissions.workflow.user;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.advert.AdvertTargetAdvert;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.services.UserService;

@Component
public class AdvertTargetAdvertReassignmentProcessor implements PrismUserReassignmentProcessor {

    @Inject
    private UserService userService;

    @Override
    public void reassign(User oldUser, User newUser, String userProperty) {
        for (AdvertTargetAdvert oldUserAdvertTargetAdvert : oldUser.getAdvertTargetAdverts()) {
            userService.mergeUserAssignmentStrict(oldUserAdvertTargetAdvert, newUser, userProperty);
        }
    }

}
