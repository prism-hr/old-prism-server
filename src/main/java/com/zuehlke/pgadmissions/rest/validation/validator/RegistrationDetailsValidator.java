package com.zuehlke.pgadmissions.rest.validation.validator;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.rest.dto.UserRegistrationDTO;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.AbstractValidator;

@Component
public class RegistrationDetailsValidator extends AbstractValidator {

    @Autowired
    private UserService userService;

    @Override
    public boolean supports(Class<?> clazz) {
        return UserRegistrationDTO.class.equals(clazz);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {
        UserRegistrationDTO user = (UserRegistrationDTO) target;

        if (!StringUtils.isBlank(user.getEmail())) {
            User userWithSameEmail = userService.getUserByEmailIncludingDisabledAccounts(user.getEmail());
            if (userWithSameEmail != null) {
                errors.rejectValue("email", "alreadyExists");
            }
        }
    }
}