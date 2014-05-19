package com.zuehlke.pgadmissions.validators;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.services.UserService;

@Component
public class RefereeValidator extends FormSectionObjectValidator implements Validator {

    private UserService userService;

    RefereeValidator() {
        this(null);
    }

    @Autowired
    public RefereeValidator(UserService userService) {
        this.userService = userService;

    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Referee.class.equals(clazz);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {
        super.addExtraValidation(target, errors);

        Referee referee = (Referee) target;
        if (userService.getCurrentUser().getEmail().equals(referee.getUser().getEmail())) {
            errors.rejectValue("email", "text.email.notyourself");
        }
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstname", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastname", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", EMPTY_FIELD_ERROR_MESSAGE);
        
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jobEmployer", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jobTitle", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phoneNumber", EMPTY_FIELD_ERROR_MESSAGE);

        if (referee.getAddress() != null && StringUtils.isBlank(referee.getAddress().getAddressLine1())) {
            errors.rejectValue("addressLocation.address1", EMPTY_FIELD_ERROR_MESSAGE);
        }

        if (referee.getAddress() != null && StringUtils.isBlank(referee.getAddress().getAddressTown())) {
            errors.rejectValue("addressLocation.address3", EMPTY_FIELD_ERROR_MESSAGE);
        }

        if (referee.getAddress() != null && referee.getAddress().getDomicile() == null) {
            errors.rejectValue("addressLocation.domicile", EMPTY_FIELD_ERROR_MESSAGE);
        }

        for (Referee existingReferee : referee.getApplication().getReferees()) {
            if (referee.getId() == null && StringUtils.equalsIgnoreCase(existingReferee.getUser().getEmail(), referee.getUser().getEmail())) {
                errors.rejectValue("email", "assignReferee.duplicate.email");
            }
        }
    }
}
