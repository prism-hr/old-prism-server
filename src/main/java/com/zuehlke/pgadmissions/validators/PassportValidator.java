package com.zuehlke.pgadmissions.validators;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.Passport;

@Component
public class PassportValidator extends AbstractValidator {

    @Override
    public void validate(final Object target, final Errors errors) {
        Passport passportInformation = (Passport) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "number", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "issueDate", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "expiryDate", EMPTY_FIELD_ERROR_MESSAGE);

        if (passportInformation != null) {
            LocalDate passportExpiryDate = passportInformation.getExpiryDate();
            LocalDate passportIssueDate = passportInformation.getIssueDate();

            if (passportExpiryDate != null) {
                if (passportExpiryDate.isBefore(new LocalDate())) {
                    errors.rejectValue("expiryDate", "date.field.notfuture");
                }
            }

            if (passportIssueDate != null) {
                if (passportIssueDate.isAfter(new LocalDate())) {
                    errors.rejectValue("issueDate", "date.field.notpast");
                }
            }

            if (passportExpiryDate != null && passportIssueDate != null) {
                if (passportExpiryDate.equals(passportIssueDate)) {
                    errors.rejectValue("expiryDate", "date.field.same");
                    errors.rejectValue("issueDate", "date.field.same");
                }
            }
        }
    }

    @Override
    protected void addExtraValidation(Object target, Errors errors) {
    }
}
