package uk.co.alumeni.prism.services.helpers.persisters;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.document.Document;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserAccount;
import uk.co.alumeni.prism.services.UserService;

@Component
public class UserPortraitPersister implements ImageDocumentPersister {

    @Inject
    private UserService userService;

    @Override
    public void persist(Integer userId, Document image) {
        User user = userService.getById(userId);
        UserAccount userAccount = user.getUserAccount();
        if (userAccount != null) {
            userAccount.setPortraitImage(image);
        }
    }

}
