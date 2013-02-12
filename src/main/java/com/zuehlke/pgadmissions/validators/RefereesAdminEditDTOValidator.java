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
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "comment", "text.field.empty");
        ValidationUtils.rejectIfEmpty(errors, "suitableForUCL", "dropdown.radio.select.none");
        ValidationUtils.rejectIfEmpty(errors, "suitableForProgramme", "dropdown.radio.select.none");

        // validate referee
        if (BooleanUtils.isTrue(dto.getContainsRefereeData())) {
            if (userService.getCurrentUser().getEmail().equals(dto.getEmail())) {
                errors.rejectValue("email", "text.email.notyourself");
            }
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "text.field.empty");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "addressLocation", "text.field.empty");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jobEmployer", "text.field.empty");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jobTitle", "text.field.empty");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phoneNumber", "text.field.empty");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstname", "text.field.empty");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastname", "text.field.empty");

            if (dto.getAddressLocation() != null && StringUtils.isBlank(dto.getAddressLocation().getAddress1())) {
                errors.rejectValue("addressLocation.address1", "text.field.empty");
            }
            if (dto.getAddressLocation() != null && StringUtils.isBlank(dto.getAddressLocation().getAddress3())) {
                errors.rejectValue("addressLocation.address3", "text.field.empty");
            }
            if (dto.getAddressLocation() != null && dto.getAddressLocation().getCountry() == null) {
                errors.rejectValue("addressLocation.country", "text.field.empty");
            }
        }
    }
}
