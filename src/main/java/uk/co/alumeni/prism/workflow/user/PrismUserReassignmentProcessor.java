package uk.co.alumeni.prism.workflow.user;

import uk.co.alumeni.prism.domain.user.User;

public interface PrismUserReassignmentProcessor {

    void reassign(User oldUser, User newUser, String userProperty);

}
