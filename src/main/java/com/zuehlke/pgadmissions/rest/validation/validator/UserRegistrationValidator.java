package com.zuehlke.pgadmissions.rest.validation.validator;

import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.exceptions.PrismForbiddenException;
import com.zuehlke.pgadmissions.rest.dto.user.UserRegistrationDTO;
import com.zuehlke.pgadmissions.services.UserService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.inject.Inject;

@Component
public class UserRegistrationValidator extends LocalValidatorFactoryBean implements Validator {

    @Inject
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
                throw new PrismForbiddenException("Activation codes do not match!");
            }
        } else {
            if (userWithSameEmail != null) {
                errors.rejectValue("email", PrismDisplayPropertyDefinition.SYSTEM_VALIDATION_EMAIL_ALREADY_IN_USE.name());
            }
        }

    }
}
