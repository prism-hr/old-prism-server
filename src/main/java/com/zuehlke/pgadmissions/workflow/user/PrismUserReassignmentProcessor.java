package com.zuehlke.pgadmissions.workflow.user;

import com.zuehlke.pgadmissions.domain.user.User;

public interface PrismUserReassignmentProcessor {

    void reassign(User oldUser, User newUser, String userProperty);

}
