package com.zuehlke.pgadmissions.rest.validation.validator;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.rest.dto.UserRegistrationDTO;
import com.zuehlke.pgadmissions.services.UserService;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

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
        String userEmail = registrationDTO.getEmail();

        if (!StringUtils.isBlank(userEmail)) {
            User userWithSameEmail = userService.getUserByEmail(userEmail);
            if (userWithSameEmail != null) {
                errors.rejectValue("email", "alreadyExists");
            }
        }
    }
}
