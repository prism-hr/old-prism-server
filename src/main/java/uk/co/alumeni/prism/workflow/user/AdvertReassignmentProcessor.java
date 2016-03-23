package uk.co.alumeni.prism.workflow.user;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.services.UserService;

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
