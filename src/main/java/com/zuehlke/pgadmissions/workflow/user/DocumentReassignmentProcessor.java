package com.zuehlke.pgadmissions.workflow.user;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.services.UserService;

@Component
public class DocumentReassignmentProcessor implements PrismUserReassignmentProcessor {

    @Inject
    private UserService userService;

    @Override
    public void reassign(User oldUser, User newUser, String userProperty) throws Exception {
        for (Document oldDocument : oldUser.getDocuments()) {
            if (!oldDocument.isResourceUserAssignmentProperty()) {
                userService.mergeUserAssignmentStrict(oldDocument, newUser, userProperty);
            }
        }
    }

}
