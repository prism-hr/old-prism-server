package com.zuehlke.pgadmissions.rest.validation.validator;

import java.util.Set;

import javax.inject.Inject;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.rest.dto.user.UserLinkingDTO;
import com.zuehlke.pgadmissions.security.UserAuthenticationService;
import com.zuehlke.pgadmissions.services.UserService;

@Component
public class UserLinkingValidator extends LocalValidatorFactoryBean implements Validator {

    @Inject
    private UserService userService;

    @Inject
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
            errors.rejectValue("currentPassword", PrismDisplayPropertyDefinition.SYSTEM_VALIDATION_INVALID_PASSWORD.name());
        } else if (otherUser == null) {
            errors.rejectValue("otherEmail", PrismDisplayPropertyDefinition.SYSTEM_VALIDATION_BAD_CREDENTIALS.name());
        } else {
            boolean alreadyLinked = false;
            Set<User> linkedUsers = currentUser.getChildUsers();
            for (User linkedUser : linkedUsers) {
                if (otherUser.getEmail().equals(linkedUser.getEmail())) {
                    alreadyLinked = true;
                }
            }
            if (alreadyLinked) {
                errors.rejectValue("otherEmail", PrismDisplayPropertyDefinition.SYSTEM_VALIDATION_USER_ALREADY_LINKED.name());
            } else if (!otherUser.isEnabled()) {
                errors.rejectValue("otherEmail", PrismDisplayPropertyDefinition.SYSTEM_VALIDATION_ACCOUNT_NOT_ACTIVATED.name());
            } else if (!userAuthenticationService.validateCredentials(otherUser, userLinkingDTO.getOtherPassword())) {
                errors.rejectValue("otherEmail", PrismDisplayPropertyDefinition.SYSTEM_VALIDATION_BAD_CREDENTIALS.name());
            }
        }
    }
}
