package com.zuehlke.pgadmissions.workflow.user;

import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.getProperty;

import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserConnection;
import com.zuehlke.pgadmissions.services.UserService;

@Component
public class UserConnectionReassignmentProcessor implements PrismUserReassignmentProcessor {

    @Inject
    private UserService userService;

    @Override
    @SuppressWarnings("unchecked")
    public void reassign(User oldUser, User newUser, String userProperty) throws Exception {
        String collectionProperty = StringUtils.replace(userProperty, "user", "").toLowerCase() + "Connections";
        for (UserConnection oldUserConnection : (Set<UserConnection>) getProperty(oldUser, collectionProperty)) {
            userService.mergeUserAssignmentStrict(oldUserConnection, newUser, userProperty);
        }
    }

}
