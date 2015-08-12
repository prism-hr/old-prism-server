package com.zuehlke.pgadmissions.workflow.user;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserCompetence;
import com.zuehlke.pgadmissions.services.UserService;

@Component
public class UserCompetenceReassignmentProcessor implements PrismUserReassignmentProcessor {

    @Inject
    private UserService userService;

    @Override
    public void reassign(User oldUser, User newUser, String userProperty) throws Exception {
        for (UserCompetence oldUserCompetence : oldUser.getUserCompetences()) {
            userService.mergeUserAssignmentStrict(oldUserCompetence, newUser, userProperty);
        }
    }

}