package com.zuehlke.pgadmissions.services.helpers.persisters;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserAccount;
import com.zuehlke.pgadmissions.services.UserService;

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
