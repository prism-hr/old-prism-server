package com.zuehlke.pgadmissions.validators;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

@Component
public class AccountValidator extends AbstractValidator {

    private static final int MINIMUM_PASSWORD_CHARACTERS = 8;
    private static final int MAXIMUM_PASSWORD_CHARACTERS = 15;

    private UserService userService;
    private final EncryptionUtils encryptionUtils;

    public AccountValidator() {
        this(null, null);
    }

    @Autowired
    public AccountValidator(UserService userService, EncryptionUtils encryptionUtils) {
        this.userService = userService;
        this.encryptionUtils = encryptionUtils;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {
        User updatedUser = (User) target;
        User existingUser = getCurrentUser();
        
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", EMPTY_FIELD_ERROR_MESSAGE);
        
        if (StringUtils.isBlank(updatedUser.getPassword()) && (StringUtils.isNotBlank(updatedUser.getNewPassword()) || StringUtils.isNotBlank(updatedUser.getConfirmPassword()))) {
            errors.rejectValue("password", EMPTY_FIELD_ERROR_MESSAGE);
        }

        if (StringUtils.isBlank(updatedUser.getConfirmPassword()) && (StringUtils.isNotBlank(updatedUser.getNewPassword()) || StringUtils.isNotBlank(updatedUser.getPassword()))) {
            errors.rejectValue("confirmPassword", EMPTY_FIELD_ERROR_MESSAGE);
        }

        if (StringUtils.isBlank(updatedUser.getNewPassword()) && (StringUtils.isNotBlank(updatedUser.getConfirmPassword()) || StringUtils.isNotBlank(updatedUser.getPassword()))) {
            errors.rejectValue("newPassword", EMPTY_FIELD_ERROR_MESSAGE);
        }

        boolean passwordFieldsFilled = StringUtils.isNotBlank(updatedUser.getConfirmPassword())
                && StringUtils.isNotBlank(updatedUser.getNewPassword())
                && StringUtils.isNotBlank(updatedUser.getPassword());

        if (passwordFieldsFilled && !encryptionUtils.getMD5Hash(updatedUser.getPassword()).equals(existingUser.getPassword())) {
            errors.rejectValue("password", "account.currentpassword.notmatch");
        }
        
        if (passwordFieldsFilled && !updatedUser.getConfirmPassword().equals(updatedUser.getNewPassword())) {
            errors.rejectValue("newPassword", "user.passwords.notmatch");
            errors.rejectValue("confirmPassword", "user.passwords.notmatch");
        }

        if (passwordFieldsFilled && updatedUser.getNewPassword().length() < MINIMUM_PASSWORD_CHARACTERS) {
            errors.rejectValue("newPassword", "user.password.small");
        }

        if (passwordFieldsFilled && updatedUser.getNewPassword().length() > MAXIMUM_PASSWORD_CHARACTERS) {
            errors.rejectValue("newPassword", "user.password.large");
        }

        if(StringUtils.isBlank(updatedUser.getEmail())){
            errors.rejectValue("email", EMPTY_FIELD_ERROR_MESSAGE);
        }else if (!EmailValidator.getInstance().isValid(updatedUser.getEmail())) {
            errors.rejectValue("email", "text.email.notvalid");
        } else {
            User userWithSameEmail = userService.getUserByEmailIncludingDisabledAccounts(updatedUser.getEmail());
            if (userWithSameEmail != null && !userWithSameEmail.getId().equals(existingUser.getId())) {
                errors.rejectValue("email", "user.email.alreadyexists");
            }
        }
    }  

    public User getCurrentUser() {
        return userService.getCurrentUser();
    }
}
