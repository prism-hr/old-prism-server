package com.zuehlke.pgadmissions.validators;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.google.common.collect.Lists;
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
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "programTitle", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "programDescription", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "atasRequired", EMPTY_DROPDOWN_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "applicationStartDate", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "studyOptions", EMPTY_DROPDOWN_ERROR_MESSAGE);

        OpportunityRequest opportunityRequest = (OpportunityRequest) target;

        // validate institution code / name
        String institutionCode = opportunityRequest.getInstitutionCode();
        if (StringUtils.equalsIgnoreCase("OTHER", institutionCode)) {
            if (StringUtils.isBlank(opportunityRequest.getOtherInstitution())) {
                errors.rejectValue("otherInstitution", EMPTY_FIELD_ERROR_MESSAGE);
            }
        }

        // validate study duration
        if (opportunityRequest.getStudyDurationNumber() == null) {
            errors.rejectValue("studyDurationNumber", EMPTY_FIELD_ERROR_MESSAGE);
        } else if (opportunityRequest.getStudyDurationNumber() < 1) {
            errors.rejectValue("studyDurationNumber", "Min", new Object[] { null, "0" }, null);
        } else if (opportunityRequest.getStudyDurationUnit() == null) {
            errors.rejectValue("studyDurationUnit", EMPTY_DROPDOWN_ERROR_MESSAGE);
        } else if (!Lists.newArrayList("YEARS", "MONTHS").contains(opportunityRequest.getStudyDurationUnit())) {
            errors.rejectValue("studyDurationUnit", EMPTY_DROPDOWN_ERROR_MESSAGE);
        }

        // validate advertising duration
        if (opportunityRequest.getAdvertisingDuration() == null) {
            errors.rejectValue("advertisingDuration", EMPTY_DROPDOWN_ERROR_MESSAGE);
        } else if (opportunityRequest.getAdvertisingDuration() < 1) {
            errors.rejectValue("advertisingDuration", "Min", new Object[] { null, "0" }, null);
        } else if (opportunityRequest.getAdvertisingDuration() > 5) {
            errors.rejectValue("advertisingDuration", "Max", new Object[] { null, "6" }, null);
        }

        // validate an author
        if (opportunityRequest.getAuthor() != null) {
            errors.pushNestedPath("author");
            ValidationUtils.invokeValidator(registerFormValidator, opportunityRequest.getAuthor(), errors);
            errors.popNestedPath();
        }
    }

    void setRegisterFormValidator(RegisterFormValidator registerFormValidator) {
        this.registerFormValidator = registerFormValidator;
    }
}
