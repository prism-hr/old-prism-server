package com.zuehlke.pgadmissions.workflow.user;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserProgram;
import com.zuehlke.pgadmissions.services.UserService;

@Component
public class UserProgramReassignmentProcessor implements PrismUserReassignmentProcessor {

    @Inject
    private UserService userService;

    @Override
    public void reassign(User oldUser, User newUser, String userProperty) throws Exception {
        for (UserProgram oldUserProgram : oldUser.getUserPrograms()) {
            userService.mergeUserAssignmentStrict(oldUserProgram, newUser, userProperty);
        }
    }

}
