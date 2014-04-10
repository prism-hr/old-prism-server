package com.zuehlke.pgadmissions.validators;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

@Component
public class UserValidator extends AbstractValidator {

    private static final int MINIMUM_PASSWORD_CHARACTERS = 8;
    private static final int MAXIMUM_PASSWORD_CHARACTERS = 15;

    private UserService userService;
    private final EncryptionUtils encryptionUtils;

    public UserValidator() {
        this(null, null);
    }

    @Autowired
    public UserValidator(UserService userService, EncryptionUtils encryptionUtils) {
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
        UserAccount updatedAccount = updatedUser.getAccount();
        User existingUser = getCurrentUser();
        UserAccount existingAccount = existingUser.getAccount();

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", EMPTY_FIELD_ERROR_MESSAGE);

        boolean emptyPassword = StringUtils.isEmpty(updatedAccount.getPassword());
        boolean emptyNewPassword = StringUtils.isEmpty(updatedAccount.getNewPassword());
        boolean emptyConfirmPassword = StringUtils.isEmpty(updatedAccount.getConfirmPassword());

        errors.pushNestedPath("account");
        if (emptyPassword && (!emptyNewPassword || !emptyConfirmPassword)) {
            errors.rejectValue("password", EMPTY_FIELD_ERROR_MESSAGE);
        }

        if (emptyConfirmPassword && (!emptyNewPassword || !emptyPassword)) {
            errors.rejectValue("confirmPassword", EMPTY_FIELD_ERROR_MESSAGE);
        }

        if (emptyNewPassword && (!emptyConfirmPassword || !emptyPassword)) {
            errors.rejectValue("newPassword", EMPTY_FIELD_ERROR_MESSAGE);
        }

        boolean passwordFieldsFilled = !(emptyPassword || emptyNewPassword || emptyConfirmPassword);

        if (passwordFieldsFilled && !encryptionUtils.getMD5Hash(updatedAccount.getPassword()).equals(existingAccount.getPassword())) {
            errors.rejectValue("password", "account.currentpassword.notmatch");
        }

        if (passwordFieldsFilled && !updatedAccount.getConfirmPassword().equals(updatedAccount.getNewPassword())) {
            errors.rejectValue("newPassword", "user.passwords.notmatch");
            errors.rejectValue("confirmPassword", "user.passwords.notmatch");
        }

        if (passwordFieldsFilled && updatedAccount.getNewPassword().length() < MINIMUM_PASSWORD_CHARACTERS) {
            errors.rejectValue("newPassword", "user.password.small");
        }

        if (passwordFieldsFilled && updatedAccount.getNewPassword().length() > MAXIMUM_PASSWORD_CHARACTERS) {
            errors.rejectValue("newPassword", "user.password.large");
        }
        errors.popNestedPath();

        if (StringUtils.isBlank(updatedUser.getEmail())) {
            errors.rejectValue("email", EMPTY_FIELD_ERROR_MESSAGE);
        } else if (!EmailValidator.getInstance().isValid(updatedUser.getEmail())) {
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
