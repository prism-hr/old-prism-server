package com.zuehlke.pgadmissions.validators;

import java.util.Date;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.Passport;
import com.zuehlke.pgadmissions.utils.DateUtils;

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
            Date passportExpiryDate = passportInformation.getExpiryDate();
            Date passportIssueDate = passportInformation.getIssueDate();
            
            if (passportExpiryDate != null) {
                if (!DateUtils.isToday(passportExpiryDate) && passportExpiryDate.before(new Date())) {
                    errors.rejectValue("expiryDate", "date.field.notfuture");
                }
            }
            
            if (passportIssueDate != null) {
                if (!DateUtils.isToday(passportIssueDate) && passportIssueDate.after(new Date())) {
                    errors.rejectValue("issueDate", "date.field.notpast");
                }
            }
            
            if (passportExpiryDate != null && passportIssueDate != null) {
                if (org.apache.commons.lang.time.DateUtils.isSameDay(passportExpiryDate, passportIssueDate)) {
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
