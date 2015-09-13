package com.zuehlke.pgadmissions.workflow.user;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.services.UserService;

@Component
public class AdvertReassignmentProcessor implements PrismUserReassignmentProcessor {

    @Inject
    private UserService userService;

    @Override
    public void reassign(User oldUser, User newUser, String userProperty) {
        for (Advert oldAdvert : oldUser.getAdverts()) {
            if (!oldAdvert.isResourceUserAssignmentProperty()) {
                userService.mergeUserAssignmentStrict(oldAdvert, newUser, userProperty);
            }
        }
    }

}
