package com.zuehlke.pgadmissions.validators;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.dto.RefereesAdminEditDTO;
import com.zuehlke.pgadmissions.services.UserService;

@Component
public class RefereesAdminEditDTOValidator extends AbstractValidator {

    private final UserService userService;

    @Autowired
    public RefereesAdminEditDTOValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return RefereesAdminEditDTO.class.equals(clazz);
    }

    @Override
    protected void addExtraValidation(Object target, Errors errors) {
        RefereesAdminEditDTO dto = (RefereesAdminEditDTO) target;

        // validate reference
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "comment", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmpty(errors, "suitableForUCL", EMPTY_DROPDOWN_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmpty(errors, "suitableForProgramme", EMPTY_DROPDOWN_ERROR_MESSAGE);

        // validate referee
        if (BooleanUtils.isTrue(dto.getContainsRefereeData())) {
            if (userService.getCurrentUser().getEmail().equals(dto.getEmail())) {
                errors.rejectValue("email", "text.email.notyourself");
            }
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", EMPTY_FIELD_ERROR_MESSAGE);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "addressLocation", EMPTY_FIELD_ERROR_MESSAGE);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jobEmployer", EMPTY_FIELD_ERROR_MESSAGE);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jobTitle", EMPTY_FIELD_ERROR_MESSAGE);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phoneNumber", EMPTY_FIELD_ERROR_MESSAGE);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstname", EMPTY_FIELD_ERROR_MESSAGE);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastname", EMPTY_FIELD_ERROR_MESSAGE);

            if (dto.getAddressLocation() != null && StringUtils.isBlank(dto.getAddressLocation().getAddress1())) {
                errors.rejectValue("addressLocation.address1", EMPTY_FIELD_ERROR_MESSAGE);
            }
            if (dto.getAddressLocation() != null && StringUtils.isBlank(dto.getAddressLocation().getAddress3())) {
                errors.rejectValue("addressLocation.address3", EMPTY_FIELD_ERROR_MESSAGE);
            }
            if (dto.getAddressLocation() != null && dto.getAddressLocation().getCountry() == null) {
                errors.rejectValue("addressLocation.country", EMPTY_FIELD_ERROR_MESSAGE);
            }
        }
    }
}
