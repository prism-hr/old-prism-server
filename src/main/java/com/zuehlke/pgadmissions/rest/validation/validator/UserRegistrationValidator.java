package com.zuehlke.pgadmissions.rest.validation.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.rest.dto.UserRegistrationDTO;
import com.zuehlke.pgadmissions.services.UserService;

@Component
public class UserRegistrationValidator extends LocalValidatorFactoryBean implements Validator {

    @Autowired
    private UserService userService;

    @Override
    public boolean supports(Class<?> clazz) {
        return UserRegistrationDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors, Object... validationHints) {
        super.validate(target, errors, validationHints);
        UserRegistrationDTO registrationDTO = (UserRegistrationDTO) target;

        User userWithSameEmail = userService.getUserByEmail(registrationDTO.getEmail());

        String activationCode = registrationDTO.getActivationCode();
        if (activationCode != null) {
            if (!activationCode.equals(userWithSameEmail.getActivationCode())) {
                errors.rejectValue("activationCode", "incorrect");
            }
        } else {
            if (userWithSameEmail != null) {
                errors.rejectValue("email", "alreadyExists");
            }
        }

    }
}
