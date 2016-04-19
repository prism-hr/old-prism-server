package uk.co.alumeni.prism.workflow.user;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.document.Document;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.services.UserService;

@Component
public class DocumentReassignmentProcessor implements PrismUserReassignmentProcessor {

    @Inject
    private UserService userService;

    @Override
    public void reassign(User oldUser, User newUser, String userProperty) {
        for (Document oldDocument : oldUser.getDocuments()) {
            if (!oldDocument.isResourceUserAssignmentProperty()) {
                userService.mergeUserAssignmentStrict(oldDocument, newUser, userProperty);
            }
        }
    }

}
