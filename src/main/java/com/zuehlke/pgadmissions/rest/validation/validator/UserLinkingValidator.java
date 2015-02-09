package com.zuehlke.pgadmissions.rest.validation.validator;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.rest.dto.user.UserLinkingDTO;
import com.zuehlke.pgadmissions.security.UserAuthenticationService;
import com.zuehlke.pgadmissions.services.UserService;

@Component
public class UserLinkingValidator extends LocalValidatorFactoryBean implements Validator {

    @Autowired
    private UserService userService;

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Override
    public boolean supports(Class<?> clazz) {
        return UserLinkingDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors, Object... validationHints) {
        super.validate(target, errors, validationHints);
        UserLinkingDTO userLinkingDTO = (UserLinkingDTO) target;

        User currentUser = userService.getCurrentUser();
        User otherUser = userService.getUserByEmail(userLinkingDTO.getOtherEmail());

        if (!userAuthenticationService.validateCredentials(currentUser, userLinkingDTO.getCurrentPassword())) {
            errors.rejectValue("currentPassword", "badCredentials");
        } else if (otherUser == null) {
            errors.rejectValue("otherEmail", "badCredentials");
        } else {
            boolean alreadyLinked = false;
            Set<User> linkedUsers = currentUser.getChildUsers();
            for (User linkedUser : linkedUsers) {
                if (otherUser.getEmail().equals(linkedUser.getEmail())) {
                    alreadyLinked = true;
                }
            }
            if (alreadyLinked) {
                errors.rejectValue("otherEmail", "alreadyLinked");
            } else if (!otherUser.isEnabled()) {
                errors.rejectValue("otherEmail", "notActivated");
            } else if (!userAuthenticationService.validateCredentials(otherUser, userLinkingDTO.getOtherPassword())) {
                errors.rejectValue("otherEmail", "badCredentials");
            }
        }
    }
}
