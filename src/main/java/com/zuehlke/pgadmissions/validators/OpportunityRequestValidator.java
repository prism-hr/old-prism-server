package com.zuehlke.pgadmissions.validators;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.OpportunityRequest;

@Component
public class OpportunityRequestValidator extends AbstractValidator {

    @Autowired
    private RegisterFormValidator registerFormValidator;

    @Override
    public boolean supports(Class<?> clazz) {
        return OpportunityRequest.class.equals(clazz);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "institutionCountry", EMPTY_DROPDOWN_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "institutionCode", EMPTY_FIELD_ERROR_MESSAGE);

        OpportunityRequest opportunityRequest = (OpportunityRequest) target;

        String institutionCode = opportunityRequest.getInstitutionCode();
        if (StringUtils.equalsIgnoreCase("OTHER", institutionCode)) {
            if (StringUtils.isBlank(opportunityRequest.getOtherInstitution())) {
                errors.rejectValue("otherInstitution", EMPTY_FIELD_ERROR_MESSAGE);
            }
        }

        errors.pushNestedPath("author");
        ValidationUtils.invokeValidator(registerFormValidator, opportunityRequest.getAuthor(), errors);
        errors.popNestedPath();
    }
}
