package com.zuehlke.pgadmissions.workflow.user;

import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserProgram;
import com.zuehlke.pgadmissions.services.UserService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class UserProgramReassignmentProcessor implements PrismUserReassignmentProcessor {

    @Inject
    private UserService userService;

    @Override
    public void reassign(User oldUser, User newUser, String userProperty) {
        for (UserProgram oldUserProgram : oldUser.getUserPrograms()) {
            userService.mergeUserAssignmentStrict(oldUserProgram, newUser, userProperty);
        }
    }

}
